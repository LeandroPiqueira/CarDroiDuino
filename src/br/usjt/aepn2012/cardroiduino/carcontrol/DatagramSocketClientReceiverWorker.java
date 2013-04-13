package br.usjt.aepn2012.cardroiduino.carcontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import android.util.Log;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> Classe que implementa o Worker que recebera os Frames de Video do
 * Server e os enviara para a Tela, atraves do Core do Sistema, a Fim de 
 * exibir os Frames ao usuario em Tempo Real
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
public class DatagramSocketClientReceiverWorker implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Socket para conexao via UPD com o Server e recebimento dos Frames	
	 */
	private DatagramSocket datagramSocket;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;

	/**
	 * Inicializando o worker para recebimento dos dados de video vindos via TCP/IP
	 * @param systemCore Core do Sistema
	 * @param clientPort Porta do Client para Receber os Frames
	 * @throws SocketException
	 */
	public DatagramSocketClientReceiverWorker(CarDroiDuinoCore systemCore, int clientPort) throws SocketException{
		this.systemCore = systemCore;
		//**********************************************
		// Inicializando a conexao Datagrama
		this.datagramSocket = new DatagramSocket(clientPort);
		//**********************************************
		this.isOn = true;
	}
	
	/**
	 * Inicia o Recebimento dos Frames via Socket UDP
	 * e os encaminha ao Core do Sistema para serem
	 * Capturados pela Thread que desenha o Frame na View
	 */
	public void run() {
		while(this.isOn){
			try {
				//********************************************
				// Cria o Pacote Datagrama para Recebimento do Frame
				DatagramPacket packetFrame = new DatagramPacket(new byte[SystemProperties.BUFFER_FRAME_RECEIVER], SystemProperties.BUFFER_FRAME_RECEIVER);
				//*********************************************
				// Recebendo o Frame de Video
				this.datagramSocket.receive(packetFrame);
				//*********************************************
				// Criando o array de byte para receber o Frame do Buffer
				byte[] byteFrame = new byte[packetFrame.getLength()];
				//*********************************************
				// Copia o Frame do Buffer para o Array definitivo
				System.arraycopy(packetFrame.getData(), 0, byteFrame, 0, packetFrame.getLength());
				//*********************************************
				// Enviando o Frame para o a Fila de Entrada do Core do Sistema
				this.systemCore.addDataToCameraQueue(byteFrame);
				//*********************************************
			} catch (IOException e) {
				Log.e("DatagramSocketClientReceiverWorker - IOException", e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					Log.e("DatagramSocketClientReceiverWorker - InterruptedException", e1.getMessage());
				}
			} catch (Exception e){
				Log.e("DatagramSocketClientReceiverWorker - Exception", e.getMessage());
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_SOCKET_RECEIVERS);
				} catch (InterruptedException e1) {
					Log.e("DatagramSocketClientReceiverWorker - InterruptedException", e1.getMessage());
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
			Log.e("DatagramSocketClientReceiverWorker - Exception", ex.getMessage());
		}
	}
}
