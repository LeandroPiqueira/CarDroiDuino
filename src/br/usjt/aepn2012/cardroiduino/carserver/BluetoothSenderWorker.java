package br.usjt.aepn2012.cardroiduino.carserver;

import java.io.IOException;
import java.io.OutputStream;

import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe que implenta o Worker que enviará os dados de Controle do Carrinho
 * ao Arduino através da comunicação Serial com o Modem Bluetooth
 * 	
 * <p>
 * <b>Data da Criacao:</b> 29/02/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class BluetoothSenderWorker implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Socket Bluetooth contendo a conexão com o Módulo Bluetooth do Arduino
	 */
	private BluetoothSocket bluetoothSocket;
	
	/**
	 * Stream de Saída para Enviar Dados pela Conexão Bluetooth
	 */
	private OutputStream outputStream;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Cria uma nova instância do Worker que irá retirar os dados de Controle do Core e
	 * enviar para o Módulo blueotooth conectado ao Arduino
	 * @param systemCore Core do Sistema
	 * @param bluetoothSocket Conexão com o Módulo Bluetooth
	 * @throws IOException
	 */
	public BluetoothSenderWorker(CarDroiDuinoCore systemCore, BluetoothSocket bluetoothSocket, Handler msgPromptHandler) throws IOException{
		this.systemCore = systemCore;
		this.bluetoothSocket = bluetoothSocket;
		this.msgPromptHandler = msgPromptHandler;
		//*************************************************
		// Criando o OutputStream para enviar os Dados de Controle através da conexão com o bluetooth
		this.outputStream = this.bluetoothSocket.getOutputStream();
		//*************************************************
		this.isOn = true;
	}

	/**
	 * Inicia o Processo de Retirada dos dados de controle
	 * do Core e envio ao Arduino que controla o carrinho
	 * através de comunicação Serial via Bluetooth (RS232)
	 */
	public void run() {
		while(isOn){
			byte[] controlData = this.systemCore.poolDataFromCarControlQueue();
			if (controlData != null){
				
				this.sendMessageToPrompt("Chegou dado para enviar ao carrinho: <" + new String(controlData) + ">. Enviando...");
				
				try {
					//****************************************
					//Enviando o Dado para o Módulo
					this.outputStream.write(controlData);
					//****************************************
					this.outputStream.flush();
					//****************************************
				} catch (IOException e) {
					Log.e("BluetoothSenderWorker - write", e.getMessage());
				}
				this.sendMessageToPrompt("Dado enviado ao Carrinho!");
			}else{
				//******************************************
				// Dando um tempo para as Threads
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_BLUETOOTH_SENDER);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Trermina a Thread de Comunicação 
	 * Desligando o Looping
	 */
	public void turnOff(){
		this.isOn = false;
		try {
			this.outputStream.close();
		} catch (Exception e) {
			Log.e("BluetoothSenderWorker - write", e.getMessage());
		}
	}
	
	/**
	 * Envia mensagem para ser apresentada no Prompt
	 * @param txt
	 */
	private void sendMessageToPrompt(String txt){
		Message msg = new Message();
		msg.obj = "BluetoothSenderWork: " + txt;
		this.msgPromptHandler.sendMessage(msg);
	}
}
