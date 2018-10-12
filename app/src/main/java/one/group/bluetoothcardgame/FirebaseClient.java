package one.group.bluetoothcardgame;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseClient extends Thread {

    private List mUrls;

    private String[] cards = {
            "anton1",
            "jakko1",
            "pokka1",
            "pokka2",
            "pokka3"
    };

    public interface ImageUrlRequestDone {
        void urlRequestDone(List urlList);
    }

    private ImageUrlRequestDone listener = null;

    public FirebaseClient(ImageUrlRequestDone listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        if (listener != null) {
            getUrlsFromFirebase();
        }
    }

    public void getUrlsFromFirebase() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        mUrls = new ArrayList<>();

        for (String card: cards) {
            DatabaseReference mDatabase = fdb.getReference().child("cards").child(card).child("image");

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    mUrls.add(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ERROR", databaseError.toString());
                }
            });
        }
        listener.urlRequestDone(mUrls);
    }
}
