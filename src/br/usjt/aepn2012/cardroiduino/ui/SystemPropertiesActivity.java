package br.usjt.aepn2012.cardroiduino.ui;

import br.usjt.aepn2012.cardroiduino.R;
import br.usjt.aepn2012.cardroiduino.db.CarDroiDuinoDbAdapter;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Tela de Propriedades do Sistema - Possui as principais propriedades para 
 * comunicação entre os elementos clientes e servidores
 * 
 * 
 * <p>
 * <b>Data da Criacao:</b> 30/04/2012
 * </p>
 * 
 * @author Leandro Piqueira / Henrique Martins
 * 
 * @version 0.1
 * 
 */
public class SystemPropertiesActivity extends Activity {

	/**
	 * Campo IP do Servidor (Anbdroid do Carrinho)
	 */
	private EditText txtIPServer;
	
	/**
	 * Campo Porta do Servidor
	 */
	private EditText txtPortServer;
	
	/**
	 * Campo IP do Cliente (Controle Remoto)
	 */
	private EditText txtIPClient;
	
	/**
	 * Helper para gerenciar conexao ao SQLlite
	 */
	private CarDroiDuinoDbAdapter carDroiDuinoDbAdapter;
	
	/**
	 * Campo Porta do Carrinho 
	 */
	private EditText txtPortClient;
	
	/**
	 * Método Executado ao inicializar a classe
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		//*************************************************
        // Setando qual o Layout (XML) associado a essa Activity --> CarDroiDuino\res\layout
        setContentView(R.layout.properties_layout);
        
        //*************************************************
        // Capturando e configurando objetos da tela
        this.txtIPServer = (EditText) findViewById(R.id.txtIPServer);
        this.txtPortServer = (EditText) findViewById(R.id.txtPortServer);
        this.txtIPClient =  (EditText) findViewById(R.id.txtIPClient);
        this.txtPortClient =  (EditText) findViewById(R.id.txtPortClient);
        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//******************************************
				//Listener do botão - Salva os dados e fecha a janela ao Clicar no Botão
				saveFields();
				finish();
			}
		});
        
        //*************************************************
        // Inicializando Conexão ao Banco de Dados
        this.carDroiDuinoDbAdapter = new CarDroiDuinoDbAdapter(this);
        this.carDroiDuinoDbAdapter.open();
	
        //*************************************************
        // Populando Campos
        this.populateFields();
	}
	
	/**
	 * Popula os Campos para exibir os dados cadastrados
	 */
	private void populateFields(){
		
		Cursor cursor;
		//*************************************************
        // Buscando IP do Server
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER);
		startManagingCursor(cursor);
		this.txtIPServer.setText(cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));
		
		cursor = null;
		//*************************************************
        // Buscando Porta do Server
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(SystemProperties.PROPERTIE_ID_PORT_SERVER);
		startManagingCursor(cursor);
		this.txtPortServer.setText(cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));
		
		cursor = null;
		//*************************************************
        // Buscando IP do Client
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT);
		startManagingCursor(cursor);
		this.txtIPClient.setText(cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));
		
		cursor = null;
		//*************************************************
        // Buscando Porta do Client
		cursor = this.carDroiDuinoDbAdapter.fetchPropertie(SystemProperties.PROPERTIE_ID_PORT_CLIENT);
		startManagingCursor(cursor);
		this.txtPortClient.setText(cursor.getString(cursor.getColumnIndexOrThrow(SystemProperties.DATABASE_FIELD_PROP)));
	}
	
	/**
	 * Salva os campos editados
	 */
	private void saveFields(){
		//*************************************************
        // Salvando IP do Server
		this.carDroiDuinoDbAdapter.updatePropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_SERVER, this.txtIPServer.getText().toString());
		
		//*************************************************
        // Salvando Porta do Server
		this.carDroiDuinoDbAdapter.updatePropertie(SystemProperties.PROPERTIE_ID_PORT_SERVER, this.txtPortServer.getText().toString());
		
		//*************************************************
        // Salvando IP do Client
		this.carDroiDuinoDbAdapter.updatePropertie(SystemProperties.PROPERTIE_ID_IPADDRESS_CLIENT, this.txtIPClient.getText().toString());
		
		//*************************************************
        // Salvando Porta do Client
		this.carDroiDuinoDbAdapter.updatePropertie(SystemProperties.PROPERTIE_ID_PORT_CLIENT, this.txtPortClient.getText().toString());
		
	}
	
}
