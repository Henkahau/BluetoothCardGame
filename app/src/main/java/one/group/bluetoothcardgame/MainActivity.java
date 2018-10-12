package one.group.bluetoothcardgame;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;





    private String[] cardIDs = {
        "id1",
        "id2"
    };

    ArrayList<String> cards;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();



        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d("testi", "User" + currentUser.getUid());

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            mDatabase = database.getReference();

            mDatabase.child("user").child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot uniqueKeySnapshot : dataSnapshot.getChildren()){
                        //Loop 1 to go through all the child nodes of users

                        Log.d("kortit", "Kortti" + uniqueKeySnapshot);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        final ImageView tv = findViewById(R.id.hello);
        FirebaseDatabase fdb = FirebaseDatabase.getInstance();
        mDatabase = fdb.getReference().child("cards").child("pokka3").child("image");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String value = dataSnapshot.getValue(String.class);
                Glide.with(MainActivity.this)
                        .load(value)
                        .into(tv);

            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                 Log.e("ERROR", databaseError.toString());
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BtActivity.class);
                startActivity(intent);
            }
        });
    }

}
