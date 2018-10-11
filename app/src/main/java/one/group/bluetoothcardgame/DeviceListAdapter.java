package one.group.bluetoothcardgame;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DeviceListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<BluetoothDevice> mBtDeviceData;
    private OnPairButtonListener listener = null;


    public DeviceListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setBtDeviceData(List<BluetoothDevice> data) {
        this.mBtDeviceData = data;
    }

    public void setListener(OnPairButtonListener listener) {
        this.listener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            holder.deviceTextView = (TextView)convertView.findViewById(R.id.list_item_label);

            BluetoothDevice btDevice = mBtDeviceData.get(position);
            holder.deviceTextView.setText(btDevice.getName() + btDevice.getAddress().toString());
            holder.deviceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPairButtonClick(position);
                    }
                }
            });
        }
        return convertView;
    }

    static class ViewHolder {
        TextView deviceTextView;
    }

    public interface OnPairButtonListener {
        void onPairButtonClick(int position);
    }
}
