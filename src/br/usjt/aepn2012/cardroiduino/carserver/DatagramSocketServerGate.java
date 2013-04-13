package br.usjt.aepn2012.cardroiduino.carserver;

import java.io.IOException;

import android.os.Handler;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe que implenta o Gate de Comunicação via TCP/IP (Socket) para o Servidor do Carrinho
 * Inicializa as Threads Trabalhadoras para Receber os dados de cotrole do carrinho e Enviar
 * os Frames capturados da camera de video do Servidor
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
public class DatagramSocketServerGate {
	
	/**
	 * Core do Sistema para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Endereco IP do Controle Remoto para conexao e envio dos Frames de 
	 * Video via UDP
	 */
	private String clientIPAddress;
	
	/**
	 * Porta que sera utilizada pelo Server para recebimento dos Dados de Controle
	 * Mesmo Numero de Porta utilizado pelo Client para Recebimento dos Frames de Video
	 */
	private int clientServerPort;
	
	/**
	 * Thread Trabalhadora que Receberá os dados Vindos do Controle 
	 * e encaminhará para o Core do Sistema
	 */
	private DatagramSocketServerReceiverWorker datagramSocketServerReceiverWorker;
	
	/**
	 * Thread que vai disparar o Worker de Recebimento
	 */
	private Thread datagramSocketServerReceiverThread;
	
	/**
	 * Thread Trabalhadora que enviará os dados de video para o Controle
	 */
	private DatagramSocketServerSenderWorker datagramSocketServerSenderWorker;
	
	/**
	 * Thread que vai disparar o Worker de Envio
	 */
	private Thread datagramSocketServerSenderThread;
	
	/**
	 * Status do Gate
	 */
	private boolean isInitialized = false;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Cria uma nova instacia do SocketServerGate
	 * @param systemCore Core do Sistema
	 * @param clientIPAddress Endereco IP do Controle Remoto
	 * @param clientServerPort Porta do Servidor para recebimento de dados e do Client para recebimento dos Frames
	 * @param msgPromptHandler Manipulador para enviar msgs a Interface Gráfica
	 * @throws IOException
	 */
	public DatagramSocketServerGate(CarDroiDuinoCore systemCore, String clientIPAddress, int clientServerPort, Handler msgPromptHandler) throws IOException{
		//*************************************************
		// Configurando o Gate
		this.systemCore = systemCore;
		this.clientIPAddress = clientIPAddress;
		this.clientServerPort = clientServerPort;
		this.msgPromptHandler = msgPromptHandler;
		//*************************************************
		// Efetuando a inicialização das Threads
		this.setupSocketGate();
	}
	
	/**
	 * Inicializa o Gate - Abre o Socket - Recebe a Conexão do Client -
	 * Inicializa os Workers para Trabalharem com o Client Conectado
	 * @throws IOException 
	 */
	private void setupSocketGate() throws IOException{
		//*************************************************************
		//Configurando Thread Trabalhadora para Receber os Dados de Controle do Carrinho via TCP/IP
		this.datagramSocketServerReceiverWorker = new DatagramSocketServerReceiverWorker(this.systemCore, this.clientServerPort, this.msgPromptHandler);
		//*************************************************************
		//Configurando Thread Trabalhadora para Enviar os Frames capturados da Camera de video do Server
		this.datagramSocketServerSenderWorker = new DatagramSocketServerSenderWorker(this.systemCore, this.clientIPAddress, this.clientServerPort);
		//*************************************************************
		//Iniciando as Threads Trabalhadoras - Ao executar o Metodo start() as threads comecam a executar o metodo Run() dentro delas 
		this.datagramSocketServerReceiverThread = new Thread(this.datagramSocketServerReceiverWorker);
		this.datagramSocketServerReceiverThread.start();
		
		this.datagramSocketServerSenderThread = new Thread(this.datagramSocketServerSenderWorker);
		this.datagramSocketServerSenderThread.start();
		
		//*************************************************************
		// Gate Servidor Inicializado com sucesso
		this.isInitialized = true;		
	}
	
	/**
	 * Retornar o Status do Gate
	 * @return Status do Gate
	 */
	public boolean isSocketGateInitialized(){
		return this.isInitialized;
	}
	
	/**
	 * Mata as Threads Workers do Gate Servidor
	 */
	public void turnOff(){
		
		//**************************************************
		// Verifica se não foi finalizada ou mesmo não foi inicializada
		if (!this.isInitialized)
			return;
		
		//**************************************************
		// Seta a flag para não inicializado para evitar null exception
		this.isInitialized = false;
		
		//**************************************************
		// Controle para Terminar as Threads
		boolean retry = true;
		//**************************************************
		// Termina os loopings das Threads
		this.datagramSocketServerReceiverWorker.turnOff();
		this.datagramSocketServerSenderWorker.turnOff();
		//**************************************************
		// Tenta matar a Thread de recebimento na FACA!!!
		while(retry){
			try {
				this.datagramSocketServerReceiverThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
		//**************************************************
		// Reutilizando a Flag-- Isso é errado... mas fazer o que?
		retry = true;
		//**************************************************
		// Tenta matar a Thread de envio na BALA!!!!
		while(retry){
			try {
				this.datagramSocketServerSenderThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
