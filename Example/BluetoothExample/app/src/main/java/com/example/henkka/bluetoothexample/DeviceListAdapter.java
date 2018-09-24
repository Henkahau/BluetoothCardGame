package com.example.henkka.bluetoothexample;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<BluetoothDevice> mBtDeviceData;


    public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setBtDeviceData(List<BluetoothDevice> data) {
        this.mBtDeviceData = data;
    }


    @Override
    public int getCount() {
        return (mBtDeviceData == null) ? 0:mBtDeviceData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.deviceTextView = (TextView)convertView.findViewById(R.id.list_item_label);

            BluetoothDevice btDevice = mBtDeviceData.get(position);
            holder.deviceTextView.setText(btDevice.getName() + btDevice.getAddress().toString());
        }
        return convertView;
    }

    static class ViewHolder {
        TextView deviceTextView;
    }
}
