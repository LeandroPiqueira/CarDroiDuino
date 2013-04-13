package br.usjt.aepn2012.cardroiduino.carserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.os.Handler;
import android.os.Message;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br>Classe que implenta o Worker que receberá dados de Controle do Carrinho via 
 * Conexão Socket recebida pelo Servidor 
 * 
 * <p>
 * <b>Data da Criacao:</b> 26/03/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class DatagramSocketServerReceiverWorker implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Socket para conexao via UDP com o Client	
	 */
	private DatagramSocket datagramSocket;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Cria uma nova instância do Worker que receberá os dados de Controle do Carrinho e 
	 * os Encaminhará para o Core do Systema para serem enviados ao Módulo Bluetooth
	 * @param systemCore Core do Sistema
	 * @param serverPort Porta do Servidor por onde recebera os dados
	 * @param msgPromptHandler Manipulador para repassar Msgs de Status a Interface grafica
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public DatagramSocketServerReceiverWorker(CarDroiDuinoCore systemCore, int serverPort, Handler msgPromptHandler) throws SocketException, UnknownHostException{
		this.systemCore = systemCore;
		//**********************************************
		// Inicializando o Socket para envio dos Dados de Controle 
		this.datagramSocket = new DatagramSocket(serverPort);
		//**********************************************
		this.msgPromptHandler = msgPromptHandler;
		//**********************************************
		this.isOn = true;
		this.sendMessageToPrompt("Inicializado!!!");
	}
	
	/**
	 * Inicia o Processo de Recebimento dos Dados de Controle pela Conexão Socket
	 * e envio as Filas de Entrada do Core
	 */
	public void run() {
		while(this.isOn){
			try {
				//***************************************
				// Pacote UDP para receber os dados de Controle
				DatagramPacket packet = new DatagramPacket(new byte[SystemProperties.BUFFER_CONTROL_RECEIVER], SystemProperties.BUFFER_CONTROL_RECEIVER);
				//***************************************
				// Recebendo o Dado via UDP
				this.datagramSocket.receive(packet);
				this.sendMessageToPrompt("Dado de Controle: <" + new String(packet.getData()) + ">.");
				//***************************************
				// Criando o Array para conter os dados de Comando recebidos
				byte[] byteControl = new byte[packet.getLength()];
				//***************************************
				// Copia o Dado do Buffer para o Array dimensionado
				System.arraycopy(packet.getData(), 0, byteControl, 0, packet.getLength());
				//***************************************
				// Enviando o dado para o a Fila de Entrada do Core do Sistema
				//this.systemCore.addDataToCarControlQueue(byteControl);
				//this.sendMessageToPrompt("Controle enviado a Fila do Core...");
				this.sendDataToQueue(byteControl);
				//***************************************
			} catch (IOException e) {
				this.sendMessageToPrompt("Ocorreu uma IOException: " + e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					this.sendMessageToPrompt("Ocorreu uma InterruptedException: " + e1.getMessage());
				}
			} catch (Exception e){
				this.sendMessageToPrompt("Ocorreu uma Exception: " + e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					this.sendMessageToPrompt("Ocorreu uma InterruptedException: " + e1.getMessage());
				}
			}
		}
	}
	
	/**
	 * Direciona o Dado recebiodo para sua devia Fila no Core do Sistema
	 * @param data - Dado recebido via Sockect
	 */
	private void sendDataToQueue(byte[] data){
		//*****************************************
		//Se possuir o Prefixo de Comando Interno, então envia o Comando para a Fila de Comandos Internos do Core
		// Do contrário envia o Comando para a Fila de Dados a Serem enviados pelo BluetootSenderWorker ao Carro
		if(new String(data).contains(SystemProperties.COMANDO_INTERNO_PREFIXO)){
			this.systemCore.addDataToInternalCommandQueue(data);
			this.sendMessageToPrompt("Comando enviado a Fila de comandos Internos do Dispositivo Android...");
		}else{
			this.systemCore.addDataToCarControlQueue(data);
			this.sendMessageToPrompt("Controle enviado a Fila de dados a serem enviados ao Carro...");
		}
	}
	
	/**
	 * Termina a Thread de Comunicação 
	 */
	public void turnOff(){
		this.isOn = false;
		try{
			this.datagramSocket.disconnect();
			this.datagramSocket.close();
		}catch(Exception ex){
			this.sendMessageToPrompt("Ocorreu uma Exception: " + ex.getMessage());
		}
	}
	
	/**
	 * Envia mensagem para ser apresentada no Prompt
	 * @param txt
	 */
	private void sendMessageToPrompt(String txt){
		Message msg = new Message();
		msg.obj = "UDPReceiver: " + txt;
		this.msgPromptHandler.sendMessage(msg);
	}
}
