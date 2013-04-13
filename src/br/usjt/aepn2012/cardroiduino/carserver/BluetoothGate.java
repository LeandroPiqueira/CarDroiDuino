package br.usjt.aepn2012.cardroiduino.carserver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe que implenta o Gate de Comunicação via Bluetooth com o Arduino
 * Esse Gate inicializa a Thread Trabalhadora que enviará os dados de Comando do Carrinho
 * Através de uma conexao com o Módulo Bluetooth do Arduino
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
public class BluetoothGate {
	
	/**
	 * Core do Sistema para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * MAC address do Modem Bluetooth do Arduino
	 */
	private String modemBluetoothMACAddress;
	
	/**
	 * Adaptador Bluetooth do dispositivo Android
	 */
	private BluetoothAdapter bluetoothAdapter;
	
	/**
	 * 
	 */
	private BluetoothDevice moduloBluetooth;
	
	/**
	 * Socket Bluetooth contendo a conexão com o Módulo Bluetooth do Arduino
	 */
	private BluetoothSocket bluetoothSocket;
	
	/**
	 * Status do Gate
	 */
	private boolean isInitialized = false;
	
	/**
	 * Worker resposável pelo envio dos dados de comando ao Arduino
	 */
	private BluetoothSenderWorker bluetoothSenderWorker;
	
	/**
	 * Thread que dispara a Thread de envio
	 */
	private Thread bluetoothSenderThread;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Cria uma nova Instancia do Gate de Comunicacao Bluetooth com o Arduino
	 * @param systemCore Core do Spistema
	 * @param modemBluetoothMACAddress MAC address do Modem Bluetooth
	 * @throws IOException 
	 */
	public BluetoothGate(CarDroiDuinoCore systemCore, String modemBluetoothMACAddress, Handler msgPromptHandler) throws IOException, Exception{
		//*************************************************
		// Setando os dados informados
		this.systemCore = systemCore;
		this.modemBluetoothMACAddress = modemBluetoothMACAddress;
		this.msgPromptHandler = msgPromptHandler;
		//*************************************************
		// Efetuando a inicialização das Threads
		this.setupBluetoothGate();
	}
	
	/**
	 * Inicializa a Conexão com o Modulo Bluetooth e cria a Thread 
	 * Trabalhadora de envio de dados de controle ao Arduino
	 * @throws IOException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void setupBluetoothGate() throws IOException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		//*************************************************
		//Capturando o Adaptador de Bluetooth do Android
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//*************************************************
		//Criando objeto para conexão com o Módulo Bluetooth
		this.moduloBluetooth = this.bluetoothAdapter.getRemoteDevice(this.modemBluetoothMACAddress);
		//*************************************************
		// Cria conexão RF com o Módulo
		//this.bluetoothSocket = this.moduloBluetooth.createRfcommSocketToServiceRecord(SystemProperties.MY_UUID);
		Method m = this.moduloBluetooth.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
		this.bluetoothSocket = (BluetoothSocket) m.invoke(this.moduloBluetooth, 1);
		//*************************************************
		//Sempre cancelando o descobrimento, pois isso deixa a conexão lenta
		this.bluetoothAdapter.cancelDiscovery();
		
		//*************************************************
		//Efetua a Conexão com o Módulo
		this.bluetoothSocket.connect();
		
		//*************************************************
		// Criando o Worker que irá enviar os dados de controle ao Módulo bluetooths
		this.bluetoothSenderWorker = new  BluetoothSenderWorker(this.systemCore, this.bluetoothSocket, this.msgPromptHandler);
		
		//*************************************************
		// Colocando o Worker pra Trabalhar
		this.bluetoothSenderThread = new Thread(this.bluetoothSenderWorker);
		this.bluetoothSenderThread.start();
		//*************************************************
		// Flag de Status do Gate para Inicializado
		this.isInitialized = true;
	}
	
	/**
	 * Retorna o status do Gate
	 * @return True se inicializado / False se não incializado
	 */
	public boolean isBluetoothGateInitialized(){
		return this.isInitialized;
	}
	
	/**
	 * Finaliza as Threads do Core
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
		this.bluetoothSenderWorker.turnOff();
		//**************************************************
		// Tenta matar a Thread de recebimento na FACA!!!
		while(retry){
			try {
				this.bluetoothSenderThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
