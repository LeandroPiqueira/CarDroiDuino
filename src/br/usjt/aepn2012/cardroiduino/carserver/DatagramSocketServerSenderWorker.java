package br.usjt.aepn2012.cardroiduino.carserver;

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
 * <br> Classe que implenta o Worker que enviará os Frames do video via 
 * Conexão Socket com o Servidor do Carrinho
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
public class DatagramSocketServerSenderWorker implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * Endereco IP do Controle Remoto para Envio dos Frames
	 */
	private String clientIPAddress; 
	
	/**
	 * Porta do Controle Remoto para Envio dos Frames
	 */
	private int clientPort;
	
	/**
	 * Socket para envio de Frames UDP
	 */
	private DatagramSocket datagramSocket;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Cria uma nova instancia da Thread trabalhadora para envio dos Frames
	 * capturados da Camera do dispositivo
	 * @param systemCore Core do Sistema
	 * @param clientIPAddress Endereco IP do Clontrole Remoto
	 * @param clientPort Porta do Controle Remoto
	 * @throws SocketException
	 */
	public DatagramSocketServerSenderWorker(CarDroiDuinoCore systemCore, String clientIPAddress, int clientPort) throws SocketException{
		this.systemCore = systemCore;
		this.clientIPAddress = clientIPAddress;
		this.clientPort = clientPort;
		//*******************************************
		// Inicializando a Conexao para envio de Frames
		this.datagramSocket = new DatagramSocket();
		//*******************************************
		this.isOn = true;
	}
	
	/**
	 * Inicia o Processo de Retirada dos Dados de Video do Core
	 * e Envio pela Conexão Socket ao Client (Controle do Carrinho)
	 * Fica preso no Looping ate que a Thread seja finalizada
	 */
	public void run() {
		while(this.isOn){
			//*********************************************
			// Retirando o Frame da Fila do Core para envio
			byte[] videoData = this.systemCore.poolDataFromCameraQueue();
			//*********************************************
			if (videoData != null){
				try {
					//*******************************************
					// Montando o pacote Datagrama para envio do Frame
					DatagramPacket datagramPacketVideo = new DatagramPacket(videoData, videoData.length, InetAddress.getByName(this.clientIPAddress), this.clientPort);
					//*******************************************
					// Enviando o Frame via UDP
					this.datagramSocket.send(datagramPacketVideo);
					//*******************************************
				} catch (IOException e) {
					Log.e("DatagramSocketServerSenderWorker - send", e.getMessage());
				}
			}else{
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_SENDERS);
				} catch (InterruptedException e) {
					Log.e("DatagramSocketServerSenderWorker - InterruptedException", e.getMessage());
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
			Log.e("DatagramSocketServerSenderWorker - Exception", ex.getMessage());
		}
	}
}
