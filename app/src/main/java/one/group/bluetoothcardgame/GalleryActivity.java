package one.group.bluetoothcardgame;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements FirebaseClient.ImageUrlRequestDone {

    ListView cardListView;
    List<String> mCardUrls = new ArrayList<>();
    FirebaseClient fbThread;
    CardListAdapter mCardListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Gallery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cardListView = findViewById(R.id.card_list);

        mCardListAdapter = new CardListAdapter(this);
        mCardListAdapter.setCardUrlList(mCardUrls);
        cardListView.setAdapter(mCardListAdapter);
        mCardListAdapter.setListener(new CardListAdapter.OnSendClickListener() {
            @Override
            public void imageClickedToSend(int imagePosition) {
                //  bluetoothMessage = mCardListAdapter.getImageUrl(imagePosition);
                //   mCardUrls.remove(imagePosition);
                //   handler.obtainMessage(MESSAGE_WRITE, socket).sendToTarget();
                updateUi();
            }
        });

        fbThread = new FirebaseClient(this);
        fbThread.start();
    }

    private void updateUi() {
        mCardListAdapter.notifyDataSetChanged();
    }

    @Override
    public void urlRequestDone(String urls) {
        mCardUrls.add(urls);
        updateUi();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}

