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

    Button btnOnOff;
    Button pairedBtn;
    Button discoverBtn;

    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> nearbyDevices = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnOnOff = findViewById(R.id.btSwitch);
        pairedBtn = findViewById(R.id.listBtn);
        discoverBtn = findViewById(R.id.searchBtn);

        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.disable();
                    Toast.makeText(getApplicationContext(), "Turned off", Toast.LENGTH_LONG).show();
                    showBtDisabled();
                }
                else {
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, 1000);
                    Toast.makeText(getApplicationContext(), "Turned on", Toast.LENGTH_LONG).show();
                    showBtEnabled();
                }
            }
        });

        if(mBluetoothAdapter.isEnabled()) {
            showBtEnabled();
        }
        else {
            showBtDisabled();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
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

    public void showBtEnabled() {
        btnOnOff.setText("Disable \n Bluetooth");
        pairedBtn.setEnabled(true);
        discoverBtn.setEnabled(true);
    }

    public void showBtDisabled() {
        btnOnOff.setText("Enable \n Bluetooth");
        pairedBtn.setEnabled(false);
        discoverBtn.setEnabled(false);
    }

    public void listPairedDevices(View v) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();


        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
            }
            Intent pairedIntent = new Intent(MainActivity.this, BtListActivity.class);
            pairedIntent.putParcelableArrayListExtra("DEVICE_LIST", deviceList);
            startActivity(pairedIntent);
        }
    }

    public void findBtDevices(View v) {

        mBluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver mReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                nearbyDevices.clear();
                mProgressDialog.show();

            }

            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                if(!nearbyDevices.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Device scan finished",
                            Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();

                    Intent newIntent = new Intent(MainActivity.this, BtListActivity.class);
                    newIntent.putParcelableArrayListExtra("DEVICE_LIST", nearbyDevices);
                    startActivity(newIntent);
                    nearbyDevices.clear();
                }


            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.
                        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(!nearbyDevices.contains(device)) {
                    nearbyDevices.add(device);
                }
            }
            else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);

                if(state == BluetoothAdapter.STATE_ON) {

                }
            }

        }
    };




}
