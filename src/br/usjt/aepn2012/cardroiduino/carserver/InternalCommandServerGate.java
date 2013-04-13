package br.usjt.aepn2012.cardroiduino.carserver;

import android.hardware.Camera;
import android.os.Handler;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe que implenta o Gate de Inicialização e Manutenção do Resolver de Comandos
 * Internos do Dispositivo Android Server
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/07/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class InternalCommandServerGate {
	
	/**
	 * Core do Sistema para o Compartilhamento dos Dados Entre as Threads
	 */
	private CarDroiDuinoCore systemCore;

	/**
	 * Resolver que Trabalhará retirando so Comandos da Fila do Core e Processando Internamente no Dispositivo Android
	 */
	private InternalCommandResolverServer internalCommandResolverServer;
	
	/**
	 * Thread que dispara a rotina do Resolver
	 */
	private Thread internalCommandResolverServerThread;
	
	/**
	 * Status do Gate
	 */
	private boolean isInitialized = false;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Camera do Dispositivo Server - Para acessar funções como Ligar/Desligar o Flash
	 */
	private Camera mCamera;
	
	public InternalCommandServerGate(CarDroiDuinoCore systemCore, Handler msgPromptHandler, Camera camera){
		//*************************************************
		// Setando os dados informados
		this.systemCore = systemCore;
		this.msgPromptHandler = msgPromptHandler;
		this.mCamera = camera;
		//*************************************************
		// Efetuando a inicialização das Threads
		this.setupInternalCommandServerGate();
	}
	
	/**
	 * Inicializa a Thread do Resolver
	 */
	private void setupInternalCommandServerGate(){
		//*************************************************
		//Criando o Resolver de Comandos Inernos
		this.internalCommandResolverServer = new InternalCommandResolverServer(this.systemCore, this.msgPromptHandler);
		//*************************************************
		//Setando o objeto da camera
		this.internalCommandResolverServer.setCamera(this.mCamera);
		
		//*************************************************
		// Colocando o Resolver pra Trabalhar
		this.internalCommandResolverServerThread = new Thread(this.internalCommandResolverServer);
		this.internalCommandResolverServerThread.start();
		//*************************************************
		// Flag de Status do Gate para Inicializado
		this.isInitialized = true;
	}
	
	/**
	 * Retorna o Status do Gate
	 * @return True se inicializado / False se não incializado
	 */
	public boolean isInternalCommandServerGateInitialized(){
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
		this.internalCommandResolverServer.turnOff();
		//**************************************************
		// Tenta matar a Thread de resolver na FACA!!!
		while(retry){
			try {
				this.internalCommandResolverServerThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}
}
