package br.usjt.aepn2012.cardroiduino.ui;

import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> Implementa um Objeto FrameView que herda de SurfaceView para Gerar dentro dele 
 * a própria Thread que vai capturar os Frames do Core e Mostrar pro usuário 
 * <p> 
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
public class FrameView extends SurfaceView implements SurfaceHolder.Callback {
	class FrameThread extends Thread{
		
		/**
		 * Holder para Captura do Canvas
		 */
		private SurfaceHolder surfaceHolder;
		
		/**
		 * Core do Sistema
		 */
		private CarDroiDuinoCore systemCore;
		
		/**
		 * Controle do looping Thread
		 */
		private boolean isOn = false;
		
		/**
		 * 
		 * @param surfaceHolder
		 * @param systemCore
		 */
		public FrameThread(SurfaceHolder surfaceHolder){
			this.surfaceHolder = surfaceHolder;
		}

		/**
		 * Informa o Core para a Thread ter acesso a Fila de Frames que 
		 * chegam via TCP/IP
		 * @param systemCore
		 */
		public void setSystemCore(CarDroiDuinoCore systemCore){
			this.systemCore = systemCore;
		}
		
		/**
		 * Informa o Core do Sistema Setado para a Thread
		 * @return
		 */
		public CarDroiDuinoCore getSystemCore(){
			return this.systemCore;
		}
		
		/**
		 * habilitar/desabilitar o looping da thread
		 * @param onOff
		 */
		public void setOnOff(boolean onOff){
			this.isOn = onOff;
		}
		
		/**
		 * Aqui comeca a rodar a thread que Captura o Frame na Fila de Chegada
		 * e o Desenha na Surface para Mostrar a imagem ao usuário
		 */
		@Override
		public void run() {
			while (this.isOn) {
				
				//*************************************************
				// Captura o Frame da Fila do Core
				byte[] jpgFrame = this.systemCore.poolDataFromCameraQueue();
				
				if (jpgFrame != null){
	                Canvas c = null;
	                try {
	                	//*****************************************
	                	// Tenta Capturar o Canvas para desenhar
	                    c = this.surfaceHolder.lockCanvas(null);
	                    synchronized (this.surfaceHolder) {
	                        doDraw(c, jpgFrame);
	                    }
	                } finally {
	                    //*********************************************
	                	// Devolve o Canvas a Surface - se der exception
	                	// não deixa a surface num estado incosistente
	                    if (c != null) {
	                    	this.surfaceHolder.unlockCanvasAndPost(c);
	                    }
	                }
				}
            }
		}
		
		/**
		 * Coloca o Frame capturado do Core na Surface para mostrar ao usuário
		 * Converte o Frame jpeg para bitmap
		 * Limpa o Canvas
		 * Desenha o Frame no Canvas
		 * @param canvas
		 */
		private void doDraw(Canvas canvas, byte[] jpgFrame){
			//*************************************************
	    	//Decodificando imagem compactada de JPGE para um Objeto Bitmap (mapa de bits)
			Bitmap bitMapImg = BitmapFactory.decodeByteArray(jpgFrame, 0, jpgFrame.length);
			
			//*************************************************
			// Se conseguiu pegar a área de desenho então Limpa a área 
			// para Desenha a imagem sem sugeiras
			canvas.drawARGB(255, 0, 0, 0);
			
			//*************************************************
			// Desenhando a Imagem na área
			canvas.drawBitmap(bitMapImg, null, canvas.getClipBounds(), null);
		}
		
	}
	
	/**
	 * Flag para saber se a Surface está pronta para iniciar a Thread 
	 * de Captura de imagem
	 */
	private boolean surfaceRead;
	
	/**
	 * Thread Implementada acima - Desenha os Frames
	 * Capturados do Core na surface
	 */
	private FrameThread frameThred;
	
	/**
	 * Cria Instancia do FrameView e incializa a Thread para desenho
	 * dos Frames capturados
	 * @param context
	 * @param attrs
	 */
	public FrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
		//***********************************************
		//Instanciando a Thread de Update da Imagem
		this.frameThred = new FrameThread(getHolder());
	}
	
	/**
	 * Evento obrigado a implementar pela Interface - Vazio por enquanto
	 */
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	/**
	 * Evento executado quando a Surface foi completamente criada
	 * Tenta iniciar a Thread se já houver sido informado o Core do Sistema
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		//***********************************************
		//Pode Inicializar aqui a Thread se Tive recebido 
		//o Core do Sistema na StartImageDrawer
		if (this.frameThred.getSystemCore() != null){
			this.frameThred.setOnOff(true);
			//TODO: Descomentar
			this.frameThred.start();
		}
		this.surfaceRead = true;
	}

	/**
	 * Tenta Matar a thread a todo custo
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		this.frameThred.setOnOff(false);
		while(retry){
			try {
				this.frameThred.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * Informa se a Surface está 100% para iniciar
	 * @return
	 */
	public boolean isSurfaceRead(){
		return this.surfaceRead;
	}
		
	/**
	 * Informa o Core do Sistema para a Thread e Tenta iniciar
	 * a thread que monta a imagem na Surface
	 * @param systemCore
	 */
	public void startImageDrawer(CarDroiDuinoCore systemCore){
		//****************************************************
		// Informa o Core do Sistema para Thread
		this.frameThred.setSystemCore(systemCore);
		//****************************************************
		//Pode iniciar a Thread aqui se já construiu a surface
		if (this.isSurfaceRead()){
			this.frameThred.setOnOff(true);
			//TODO: Descomentar
			this.frameThred.start();
		}
	}
	
}
