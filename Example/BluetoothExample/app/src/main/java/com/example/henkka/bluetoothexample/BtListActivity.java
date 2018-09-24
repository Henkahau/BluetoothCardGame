package com.example.henkka.bluetoothexample;

import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
        mListView.setAdapter(mBtDeviceAdapter);

    }
}
