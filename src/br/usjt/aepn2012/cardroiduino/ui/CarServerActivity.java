package br.usjt.aepn2012.cardroiduino.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
//import java.util.Vector;

import br.usjt.aepn2012.cardroiduino.R;
import br.usjt.aepn2012.cardroiduino.carserver.BluetoothGate;
import br.usjt.aepn2012.cardroiduino.carserver.DatagramSocketServerGate;
import br.usjt.aepn2012.cardroiduino.carserver.InternalCommandServerGate;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.ScrollView;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Tela do Módulo Servidor do Sistema -
 * Tela que Será executada no Dispositivo acoplado ao Carrinho a ser controlado
 * Possui:
 * 	- Interface com a Câmera do Dispositivo Android
 * 	- Conexão Socket Para Recebimento e Envio de Dados ao Client (Controle Remoto)
 * 	- Interface Bluetooth com o Arduino Controlador do Carrinho 
 * 	- Inicialização do Resolvedor de Comandos Internos
 * 
 * </p>
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/01/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class CarServerActivity extends Activity implements SurfaceHolder.Callback {
	
	/**
	 * Endereco IP do Controle Remoto do Carrinho
	 */
	private String clientIPAddress;
	
	/**
	 * Porta Disponibilizada pelo Server para Recebimento dos Comandos
	 * Mesmo numero de porta disponibilizado pelo Client para envio dos Frames
	 */
	private String clientServerPort;
	
	/**
	 * MAC address do Modem Bluetooth do Arduino
	 */
	private String modemBluetoothMACAddress;
	
	/**
	 * Core para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Gate para Comunicação TCP/IP com o Controle Remoto
	 */
	private DatagramSocketServerGate socketServerGate;
	
	/**
	 * Gate para Comunicação com o Módulo Bluetooth
	 */
	private BluetoothGate bluetoothGate;
	
	/**
	 * Gate para processamento de Comandos Internos do Dispositivo Android
	 */
	private InternalCommandServerGate internalCommandServerGate;
	
	/**
	 * Câmera do dispositivo móvel
	 */
	private Camera mCamera;
	
	/**
	 * Prompt para exibir Status
	 */
	private EditText mTxtPrompt;
	
	/**
	 * Scroll para o Promt
	 */
	private ScrollView mScrPrompt;
	
	/**
	 * Surperficie para exibição da imagem (Preview) da câmera
	 */
	private SurfaceView surfaceView;
	
	/**
	 * Recipiente da Superficie que conterá a imagem da câmera
	 */
	private SurfaceHolder surfaceHolder;
	
	/**
	 * Verificar se a Câmera está aberta obtendo os Frames
	 */
	private boolean isPreviewRunning = false; 
	
	/**
	 * Verificar se as Threads já foram inicializadas
	 */
	private boolean isThreadsInitialided = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	//*************************************************
    	// Executa primeiro o método da classe herdada (classe Superior - extends)
        super.onCreate(savedInstanceState);
        
        //*************************************************
        // Configuração Padrão para captura da camera
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  
        
        //*************************************************
        // Setando qual o Layout (XML) associado a essa Activity --> CarDroiDuino\res\layout
        setContentView(R.layout.car_layout);
        
        //*************************************************
        // Obtendo a Porta a disponibilizar para a Conexao e o MAC Address do Modulo 
        // Bluetooth do Arduino - Esses dados foram enviados pela Classe CarDroiDuinoActivity
        this.clientServerPort = getIntent().getExtras().getString(SystemProperties.KEY_PORT_NUMBER);
        this.modemBluetoothMACAddress = getIntent().getExtras().getString(SystemProperties.KEY_DEVICE_ADDRESS);
        this.clientIPAddress = getIntent().getExtras().getString(SystemProperties.KEY_IP_ADDRESS);
        //*************************************************
        
        //*************************************************
        // Inicializando a Surface para Exibir o Preview da Camera
        this.surfaceView = (SurfaceView)findViewById(R.id.surface);  
        this.surfaceHolder = this.surfaceView.getHolder();  
        this.surfaceHolder.addCallback(this);  
        this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //*************************************************
        
        //*************************************************
        // Capturando o Txt e Scroll para utilizar como Prompt de Msgs
        this.mTxtPrompt = (EditText) findViewById(R.id.txtPrompt);
        this.mScrPrompt = (ScrollView) findViewById(R.id.scrPrompt);
        
        //*************************************************
        // Inicializando o Server do Carrinho
        this.setupServer();
    }
    
    /**
     * Inicia o Gate que irá executar e reter as Threads trabalhadoras de Envio dos Frames da
     * Camera, Recebimento dos Dados de Comando do carrinho e envio dos dados do comando
     * via Bluetooth para o Arduino
     */
    private void setupServer(){
    	//*************************************************
    	// Inicializa o Core do Sistema para preparar as Filas para a troca de dados entre as Threads
    	this.systemCore = new CarDroiDuinoCore();
    	try {
    		//*************************************************
			// Inicia o gate que irá Conectar via Bluetooth ao Módulo do Arduino e enviar os 
			// comandos que chegarem a Fila de Comandos do Core para Controlar o Carrinho
    		//TODO:Comentar/Descomentar aqui
			//this.bluetoothGate = new BluetoothGate(this.systemCore, this.modemBluetoothMACAddress, mHandler);
    		//*************************************************
    		// Inicia o Gate que ira criar e gerenciar as Threads de Envio de Frames da Camera e 
    		// Recebimento dos Dados de Controle Via TCP/IP
    		this.socketServerGate = new DatagramSocketServerGate(this.systemCore, this.clientIPAddress, Integer.parseInt(this.clientServerPort), this.mHandler);
    		//*************************************************
    		// GATE DE COMANDOS INTERNOS SÓ É CRIADO DEPOIS DA CAMERA SER INICIALIZADA
    		//*************************************************
    	} catch (Exception e){
			new AlertDialog.Builder(this).setMessage(e.getMessage()).show();
		}
    	
    	//************************************************
    	// Da um tempo para que o Client unicialize suas Threads
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	//*************************************************
    	// Informando que tudo foi inicializado
    	this.isThreadsInitialided = true;
    }
    
    /**
     * Manipulador para Escrever mensagens para a Thread Grafica do Sistema para
     * Visualização pelo usuário
     */
    private Handler mHandler = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		String txt = mTxtPrompt.getText().toString();
    		
    		//****************************************
    		// Se passar de 100 
    		if (mTxtPrompt.getLineCount() > 100)
    			txt = "";
    		
    		txt += "\n" + (String) msg.obj;
    		/*
    		 * Loga a mensagen Recebida no Prompt
    		 */
    		mTxtPrompt.setText(txt);
    		
    		/*
    		 * Descendo o Scroll
    		 */
    		mScrPrompt.post(new Runnable() {
				public void run() {
					mScrPrompt.smoothScrollTo(0, mTxtPrompt.getBottom());
				}
			});
    	}
    };
    
	/**
     * Evento disparado sempre que um Frame da camera for capturado pelo Preview
     * Ao chegar o Frame ele é compactado para JPG para poder ser enviado via 
     * TCP/IP ao Client do carrinho - Em seguida ele é colocado na Fila do Core
     * para a Thread Sender poder Captura-lo e envia-lo
     */
    Camera.PreviewCallback mPreviewCallBack = new Camera.PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (systemCore != null && socketServerGate != null 
					&& socketServerGate.isSocketGateInitialized() && isThreadsInitialided){
				//*************************************************
				// Compactando e Enviando o Frame ao Core do Sistema
				systemCore.addDataToCameraQueue(convertImageToJPEG(data));	
			}		
		}
	};
	
	/**
	 * Converte a Imagem do Formato do frame da câmera (NV21) para JPEG
	 * @param data Imagem no Formato Capturado pela Câmera
	 * @return Imagem em Formtato JPEG
	 */
	private byte[] convertImageToJPEG(byte[] data){
		
		//*************************************************
		// Capturando a Largura e Altura da Imagem corrente para efetuar a conversao
		Camera.Parameters parameters = mCamera.getParameters();
		int w = parameters.getPreviewSize().width;
		int h = parameters.getPreviewSize().height;

		
		//*************************************************
		// Transforma a Imagem em um Objeto do tipo YuvImage, pois esse objeto
		// possui um metodo para conveter imagens para JPEG
		YuvImage yuv_image = new YuvImage(data, parameters.getPreviewFormat(), w, h, null);
		
		//*************************************************
		// Cria o objeto de um retangulo para configurar a imagem que sera convertida
		Rect rect = new Rect(0, 0, w, h);
		
		//*************************************************
		// Cria um Objeto capaz de conter a imagem convertida e devolve-la como um Array de Bytes
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
		
		//*************************************************
		// Comprimindo a Imagem - O resultado (Frame em JPEG) será devolvido dentro
		// do outputStream
		yuv_image.compressToJpeg(rect, 30, output_stream);
		
		//*************************************************
		// Retornado a Imagem convertida 
		return output_stream.toByteArray();
	}

	/**
	 * Evento disparado quando ocorrer uma mudança na Surface, como por exemplo,
     * o giro da tela - Efetua o resto do Setup da Camera nesse metodo
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
		//*************************************************
		// Se a camera estiver executando, entao para o Preview para poder configura-la
		if (isPreviewRunning) {
			mCamera.stopPreview();
		}
		
		//*************************************************
		// Seta alguns parâmetros de configuração da Camera
		Camera.Parameters p = mCamera.getParameters();
		//*************************************************
		// AQUI CONFIGURA QUANTOS FRAMES VAMOS CAPTURAR POR SEGUNDO DA CAMERA
		p.setPreviewFrameRate(5);
		//p.setPreviewFormat(PixelFormat.JPEG);
		p.setJpegQuality(1);
		//p.setPreviewSize(480, 320);
		p.setPreviewSize(240, 160);
		//*************************************************
		// Seta os parametros configurados
		mCamera.setParameters(p);
		
		try {
			//*************************************************
			// Informa para a Camera qual a Surface ela vai usar para mostrar seu Preview
			mCamera.setPreviewDisplay(holder);
			//*************************************************
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "surfaceChanged - Exception: " + e.getMessage());
			e.printStackTrace();
		}
		//*************************************************
		// Reinicia a camera para ela voltar a capturar imagens de seu Preview
		mCamera.startPreview();
		//*************************************************
		// Seta Flag para informar que a camera esta rodando
		isPreviewRunning = true;
		//*************************************************
		// AQUI INFORMA O OBJETO QUE VAI RECEBER OS FRAMES CAPTURADOS PELA CAMERA 
		// ESSE OBJETO FOI CONFIGURADO LOGO ACIMA
		mCamera.setPreviewCallback(mPreviewCallBack);
		
		//**************************************************
		//INICIALIZA O GATE QUE GERENCIA E MANIPULA O RESOLVEDOR DE COMANDOS INTERNOS
		//**************************************************
		this.internalCommandServerGate = new InternalCommandServerGate(this.systemCore, this.mHandler, this.mCamera);
		//**************************************************
	}
	
	/**
	 * Evento disparado quando a Surface é criada. Obtem a Camera do Dispositivo
	 * Para poder manusea-la
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}
	
	/**
	 * Evento disparado quando a Surface é destruida - Para a execucao da Camera
	 * e a libera para outro Software
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		//*********************************************
		//Matando as Threads
		this.killGates();
		//*********************************************
		//Destruindo objetos
		mCamera.stopPreview();
		mCamera.setPreviewCallback(null);
		mCamera.release();
		mCamera = null;
		isPreviewRunning = false;
	}
	
	/**
	 * Mata as Threads dos Gates
	 */
	private void killGates(){
		//*********************************************
		//Matando as Threads do Server
		this.internalCommandServerGate.turnOff();
		this.socketServerGate.turnOff();
		//TODO: comentar/descomentar
		//this.bluetoothGate.turnOff();
	}
	
	/**
	 * MATA TUDOOO!!!!!!!!!!!!! 
	 */
//	@Override
//	protected void onDestroy() {
//		//*********************************************
//		//Metodo superior
//		super.onDestroy();
//		
//		this.surfaceHolder = null;
//		this.surfaceView = null;
//		
//		//*********************************************
//		//Matando as Threads
//		this.killGates();
//	}
	
//	@Override
//	protected void onPause(){
//		//*********************************************
//		//Metodo superior
//		super.onPause();
//		//*********************************************
//	}
//	
//	@Override
//	protected void onStop(){
//		//*********************************************
//		//Metodo superior
//		super.onStop();
//		//*********************************************
//	}
}
