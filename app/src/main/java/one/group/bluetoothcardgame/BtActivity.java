package one.group.bluetoothcardgame;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Set;

public class BtActivity extends AppCompatActivity {


    Button btnOnOff;
    Button pairedBtn;
    Button discoverBtn;
    Button listenButton;
    TextView pairedDevice;


    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> nearbyDevices = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);


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

        pairedDevice = findViewById(R.id.paired_device_text_view);
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

        listenButton = findViewById(R.id.messagingButton);
        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BtActivity.this, BtMessageActivity.class);
                startActivity(intent);
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
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciever);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }


    public void showBtEnabled() {
        btnOnOff.setText("Disable \n Bluetooth");
        pairedBtn.setEnabled(true);
        discoverBtn.setEnabled(true);
        listenButton.setEnabled(true);
    }

    public void showBtDisabled() {
        btnOnOff.setText("Enable \n Bluetooth");
        pairedBtn.setEnabled(false);
        discoverBtn.setEnabled(false);
        listenButton.setEnabled(false);
    }

    public void listPairedDevices(View v) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> deviceList = new ArrayList<>();


        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
            }
            Intent pairedIntent = new Intent(BtActivity.this, BtListActivity.class);
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

                    Intent newIntent = new Intent(BtActivity.this, BtListActivity.class);
                    newIntent.putParcelableArrayListExtra("DEVICE_LIST", nearbyDevices);
                    startActivity(newIntent);
                    nearbyDevices.clear();
                }


            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.
                        getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(!nearbyDevices.contains(device) && device.getName() != null) {
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
