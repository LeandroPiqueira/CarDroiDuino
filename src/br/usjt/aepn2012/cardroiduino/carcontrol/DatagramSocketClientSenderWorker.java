package br.usjt.aepn2012.cardroiduino.carcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.util.Log;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> Classe que implementa a Thread Trabalhadora que enviará os Dados de Controle para o Carrinho
 * via UPD
 * 	
 * <p>
 * <b>Data da Criacao:</b> 29/03/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class DatagramSocketClientSenderWorker implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * Endereco IP do Server para envio dos Comandos ao Carrinho
	 */
	private String serverIPAddress;
	
	/**
	 * Porta do Server para envio dos Comandos ao Carrinho
	 */
	private int serverPort;
	
	/**
	 * Socket para envio de Frames UDP
	 */
	private DatagramSocket datagramSocket;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Inicializa o Worker para envio dos Dados de Controle do Carrinho
	 * @param systemCore Core do Sistema
	 * @param serverIPAddress IP do Server para envio dos Comandos
	 * @param serverPort Porta do Server para envio dos Comandos
	 * @throws SocketException
	 */
	public DatagramSocketClientSenderWorker(CarDroiDuinoCore systemCore,  String serverIPAddress, int serverPort) throws SocketException{
		this.systemCore = systemCore;
		this.serverIPAddress = serverIPAddress;
		this.serverPort = serverPort;
		//*******************************************
		// Inicializando a Conexao para envio de Frames
		this.datagramSocket = new DatagramSocket();
		//*******************************************
		this.isOn = true;
	}
	
	/**
	 * Inicia o Processo de retirada dos dados dos Comandos do
	 * Core e envia através da Conexão Socket (UDP) ao Server do Carrinho
	 * Fica preso no Looping ate que a Thread seja finalizada
	 */
	public void run() {
		while(this.isOn){
			//*********************************************
			// Retira o dado de Comando do Carrinho
			byte[] commandData = this.systemCore.poolDataFromCarControlQueue();
			if (commandData != null){
				try{				
					//*******************************************
					// Montando o pacote Datagrama para envio do Comando
					DatagramPacket datagramPacketCommand = new DatagramPacket(commandData, commandData.length, InetAddress.getByName(this.serverIPAddress), this.serverPort);
					//*******************************************
					// Enviando o Comando via UDP
					this.datagramSocket.send(datagramPacketCommand);
					//*******************************************
				} catch (IOException e) {
					Log.e("DatagramSocketClientSenderWorker - send", e.getMessage());
				}
			}else{
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_SENDERS);
				} catch (InterruptedException e) {
					Log.e("DatagramSocketClientSenderWorker - InterruptedException", e.getMessage());
				}
			}
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
			Log.e("DatagramSocketClientSenderWorker - Exception", ex.getMessage());
		}
	}
}
