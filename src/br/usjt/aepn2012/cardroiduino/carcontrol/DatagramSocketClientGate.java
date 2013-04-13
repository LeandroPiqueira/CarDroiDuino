package br.usjt.aepn2012.cardroiduino.carcontrol;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe que implenta o Gate de Comunicação via UPD (Socket) para o Controle Remoto do Carrinho
 * Inicia as Threads Trabalhadoras para envio dos comandos e recebimento dos Frames de video
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
public class DatagramSocketClientGate {

	/**
	 * Core do Sistema para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Endereço IP do Servidor do Carrinho para Conexão
	 */
	private String serverIPAddress;
	
	/**
	 * Porta que sera utilizada pelo Client para recebimento dos Frames de video
	 * Mesmo numero de porta utilizado pelo Server para recebimento dos dados de controle
	 */
	private int clientServerPort;
	
	/**
	 * Status do Gate
	 */
	private boolean isInitialized = false;
	
	/**
	 * Worker resposavel pelo recebimento dos Frames de video e encaminhamento destes ao Core do Sistema
	 */
	private DatagramSocketClientReceiverWorker datagramSocketClientReceiverWorker;
	
	/**
	 * Thread que vai disparar o Worker de Recebimento
	 */
	private Thread datagramSocketClientReceiverThread;
	
	/**
	 * Worker responsavel pelo envio dos Dados de Controle do Carrinho para o Server atraves do core
	 */
	private DatagramSocketClientSenderWorker datagramSocketClientSenderWorker;
	
	/**
	 * Thread que vai disparar o Worker de Envio
	 */
	private Thread datagramSocketClientSenderThread;
	
	/**
	 * Cria uma nova instacia do SocketClientGate
	 * @param systemCore Core do Sistema
	 * @param serverIPAddress Endereco IP do Servidor
	 * @param clientServerPort Porta para envio dos Dados ao Server e Recebimento dos Frames
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public DatagramSocketClientGate(CarDroiDuinoCore systemCore, String serverIPAddress, int clientServerPort) throws UnknownHostException, IOException{
		//*************************************************
		// Configurando o Gate
		this.systemCore = systemCore;
		this.serverIPAddress = serverIPAddress;
		this.clientServerPort = clientServerPort;
		//*************************************************
		// Efetuando a inicialização das Threads
		this.setupSocketGate();
	}
	
	/**
	 * Inicializa a Conexão TCP/IP e os Trabalhadores (Workers) para
	 * Enviar e Receber dados do Servidor do Carrinho
	 * @throws SocketException 
	 */
	private void setupSocketGate() throws SocketException{
		//*************************************************
		// Se a Conexão foi efetuada, então cria o Objeto da Thread Worker que receberá os Frames de Video
		this.datagramSocketClientReceiverWorker = new DatagramSocketClientReceiverWorker(this.systemCore, this.clientServerPort);
		//*************************************************
		//Cria objeto da Thread Trabalhadora que enviara os dados de Controle do Carrinho
		this.datagramSocketClientSenderWorker = new DatagramSocketClientSenderWorker(this.systemCore, this.serverIPAddress, this.clientServerPort);
		
		//*************************************************
		// Inicia as Threads para que elas comecem seus Trabalhos
		this.datagramSocketClientReceiverThread = new Thread(this.datagramSocketClientReceiverWorker);
		this.datagramSocketClientReceiverThread.start();

		this.datagramSocketClientSenderThread = new Thread(this.datagramSocketClientSenderWorker);
		this.datagramSocketClientSenderThread.start();
		
		//*************************************************
		// Flag de Status do Gate para Inicializado
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
	 * Desliga o Gate matando as Threads Trabalhadoras
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
		this.datagramSocketClientReceiverWorker.turnOff();
		this.datagramSocketClientSenderWorker.turnOff();
		//**************************************************
		// Tenta matar a Thread de recebimento na FACA!!!
		while(retry){
			try {
				this.datagramSocketClientReceiverThread.join();
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
				this.datagramSocketClientSenderThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
