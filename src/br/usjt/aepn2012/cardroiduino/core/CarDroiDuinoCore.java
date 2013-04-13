package br.usjt.aepn2012.cardroiduino.core;

import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Core do Sistema CarDroiDuino -
 * 	Possui filas internas para o compartilhamento de dados entre as threads desse módulo
 * 	- Fila dos Dados da Câmera --> Compartilhamento do Dado de Vídeo Capturado pela Câmera do Server
 * 	- Fila dos Dados de Controle do Carrinho --> Compartilhamento do Dado do Controle do Carrinho (Client)
 * </p>
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
public class CarDroiDuinoCore {
	
	/**
	 * Fila para Compartilhamento dos Dados de Video Capturados da Câmera
	 */
	private ConcurrentLinkedQueue<byte[]> filaCameraVideo;
	
	/**
	 * Fila para Compartilhamento dos Dados de Controle do Carrinho
	 */
	private ConcurrentLinkedQueue<byte[]> filaControleCarro;
	
	/**
	 * Fila para Compartilhamento dos Comandos Internos (para controle entre os dispositivos Android)
	 */
	private ConcurrentLinkedQueue<byte[]> filaComandosInternos;
	
	/**
	 * Construtor para Inicializar as Filas
	 */
	public CarDroiDuinoCore(){
		this.filaCameraVideo = new ConcurrentLinkedQueue<byte[]>();
		this.filaControleCarro = new ConcurrentLinkedQueue<byte[]>();
		this.filaComandosInternos = new ConcurrentLinkedQueue<byte[]>();
	}
	
	/**
	 * Adiciona os Dados Obtidos da Câmera a Fila de Compartilhamento
	 * @param data - Dados da Câmera do Dispositivo
	 * @return True se adicionou o dado a Fila
	 */
	public synchronized boolean addDataToCameraQueue(byte[] data){
		return this.filaCameraVideo.add(data);
	}
	
	/**
	 * Adiciona os Dados de Controle do Carrinho a Fila de Compartilhamento
	 * @param data - Dados recebidos pelo Controle do Carrinho
	 * @return True se adicionou o dado a Fila
	 */
	public synchronized boolean addDataToCarControlQueue(byte[] data){
		return this.filaControleCarro.add(data);
	}
	
	/**
	 * Adiciona os Dados de Comandos Internos à Fila de Comandos Internos dos Dispositivos Android
	 * @param data - Dados recebidos pelo Datagram Receiver Worker
	 * @return True se adicionou o dado a Fila
	 */
	public synchronized boolean addDataToInternalCommandQueue(byte[] data){
		return this.filaComandosInternos.add(data);
	}
	
	/**
	 * Retira o dado do Video da câmera da Fila de Compartilhamento
	 * @return Dado do Video armazenado
	 */
	public synchronized byte[] poolDataFromCameraQueue(){
		return this.filaCameraVideo.poll();
	}
	
	/**
	 * Retira os Dados de Controle do Carrinho a Fila de Compartilhamento
	 * @return Dado para controle do Carrinho
	 */
	public synchronized byte[] poolDataFromCarControlQueue(){
		return this.filaControleCarro.poll();
	}
	
	/**
	 * Retira os Dados dos Comandos Internos da Fila de Comandos Internos dos Dispositivos Android
	 * @return Dados de Comandos internos dos Dispositivos Android
	 */
	public synchronized byte[] poolDataFromInternalCommandQueue(){
		return this.filaComandosInternos.poll();
	}
}
