package br.usjt.aepn2012.cardroiduino.ui;

import java.util.Set;

import br.usjt.aepn2012.cardroiduino.R;
import br.usjt.aepn2012.cardroiduino.utils.SystemProperties;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * <p>
 * <b>Descricao:</b> 
 * <br> 
 * <p> Esta Activity aparece como uma dialog. Lista os dispositivos pareados
 * e os detectados após o descobrimento. Quando um dispositivo é selecionado
 * pelo usuário o MAC address do dispositivo é retornado, para a Activity
 * que chamou está, no result Intent
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
public class DeviceBluetoothListActivity extends Activity {
    
	/**
	 * Constantes para Debugar a Aplicacao
	 */
	private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    /**
     * Objeto para Reter o Adaptador Bluetooth
     */
    private BluetoothAdapter mBtAdapter;
    
    /**
     * Array Adapter que irá conter os MAC addresses dos dispositivos já pareados
     */    
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    
    /**
     * Array Adapter que irá conter os MAC addresses dos dispositivos novos encontrados pela 
     * Busca do Bluetooth
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    /**
     * Pirmeiro método a executar depois do Construtor
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	//*************************************************
    	// Executa primeiro o método da classe herdada (classe Superior - extends)
        super.onCreate(savedInstanceState);

        //*************************************************
        // Efetua o Setup da Activity determinando a caracteristica da Tela
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        
        //*************************************************
        // Setando qual o Layout (XML) associado a essa Activity --> CarDroiDuino\res\layout
        setContentView(R.layout.bluedevicelist_layout);

        //*************************************************
        // Setando como Retorno CANCELED como Default para caso o usuário saia da 
        // Activity sem selecionar um MAC Address
        setResult(Activity.RESULT_CANCELED);

        
        //*************************************************
        // Inicializando o botão que irá inicilizar a descoberta de Novos dispositivos
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//*************************************************
            	// Chama Rotina que procura dispositivos novos
                doDiscovery();
                //*************************************************
                // Some com a tela de progresso
                v.setVisibility(View.GONE);
            }
        });

        //*************************************************
        // Inicializando ArrayAdapters para conter os MAC Address dos Dispositivos
        // Um para dispositivos já pareados e outro para dispositivos encontrados
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        //*************************************************
        // Pegando o ListView que irá listar os dispositivos pareados e associandor
        // o ArrayAdapter para ele
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        //*************************************************
        // Associa ao ato de o usuário tocar na linha do ListView a um evento especifico
        // para capturar o MAC address selecionado
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        //*************************************************
        // Pegando o ListView que irá listar os dispositivos encontrados e associandor
        // o ArrayAdapter para ele
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        //*************************************************
        // Associa ao ato de o usuário tocar na linha do ListView a um evento especifico
        // para capturar o MAC address selecionado
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        //*************************************************
        // Registrar para executar o BroadCastReceiver quando um novo dispositivo for
        // encontraado
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        //*************************************************
        // Registrar para executar o BroadCastReceiver quando o Bluetooth terminar de
        // procurar por dispositivos
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        
        //*************************************************
        // Capturando o Adaptador de Bluetooth local
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        //*************************************************
        // Capturando a lista de dispositivos já pareados com o Adaptador de Bluetooth
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        //*************************************************
        // Se possuir dispositivos pareados pega um por um e adiciona ao ArrayAdapter de 
        // dispositivos ja pareados. Do contrario enfia uma String dizendo que não tem ninguem pareado
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
    }

    /**
     * Evento disparado quando a Activity é destruida
     * Para o descobrimento do Bluetooth e desregistra o BroadCast que recebia as mensagens
     * do Sistema Operacional
     */
    @Override
    protected void onDestroy() {
    	//*************************************************
    	// Executa primeiro o método da classe superior (extends)
        super.onDestroy();

        //*************************************************
        // Se ainda estiver descobrindo dispositivos então para todo o processo
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        //*************************************************
        // Desregistra o BroadCastReceiver que recebia as mensagens do Sistema Operacional
        this.unregisterReceiver(mReceiver);
    }

    /**
     * Inicia o descobrimento dos dispositivos Bluetooth ao redor
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");

        //*************************************************
        // Habilita a barra de progresso para mostrar ao usuário que está tentando descobrir dispositivos
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        //*************************************************
        // Exibindo o Titulo de Novos dispositivos para a ListView de novos dispositivos
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        
        //*************************************************
        // Se o descobrimento ja estiver rolando então para ele
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        //*************************************************
        // Solicita ao Adaptador que comece a caçar os dispositivos ao redor
        mBtAdapter.startDiscovery();
    }

    /**
     * Evento disparado quando o usuário seleciona qualquer um dos dispositivos nas 
     * duas listas de dispositivos - Pega o MAC address do dispositivo selecionado
     * e retorna para a CarServerActivity
     */
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	//*************************************************
        	// Cancela o descobrimento 
            mBtAdapter.cancelDiscovery();

            //*************************************************
            // Pega o MAC Address contido na String selecionada
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            //*************************************************
            // Cria o Intent de resultado e adiciona o MAC address selecionado a ele
            Intent intent = new Intent();
            intent.putExtra(SystemProperties.KEY_DEVICE_ADDRESS, address);

            //*************************************************
            /// Indica que o resultado a ser enviado é válido - OK
            setResult(Activity.RESULT_OK, intent);
            
            //*************************************************
            // Finaliza essa Activity enviando o retorna a CarServerActivity
            finish();
        }
    };

    /**
     * Evento disparado quando um novo dispositivo é encontrado ou quando o descobrimento
     * de novos dispositivos é finalizado
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	//*************************************************
        	// Captura a identificacao da acao disparada pelo Sistema
            String action = intent.getAction();

            
            //*************************************************
            // Se o evento for a descoberta de um novo dispositivo...
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	//*************************************************
            	// Pega o dispositivo informado pela Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //*************************************************
                // Se o dispositivo ainda não estiver pareado então adiciona ele ao ArrayAdapter de dispositivos descobertos
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            //*************************************************
            // Se o evento for informar o fim do descobrimento por novos dispositivos...    
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	//*************************************************
            	// Some com a Barra de progresso
                setProgressBarIndeterminateVisibility(false);
                //*************************************************
                // Muda o titulo para pedir que o usuário selecione um dispositivo
                setTitle(R.string.select_device);
                //*************************************************
                // Se não foi descoberto dispositivo algum então coloca uma mensagem no ListView informando
                // que a busca não deu em nada
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

}
