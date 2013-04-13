package br.usjt.aepn2012.cardroiduino.utils;

import java.util.UUID;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Classe de Propriedades do Sistema - 
 * Contém constantes e Métodos comuns ao Sistema
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
public class SystemProperties {
	/**
	 * Chave para Passagem de Dados entre Activities - Número da Porta (Tanto para Servidor quanto para Client)
	 * # Client: Dispositivo Controle Remoto
	 * # Servidor: Dispositivo no Carrinho
	 */
	public static final String KEY_PORT_NUMBER = "port_num";
	
	/**
	 *  Chave para Passagem de Dados entre Activities - Endereço IP do Servidor (Dispositivo no Carrinho)
	 */
	public static final String KEY_IP_ADDRESS = "ip_address";
	
	/**
	 * Chave para Passagem de Dados retornados via Intent - MAC address do dispositivo Bluetooth
	 */
	public static final String KEY_DEVICE_ADDRESS = "device_address";
	
	/**
	 * Request Code para Validar a Intent retornada da Lista de Dispositivos Bluetooth
	 */
	public static final int REQUEST_CODE_CONNECT_BLUETOOTH = 1; 
	
	/**
	 * Request Code para Validar a Intent retornada da habilitação do Bluetooth do Dispositivo
	 */
	public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 2;
	
	/**
	 * Informação para Conexão com o Módulo Bluetooth
	 */
	public static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	/**
	 * Nome da base de dados de propriedades a ser atribuido em CarDroiDuinoDbAdapter
	 */
	public static final String DATABASE_NAME = "data";
	
	/**
	 * Nome da tabela de propriedades a ser criada na base de dados em CarDroiDuinoDbAdapter
	 */
	public static final String DATABASE_TABLE = "propriedades";
	
	/**
	 * Campo da tabela de propriedades - Identificação
	 */
	public static final String DATABASE_FIELD_ID = "_id";
	
	/**
	 * Campo da tabela de propriedades - Propriedade
	 */
	public static final String DATABASE_FIELD_PROP = "propriedade";
	
	/**
	 * Campo da tabela de propriedades - Descrição
	 */
	public static final String DATABASE_FIELD_DESC = "descricao";
	
	/**
	 * ID da Propriedade - Endereço IP do Server
	 */
	public static final int PROPERTIE_ID_IPADDRESS_SERVER = 1;
	
	/**
	 * ID da Propriedade - Porta do Servidor
	 */
	public static final int PROPERTIE_ID_PORT_SERVER = 2;
	
	/**
	 * ID da Propriedade - Endereço IP do Client
	 */
	public static final int PROPERTIE_ID_IPADDRESS_CLIENT = 3;
	
	/**
	 * Valor Default para Propriedade - Endereço IP do Servidor
	 */
	public static final String PROPERTIE_DEFAUL_VAL_IPADDRESS_SERVER = "192.168.171.0";
	
	/**
	 * Valor Default para Propriedade - Porta do Servidor
	 */
	public static final String PROPERTIE_DEFAUL_VAL_PORT_SERVER = "5005";
	
	/**
	 * Valor Default para Propriedade - Endereço IP do Cliente
	 */
	public static final String PROPERTIE_DEFAUL_VAL_IPADDRESS_CLIENT = "192.168.171.1";
	
	/**
	 * Valor Default para Propriedade - Porta do Cliente
	 */
	public static final String PROPERTIE_DEFAUL_VAL_PORT_CLIENT = "5005";
	
	/**
	 * ID da Propriedade - Porta do Client
	 */
	public static final int PROPERTIE_ID_PORT_CLIENT = 4;
	
	/**
	 * Verão da base de dados de propriedades
	 */
	public static final int DATABASE_VERSION = 1;
	
	/**
	 * SQL para criação da tabela de propriedades
	 */
	public static final String TABLE_CREATE = "create table " + DATABASE_TABLE + " (" + DATABASE_FIELD_ID + " integer primary key, "
										        + DATABASE_FIELD_PROP + " text not null, " + DATABASE_FIELD_DESC + " text not null);";
	
	/**
	 * SQL para excluisão da tabela de propriedades
	 */
	public static final String TABLE_DROP = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
	
	/**
	 * Comando para Controlar o Carrinho - Virar para Esquerda
	 * Composição: E<grau a virar - de 0 à 90 graus>
	 * Por exemplo: E56 --> Virar 56 graus à esquerda
	 */
	public static final String COMANDO_ESQUERDA = "E";
	
	/**
	 * Comando para Controlar o Carrinho - Virar para Direita
	 * Composição: D<grau a virar - de 0 à 90 graus>
	 * Por exemplo: D45 --> Virar 45 graus à direita
	 */
	public static final String COMANDO_DIREITA = "D";
	
	/**
	 * Comando para Controlar o Carrinho - Acelerar para Frente
	 */
	public static final String COMANDO_FRENTE = "FRT";
	
	/**
	 * Comando para Controlar o Carrinho - Parar
	 */
	public static final String COMANDO_PARAR = "PAR";
	
	/**
	 * Comando para Controlar o Carrinho - Ir para Trás - Ré
	 */
	public static final String COMANDO_RE = "TRA";
	
	/**
	 * Comando para Controlar o Carrinho - Troca de Marcha
	 * Composição: M<numero da marchar - de 00 à 03>
	 * Por exemplo: M3 --> Terceira marcha (ou PWM com 100% de Ciclo de Trabalho)
	 */
	public static final String COMANDO_MARCHA = "MC";
	
	/**
	 * Prefixo Usado por Todos os Comandos Internos Destinados a executar funções
	 * dentro dos Dispositivos Android
	 */
	public static final String COMANDO_INTERNO_PREFIXO = "I_";
	
	/**
	 * Comando Interno para controle de funções dos Dispositivos Android - Ligar/Desligar Lanterna do Flash
	 */
	public static final String COMANDO_LANTERNA_SERVER = COMANDO_INTERNO_PREFIXO + "LANTE";
	
	/**
	 * Número máximo de Marchas para o Carrinho
	 */
	public static final int CONTROLE_NUM_MARCHA_MAX = 3;
	
	/**
	 * Número mínimo da Marcha
	 */
	public static final int CONTROLE_NUM_MARCHA_MIN = 0;	
	
	/**
	 * Delay para as Threads de Controle do Carrinho
	 */
	public static final int THREAD_DELAY_BLUETOOTH_SENDER = 5;
	
	/**
	 * Delay para as Threads de Envio de dados Via TCP/IP
	 */
	public static final int THREAD_DELAY_SOCKET_SENDERS = 5;
	
	/**
	 * Delay para as Threads de Recebimento de dados Via TCP/IP
	 */
	public static final int THREAD_DELAY_SOCKET_RECEIVERS = 5;
	
	/**
	 * Delay para a Thread Resolvedora de Comandos Internos dos Dispositivos Android
	 */
	public static final int THREAD_DELAY_INTERNAL_COMMAND_RESOLVER = 1000;
	
	/**
	 * Numero máximo de Graus a virar para Esquerda ou Direita
	 */
	public static final int CONTROLE_GRAUS_MAX = 90;
	
	/**
	 * Range total do acelerômetro - Escala Superior menos Escala Inferior
	 */
	public static final float CONTROLE_ACELEROMETRO_RANGE_TOTAL = 10*2;
	
	/**
	 * Fundo de Escala Superior da Leitura do Acelerômetro: 9.8 m/s²
	 */
	public static final float CONTROLE_ACELEROMETRO_FUNDO_ESCALA_SUPERIOR = 10;
	
	/**
	 * Fundo de Escala Inferior da Leitura do Acelerômetro: -9.8 m/s²
	 */
	public static final float CONTROLE_ACELEROMETRO_FUNDO_ESCALA_INFERIOR = -10;
	
	/**
	 * Modo Free Navigation - Valor default para o ponto de controle Ré
	 * Utilizado caso o calculo do ponto dê um valor incossistente com o fundo de escala
	 */
	public static final float CONTROLE_FREE_NAV_PONTO_RE_DEFAULT = -8;//6;
	
	/**
	 * Modo Free Navigation - Valor default para o ponto de controle M1
	 * Utilizado caso o calculo do ponto dê um valor incossistente com o fundo de escala
	 */
	public static final float CONTROLE_FREE_NAV_PONTO_M1_DEFAULT = 9;//8;
	
	/**
	 * Threshold do Eixo Z - Limiar da diferença entre os valores lidos para se determinar uma leitura
	 */
	public static final float CONTROLE_EIXO_Z_THRESHOLD = (float) 0.5;//0.1;
	
	/**
	 * Threshold do Eixo Y - Limiar da diferença entre os valores lidos para se determinar uma leitura
	 */
	public static final float CONTROLE_EIXO_Y_THRESHOLD = (float) 0.2;
	
	/**
	 * Número de Leituras no primeiro Acumulado do Eixo Z - para o controle direcional
	 */
	public static final float CONTROLE_EIXO_Z_QTD_LEITURAS_1 = 7;
	
	/**
	 * Número de Leituras no segundo Acumulado do Eixo Z - para o controle direcional
	 */
	public static final float CONTROLE_EIXO_Z_QTD_LEITURAS_2 = 7;
	
	/**
	 * Número de Leituras no primeiro Acumulado do Eixo Y - para o controle direcional
	 */
	public static final float CONTROLE_EIXO_Y_QTD_LEITURAS_1 = 2;
	
	/**
	 * Número de Leituras no segundo Acumulado do Eixo Y - para o controle direcional
	 */
	public static final float CONTROLE_EIXO_Y_QTD_LEITURAS_2 = 2;
	
	/**
	 * Códigos das marchas na de acordo com o Numero das passagens
	 * Posicao 0: N --> Neutra
	 * Posicao 1: G --> Primeira Marcha
	 * Posicao 2: H --> segunda Marcha
	 * Posicao 3: I --> Terceira Marcha
	 */
	public static final String[] COMANDO_MARCHA_CARACTERES = {"N","G","H","I"};

	/**
	 * Número máximo de amostras a ignorar do acelerômetro - Ajuste da taxa de 
	 * frequencia do Acelerometro   
	 */
	//public static final int ACELER_AMOSTRAS_IGNORAR = 3;
	
	/**
	 * Tamanho do Buffer de recebimento para os Frames encaminhados ao Client
	 */
	public static final int BUFFER_FRAME_RECEIVER = 500000;

	/**
	 * Tamanho do Buffer de recebimento para os Dados de Controle encaminhados ao Server
	 */
	public static final int BUFFER_CONTROL_RECEIVER = 8;//4;
}
