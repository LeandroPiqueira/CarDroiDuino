package br.usjt.aepn2012.cardroiduino.carserver;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import br.usjt.aepn2012.cardroiduino.core.CarDroiDuinoCore;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br>Classe que implenta o resolvedor de comandos Internos para o Dispositivo Android Server
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
public class InternalCommandResolverServer implements Runnable {

	/**
	 * Core do Sistema - Provê as Filas para Troca de Dados entre as Threads do Sistema
	 */
	private CarDroiDuinoCore systemCore;
	
	/**
	 * Manipulador para enviar mensagens a Thread Grafica
	 */
	private Handler msgPromptHandler;
	
	/**
	 * Controle do Looping da Thread
	 */
	private boolean isOn = false;
	
	/**
	 * Camera do Dispositivo Server - Para acessar funções como Ligar/Desligar o Flash
	 */
	private Camera mCamera;
	
	/**
	 * Cria uma nova instância do Resolver que processará os comandos recebidos do core
	 * e executará sobre o dispositivo Server
	 * @param systemCore Core do Sistema 
	 * @param msgPromptHandler Manipulador para repassar Msgs de Status a Interface grafica
	 */
	public InternalCommandResolverServer(CarDroiDuinoCore systemCore, Handler msgPromptHandler){
		//**********************************************
		this.systemCore = systemCore;
		this.msgPromptHandler = msgPromptHandler;
		//**********************************************
		this.isOn = true;
		this.sendMessageToPrompt("Inicializado!!!");
	}
	
	/**
	 * Inicia o Processo de Retirada dos Dados da Fila de Comandos Internos do Core
	 * Processa os Comandos e executa as Funções necessárias 
	 */
	public void run() {
		while(this.isOn){
			//*********************************************
			// Retirando o Frame da Fila do Core para processamento
			byte[] data = this.systemCore.poolDataFromInternalCommandQueue();
			
			if(data!=null){
				try{
					//******************************************
					//Transforma em ASCII para Facilitar a verificação
					String comando = new String(data);
					
					//********************************************************
					//VERIFICANDO O COMANDO E EXECUTANDO AS OPERAÇÕES
					//********************************************************
					if (comando.contains(SystemProperties.COMANDO_LANTERNA_SERVER)){
						this.ligarDesligarLanterna();
					}else{
						this.sendMessageToPrompt("COMANDO DESCONHECIDO: " + comando);
					}				
				}catch(Exception ex){
					this.sendMessageToPrompt("Falha - " + ex.getMessage());
				}				
			}else{
				try {
					Thread.sleep(SystemProperties.THREAD_DELAY_INTERNAL_COMMAND_RESOLVER);
				} catch (InterruptedException e) {
					Log.e("DatagramSocketServerSenderWorker - InterruptedException", e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Ligar ou Desligar a Lanterna de Acordo com o Comando recebido
	 * @param data - Comando de Ligar/Desligar a Lanterna (Flash do dispositivo Android)
	 */
	private void ligarDesligarLanterna(){
		if(this.mCamera!=null){
			//********************************************
			//Capturando os Parametros da Camera
			Camera.Parameters params = this.mCamera.getParameters();
			//********************************************
			//Se a camera estiver Ligada Manda Desligar, do contrário manda Ligar
			if (params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH)){
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				this.sendMessageToPrompt("Lanterna Desligada!");
			}else{
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				this.sendMessageToPrompt("Lanterna Ligada!");
			}
			//********************************************
			//Devolvendo os Parametros para a Camera
			this.mCamera.setParameters(params);
		}
	}
	
	/**
	 * Seta a Camera carregada na Activity para acesso as funções necessárias
	 * @param camera
	 */
	public void setCamera(Camera camera){
		this.mCamera = camera;
	}
	
	
	/**
	 * Termina a Thread de Comunicação 
	 */
	public void turnOff(){
		this.isOn = false;
	}
	
	/**
	 * Envia mensagem para ser apresentada no Prompt
	 * @param txt
	 */
	private void sendMessageToPrompt(String txt){
		Message msg = new Message();
		msg.obj = "InternalCommandResolver: " + txt;
		this.msgPromptHandler.sendMessage(msg);
	}

}
