package br.usjt.aepn2012.cardroiduino.ui;


import br.usjt.aepn2012.cardroiduino.R;
import br.usjt.aepn2012.cardroiduino.db.CarDroiDuinoDbAdapter;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
import android.app.Activity;
//import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
//import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.text.InputType;
import android.view.View;
//import android.widget.EditText;
import android.widget.ImageButton;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Tela de Inicio do Sistema CarDroiDuino - 
 * Inicia o Menu para Escolha da Finalidade do Dispositivo: Servidor do Carrinho ou Controle Remoto
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
public class CarDroidDuinoActivity extends Activity {
	
	/**
	 * Porta para Conexão - Tanto para o Controle Remoto quanto para o Servidor no Carrinho
	 */
	private String port = "5002";
	
	/**
	 * Endereço IP - Tanto para o Controle Remoto quanto para o Servidor no Carrinho
	 */
	private String ipAddress = "192.168.0.1";
	
	/**
	 * Helper para auxiliar na conexão ao SQLLite
	 */
	private CarDroiDuinoDbAdapter carDroiDuinoDbAdapter;
	
	/**
	 * MAC Address do Modem bluetooth do Carrinho
	 */
	private String modemBluetoothMACAddress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	//*************************************************
    	// Executa primeiro o método da classe herdada (classe Superior - extends)
    	super.onCreate(savedInstanceState);
        
        //*************************************************
        // Setando qual o Layout (XML) associado a essa Activity --> CarDroiDuino\res\layout
        setContentView(R.layout.principal_layout);
        
        //*************************************************
        //Iniciando a conexão ao Banco de Dados
        this.carDroiDuinoDbAdapter = new CarDroiDuinoDbAdapter(this);
        this.carDroiDuinoDbAdapter.open();
        
        //*************************************************
        //Verificando se as propriedades fora criadas
        this.verifyBasicProperties();
        
        /*
         * Botão de Abertura da Tela que executará no Carrinho
         */
        ImageButton btnOpenCarServer = (ImageButton) findViewById(R.id.btnCarro);
        btnOpenCarServer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openCarServerActivity();
			}
		});
        
        /*
         * Botão para Abertura da Tela de Controle Remoto do Carrinho
         */
        ImageButton btnOpenCarControl = (ImageButton) findViewById(R.id.btnControle);
        btnOpenCarControl.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//openCarControlActivy();
				startCarControlActivity();
			}
		});
        
        /*
         * Botão para Abertura da Tela de Propriedades do Sistema
         */
        ImageButton btnOpenProperties = (ImageButton) findViewById(R.id.btnPropriedades);
        btnOpenProperties.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openPropertiesActivity();
			}
		});
    }
	
	/**
	 * Abre a Activity do Servidor do Carrinho
	 * A Abertura está no evento onAtivityResult, pois só abre a Activity depois de 
	 * obter o MAC address do Modem bluetooth do carrinho
	 */
	private void openCarServerActivity(){
		if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
	        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	        startActivityForResult(enableIntent, SystemProperties.REQUEST_CODE_ENABLE_BLUETOOTH);
	  	} else {
	  		//this.obtainClientIPAddressAndRequestPort();
	  		openDeviceBluetoothList();
	  	}
	}
	
	/**
	 * Abre a Activity do Controle Remoto
	 */
	/*private void openCarControlActivy(){
		this.obtainServerIPAddressAndRequestPort();
	}*/
	
	/**
	 * Abre a Activity de Propriedades do Sistema
	 */
	private void openPropertiesActivity(){
		//*************************************************
		// Criando Objeto para enviar a intencao de iniciar a Tela de Propriedades
    	Intent i = new Intent(this, SystemPropertiesActivity.class);
		//*************************************************
		// Enviando ao Sistema Operacional a intencao de iniciar a Activity
		startActivity(i);
	}
	
	/**
	 * Obtem o endereço IP para Conexão do Client (Controle Remoto) ao Server
	 * e Solicita ao usuário a Porta para a conexão
	 */
   /* private void obtainServerIPAddressAndRequestPort(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Starting Control...");
		alert.setMessage("Informe o IP do Server");
		//*************************************************
		// Setando EditText na Alert Dialog para Capturar o IP
		final EditText input = new EditText(this);
		alert.setView(input);
		//*************************************************
		// Setando instrucoes para Botao de OK da Dialog
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//*************************************************
				// Pega o Edereço IP digitado do Android Servidor
				ipAddress = input.getText().toString();
				obtainServerPortAndStartCarControlActivity();
			}
		});
		
		//*************************************************
		// Setando instrucoes para Botao de Cancelar da Dialog
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//*************************************************
						//Nao faz nada - apenas fecha a Dialog sem executar algum comando
						//*************************************************
					}
				});
		//*************************************************
		// Abre a Dialog configurada acima
		alert.show();
		//*************************************************
    }*/
    
    /**
     * Obtem a Porta para a Conexão ao Servidor e inicia a Activity de
     * Controle Remoto do Carrinho
     */
    /*private void obtainServerPortAndStartCarControlActivity(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Starting Control...");
		alert.setMessage("Informe a Porta do Server");
		//*************************************************
		// Setando EditText na Alert Dialog para Capturar a Porta do Server disponivel para conexao
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		//*************************************************
		// Setando instrucoes para Botao de OK da Dialog
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//*************************************************
				// Pega a Porta digitada do Android Servidor
				port = input.getText().toString();
				startCarControlActivity();
			}
		});
		
		//*************************************************
		// Setando instrucoes para Botao de Cancelar da Dialog
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//*************************************************
						//Nao faz nada - apenas fecha a Dialog sem executar algum comando
						//*************************************************
					}
				});
		//*************************************************
		// Abre a Dialog configurada acima
		alert.show();
		//*************************************************
    }*/
    
    /**
     * Obtem a Porta do Client para o Servidor e Inicia a Activity para obter MAC 
     * address do Modem Bluetooth
     */
    /*private void obtainClientPortAndRequestBluetooth(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Starting Server...");
		alert.setMessage("Informe a Porta do Controle");
		//*************************************************
		// Setando EditText na Alert Dialog para Capturar a Porta do Server a disponibilizar para Conexao
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		alert.setView(input);
		//*************************************************
		// Setando instrucoes para Botao de OK da Dialog
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//*************************************************
				// Pega a Porta digitada do Android Servidor
				port = input.getText().toString();
				openDeviceBluetoothList();
			}
		});
		//*************************************************
		// Setando instrucoes para Botao de Cancelar da Dialog
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//*************************************************
						//Nao faz nada - apenas fecha a Dialog sem executar algum comando
						//*************************************************
					}
				});
		//*************************************************
		// Abre a Dialog configurada acima
		alert.show();
		//*************************************************
    }*/
    
    /**
     * Obtem o endereco IP do Controle Remoto para envio dos Frames via UDP
     */
    /*private void obtainClientIPAddressAndRequestPort(){
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Starting Server...");
		alert.setMessage("Informe o IP do Controle");
		//*************************************************
		// Setando EditText na Alert Dialog para Capturar o IP
		final EditText input = new EditText(this);
		alert.setView(input);
		//*************************************************
		// Setando instrucoes para Botao de OK da Dialog
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//*************************************************
				// Pega o Edereço IP digitado
				ipAddress = input.getText().toString();
				obtainClientPortAndRequestBluetooth();
			}
		});
		
		//*************************************************
		// Setando instrucoes para Botao de Cancelar da Dialog
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						//*************************************************
						//Nao faz nada - apenas fecha a Dialog sem executar algum comando
						//*************************************************
					}
				});
		//*************************************************
		// Abre a Dialog configurada acima
		alert.show();
		//*************************************************
    }*/
    
    /**
     * Abre a Lista de Dispositivos Bluetooth para obter o MAC address do Modem no Carrinho
     * O MAC address será retornado no Evento onActivityResult
     */
    private void openDeviceBluetoothList(){
    	//*************************************************
    	// Cria o Objeto Intent para mostrar a intencao de abrir a Activity
    	Intent serverIntent = new Intent(this, DeviceBluetoothListActivity.class);
    	//*************************************************
    	// Manda a intencao de abrir a Tela para o Sistema Operacional
        startActivityForResult(serverIntent, SystemProperties.REQUEST_CODE_CONNECT_BLUETOOTH);
    }
    
    /**
     * Evento disparado quando retornado o resultudo de uma Activity
     * Se retornado o MAC address do Modem bluetooth, inicializa a Activity Servidora do Carrinho
     * Se o Intent retornado for da habilitação do Bluetooth, chama novamente função para abertura 
     * do Server do Carrinho
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	//*************************************************
    	// Executa, primeiro, o metodo da Classe superior
		super.onActivityResult(requestCode, resultCode, data);
		//*************************************************
		// Verifica qual foi a Activity e qual foi o Resultado retornado pelo Intent
		switch (requestCode) {
		//*************************************************
		// Retorno da Activity que lista os bluetooth encontrados
		case SystemProperties.REQUEST_CODE_CONNECT_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				//*************************************************
				// Se for um resultado OK entao retornou o MAC Address do Dispositivo ao qual o Server vai se conectar
				this.modemBluetoothMACAddress = data.getExtras().getString(SystemProperties.KEY_DEVICE_ADDRESS);
				//*************************************************
				// Inicia a Activity do Servidor
				startCarServerActivity();
			}
			break;
		//*************************************************
		// Retorno da Intent Inidicando que o Bluetooth foi Ativado
		case SystemProperties.REQUEST_CODE_ENABLE_BLUETOOTH:
			if (resultCode == Activity.RESULT_OK) {
				//*************************************************
				// Se o Bluetooth foi ativado entao inicia a abertura da Activity do Servidor
				this.openCarServerActivity();
			}
			break;
		default:
			break;
		}
	}
    
    /**
	 * Abre a Tela de Servidor do Carrinho
	 * @param port
	 */
	private void startCarServerActivity(){
		//*************************************************
    	//Inicializa campos de IP e Porta
    	this.loadIPAddressAndPort(SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT, SystemProperties.PROPERTIE_ID_PORT_CLIENT);
		//*************************************************
		// Criando Objeto para enviar a intencao de iniciar o Servidor do Carrinho
		Intent i = new Intent(this, CarServerActivity.class);
		//*************************************************
		// Passando o Numero da Porta para o servidor enviar os Frames via UDP ao Client
		i.putExtra(SystemProperties.KEY_PORT_NUMBER, this.port);
		//*************************************************
		// Passando o MAC Address do dispositivo Bluetooth para a Activity
		i.putExtra(SystemProperties.KEY_DEVICE_ADDRESS, this.modemBluetoothMACAddress);
		//*************************************************
		// Passando o Endereco IP do Client para envio dos Frames via UDP
		i.putExtra(SystemProperties.KEY_IP_ADDRESS, this.ipAddress);
		
		// Enviando ao Sistema Operacional a intencao de iniciar a Activity
		startActivity(i);
	}

	/**
     * Abre a Tela de Controle Remoto do Carrinho
     */
    private void startCarControlActivity(){
    	//*************************************************
    	//Inicializa campos de IP e Porta
    	this.loadIPAddressAndPort(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER, SystemProperties.PROPERTIE_ID_PORT_SERVER);
    	//*************************************************
		// Criando Objeto para enviar a intencao de iniciar o Controle (Client) do Carrinho
    	Intent i = new Intent(this, CarControlActivity.class);
    	//*************************************************
    	// Passando o Endereço IP para o Client enviar os dados de comando ao Server
    	i.putExtra(SystemProperties.KEY_IP_ADDRESS, this.ipAddress);
    	//*************************************************
    	// Passando a Porta para o Client enviar os dados de Comando ao Server
		i.putExtra(SystemProperties.KEY_PORT_NUMBER, this.port);
		//*************************************************
		// Enviando ao Sistema Operacional a intencao de iniciar a Activity
		startActivity(i);
    }
    
    /**
     * Carrega o IP e a Porta a ser utilizado - Se for abrir activity server então carrega IP e Portas do Client
     * se for abrir activity do cliente então carrega IP e Portas do Server
     * @param idIPaddress
     * @param idPort
     */
    private void loadIPAddressAndPort(int idIPaddress, int idPort){
    	Cursor cursor;
    	//*************************************************
    	//Carrega o IP a ser utilizado
    	cursor = this.carDroiDuinoDbAdapter.fetchPropertie(idIPaddress);
    	startManagingCursor(cursor);
    	this.ipAddress = cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)).trim();
    	
    	cursor = null;
    	//*************************************************
    	//Carrega a Porta a ser utilizada
    	cursor = this.carDroiDuinoDbAdapter.fetchPropertie(idPort);
    	startManagingCursor(cursor);
    	this.port = cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)).trim();
    }
    
    /**
     * Verifica se as propriedades básicas foram criadas no Banco de Dado
     * Se não fora - Então as Cria com valores default
     */
    private void verifyBasicProperties(){
    	
    	//************************************************
    	//Verificando uma das propriedades
    	Cursor cursor = this.carDroiDuinoDbAdapter.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER);
    	startManagingCursor(cursor);
    	
    	//************************************************
    	//Verifica apenas uma prop - Se não estiver criada logo nenhuma outra está
    	if(cursor.getCount() == 0){
    		//************************************************
    		//Inserindo prop do IP do Servidor
    		this.carDroiDuinoDbAdapter.createPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER, SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_SERVER, " ");
    		
    		//************************************************
    		//Inserindo prop da Porta do Servidor
    		this.carDroiDuinoDbAdapter.createPropertie(SystemProperties.PROPERTIE_ID_PORT_SERVER, SystemProperties.PROPERTIE_DEFAUL_VAL_PORT_SERVER, " ");
    		
    		//************************************************
    		//Inserindo prop do IP do Client
    		this.carDroiDuinoDbAdapter.createPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT, SystemProperties.PROPERTIE_DEFAUL_VAL_IPADDRESS_CLIENT, " ");
    		
    		//************************************************
    		//Inserindo prop da Porta do Client
    		this.carDroiDuinoDbAdapter.createPropertie(SystemProperties.PROPERTIE_ID_PORT_CLIENT, SystemProperties.PROPERTIE_DEFAUL_VAL_PORT_CLIENT, " ");
    	}
    }
}