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

    private ArrayList<String> mUrls;

    public interface ImageUrlRequestDone {
        void urlRequestDone(String urls);
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

    private void getUrlsFromFirebase() {
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        DatabaseReference mDatabase = fdb.getReference().child("cards").child("pokka3").child("image");
        mUrls = new ArrayList<>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                //mUrls.add(value);
                listener.urlRequestDone(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR", databaseError.toString());
            }
        });
    }
}
