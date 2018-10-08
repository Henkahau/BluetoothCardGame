package com.example.henkka.bluetoothexample;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BtListActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayList<BluetoothDevice> mBtDeviceList;
    private DeviceListAdapter mBtDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_list);

        mBtDeviceList = getIntent().getParcelableArrayListExtra("DEVICE_LIST");
        mListView = (ListView)findViewById(R.id.btListView);

        mBtDeviceAdapter = new DeviceListAdapter(this);
        mBtDeviceAdapter.setBtDeviceData(mBtDeviceList);

        mBtDeviceAdapter.setListener(new DeviceListAdapter.OnPairButtonListener() {
            @Override
            public void onPairButtonClick(int position) {
                BluetoothDevice device = mBtDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    showToast("UNPAIR");
                    unPairDevices(device);
                }
                else {
                    showToast("Pairing Devices");
                    pairDevices(device);
                }
            }
        });


        mListView.setAdapter(mBtDeviceAdapter);
        registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mPairReceiver);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);

                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
                        BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    showToast("Paired");
                }
                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    showToast("Unpaired");
                }
                mBtDeviceAdapter.notifyDataSetChanged();
            }
        }
    };

    private void pairDevices(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[])null);
            method.invoke(device, (Object[])null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unPairDevices(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[])null);
            method.invoke(device, (Object[])null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
