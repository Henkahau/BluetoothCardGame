package one.group.bluetoothcardgame;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class BtMessageActivity extends AppCompatActivity {

    String TAG = "BLUETOOTH TESTI";

    ListView messageList;
    EditText editMessage;
    BluetoothAdapter mBtAdapter;
    Button sendButton;
    ArrayList<Drawable> imageViews = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<ImageView> imageAdapter;
    String bluetoothMessage = "00";

    private boolean accepting = false;

    public static final int REQUEST_ENABLE_BT=1;
    ListView lv_paired_devices;
    Set<BluetoothDevice> set_pairedDevices;
    ArrayAdapter adapter_paired_devices;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice connectedDevice;
    BluetoothSocket socket;
    ConnectedThread mConnectedThread;

    private static final UUID MY_UUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int CONNECTING=2;
    public static final int CONNECTED=3;
    public static final int NO_SOCKET_FOUND=4;

    ArrayList<String> urls;
    private DatabaseReference mDatabase;

    private String[] cards = {
            "anton1",
            "jaakko1",
            "pokka1",
            "pokka2",
            "pokka3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_message);
        urls = new ArrayList<>();
        FirebaseDatabase fb = FirebaseDatabase.getInstance();


        for (String card: cards) {
            mDatabase = fb.getReference().child("cards").child(card).child("image");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imgUrl = dataSnapshot.getValue(String.class);
                    urls.add(imgUrl);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        editMessage = findViewById(R.id.edit_message);
        messageList = findViewById(R.id.text_message);
        sendButton = findViewById(R.id.send_button);

        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_item_layout, R.id.list_item_label, urls);


        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothMessage = ((TextView)view.findViewById(R.id.list_item_label)).getText().toString();
                handler.obtainMessage(MESSAGE_WRITE, socket).sendToTarget();
            }
        });
        messageList.setAdapter(adapter);
        connectedDevice = getIntent().getParcelableExtra("btdevice");

        if (connectedDevice != null) {
            Toast.makeText(getApplicationContext(), connectedDevice.getName(), Toast.LENGTH_LONG).show();
            ConnectThread connectThread = new ConnectThread(connectedDevice);
            connectThread.start();
        }
        else {
            Toast.makeText(getApplicationContext(), "EI OLLU DEVICEE", Toast.LENGTH_LONG).show();
            if (socket != null) {
                startAcceptingConnection();
            }

        }


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothMessage = editMessage.getText().toString();
                handler.obtainMessage(MESSAGE_WRITE, socket).sendToTarget();
            }
        });

    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg_type) {
            super.handleMessage(msg_type);

            switch (msg_type.what) {
                case MESSAGE_READ:
                    byte[] readbuff =(byte[])msg_type.obj;
                    String receivedMessage = new String(readbuff);
                    urls.add(receivedMessage);
                    adapter.notifyDataSetChanged();

                    break;

                case MESSAGE_WRITE:
                    if (msg_type.obj != null) {
                        mConnectedThread.write(bluetoothMessage.getBytes());
                    }

                case CONNECTED:
                    Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                    break;

                case CONNECTING:
                    Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_SHORT).show();
                    break;

                case NO_SOCKET_FOUND:
                    Toast.makeText(getApplicationContext(),"No socket found",Toast.LENGTH_SHORT).show();
                    break;
            }


        }

    };

    private void connectedToServer(BluetoothSocket socket) {
        this.socket = socket;
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        bluetoothMessage = "Hei serveri";
        handler.obtainMessage(MESSAGE_WRITE, socket).sendToTarget();
    }

    private void connectedToClient(BluetoothSocket socket) {
        this.socket = socket;
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        bluetoothMessage = "Hei klientti";
        handler.obtainMessage(MESSAGE_WRITE, socket).sendToTarget();
    }


    public void startAcceptingConnection()
    {
        //call this on button click as suited by you

        AcceptThread acceptThread = new AcceptThread();
        acceptThread.start();
        Toast.makeText(getApplicationContext(),"accepting",Toast.LENGTH_SHORT).show();
    }


    public class AcceptThread extends Thread
    {
        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("NAME",MY_UUID);
            } catch (IOException e) { }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null)
                {
                    // Do work to manage the connection (in a separate thread)
                    handler.obtainMessage(CONNECTED).sendToTarget();
                    connectedToServer(socket);
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;

        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                handler.obtainMessage(CONNECTING).sendToTarget();

                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            //bluetoothMessage = "huu";
            //handler.obtainMessage(MESSAGE_WRITE, mmSocket).sendToTarget();

            connectedToClient(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tempIs = null;
            OutputStream tempOs = null;

            try {
                tempIs = socket.getInputStream();
                tempOs = socket.getOutputStream();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            mmInputStream = tempIs;
            mmOutputStream = tempOs;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInputStream.read(buffer);
                    handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

        }

        public void write(byte[] bytes) {
            try {
                mmOutputStream.write(bytes);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

}
