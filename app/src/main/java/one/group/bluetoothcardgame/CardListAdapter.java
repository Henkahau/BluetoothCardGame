package one.group.bluetoothcardgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CardListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private ArrayList<String> cardUrlList;

    private OnSendClickListener listener = null;

    public CardListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setCardUrlList(ArrayList<String> cardUrlList) {
        this.cardUrlList = cardUrlList;
    }

    public void setListener(OnSendClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return (cardUrlList == null) ? 0:cardUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_layout, null);
            holder = new ViewHolder();
            //holder.cardImageView = (ImageView)convertView.findViewById(R.id.list_item_label);
            holder.cardTextView = (TextView)convertView.findViewById(R.id.list_item_label);

            String imageUrl = cardUrlList.get(position);
            holder.cardTextView.setText(imageUrl);
//            Glide.with(convertView)
//                    .load(imageUrl)
//                    .into(holder.cardImageView);

//            holder.cardImageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null) {
//                        listener.imageClickedToSend(position);
//                    }
//                }
//            });


        }
        return null;
    }

    static class ViewHolder {
        //ImageView cardImageView;
        TextView cardTextView;
    }

    public interface OnSendClickListener {
        void imageClickedToSend(int imagePosition);
    }


}
