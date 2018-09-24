package com.example.henkka.bluetoothexample;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btnOn;
    Button btnOf;
    Switch aSwitch;
    ListView aListView;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> pairedDevices;
    ArrayList<BluetoothDevice> nearbyDevices;
    ArrayAdapter<String> btArrayAdapter;

    private ProgressDialog mProgressDialog;

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                nearbyDevices = new ArrayList<>();
                mProgressDialog.show();

            }

            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //unregisterReceiver(mReciever);
                Toast.makeText(getApplicationContext(), "Device scan finished",
                        Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();

                if(nearbyDevices.size()>0) {
                    Intent newIntent = new Intent(MainActivity.this, BtListActivity.class);
                    newIntent.putParcelableArrayListExtra("DEVICE_LIST", nearbyDevices);
                    startActivity(newIntent);

                }
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.
                        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                nearbyDevices.add(device);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aSwitch = (Switch)findViewById(R.id.btSwitch);
        aListView = (ListView)findViewById(R.id.listPairedDevices);
        findViewById(R.id.listBtn);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Scanning for nearby Bluetooth devices");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog.dismiss();
                        mBluetoothAdapter.cancelDiscovery();
                    }
                });

        boolean btStatus = mBluetoothAdapter.isEnabled();
        aSwitch.setChecked(btStatus);

        //IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(mReciever, intentFilter);

        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aSwitch.isChecked()) {
                    setBtOn(v);
                }else {
                    setBtOff(v);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReciever, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciever);
    }

    public void setBtOn(View v) {

        if(!mBluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
            //listPairedDevices(v);
        }else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
            //listPairedDevices(v);
        }

    }

    public void setBtOff(View v) {
        mBluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
    }

    public void listPairedDevices(View v) {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<String> deviceList = new ArrayList<String>();
        btArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item_layout,
                R.id.list_item_label, deviceList);

        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                deviceList.add(device.getName());
            }
            aListView.setAdapter(btArrayAdapter);
        }
    }

    public void findBtDevices(View v) {

        mBluetoothAdapter.startDiscovery();
    }



}
