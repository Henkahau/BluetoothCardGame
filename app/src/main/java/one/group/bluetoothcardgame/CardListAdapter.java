package one.group.bluetoothcardgame;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class CardListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private List<String> cardUrlListData;

    private OnSendClickListener listener = null;

    public CardListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    public void setCardUrlList(List<String> cardUrlList) {
        this.cardUrlListData = cardUrlList;
    }

    public void setListener(OnSendClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return (cardUrlListData == null) ? 0:cardUrlListData.size();
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
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.image_item_layout, null);
            holder = new ViewHolder();
            holder.cardImageView = (ImageView)convertView.findViewById(R.id.image_label);

            String imageUrl = cardUrlListData.get(position);

            Glide.with(getView(position, convertView, parent))
                    .asDrawable()
                    .load(imageUrl)
                    .into(holder.cardImageView);

            holder.cardImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.imageClickedToSend(position);
                    }
                }
            });


        }
        return convertView;
    }

    public String getImageUrl(int position) {
        return cardUrlListData.get(position);
    }

    public void getCardListContent() {
       for (String url: cardUrlListData) {
           Log.e("CARDLISTURLS", url+ " " + cardUrlListData.indexOf(url));
       }
    }

    public void updateCardList(List<String> newCardList) {
//        cardUrlListData.clear();
//        cardUrlListData.addAll(newCardList);
        cardUrlListData = newCardList;
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView cardImageView;
    }

    public interface OnSendClickListener {
        void imageClickedToSend(int imagePosition);
    }


}
