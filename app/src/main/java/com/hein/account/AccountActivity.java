package com.hein.account;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity  {
//    private ActivityMainBinding binding;
    FirebaseFirestore dbroot;
    protected ImageView avatar;
    protected String user_document;
    protected TextView headname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_main);

        configureDetailButton();
        dbroot=FirebaseFirestore.getInstance();
         avatar = findViewById(R.id.avatarImage);
         headname= findViewById(R.id.UsernameView);
        fetchUser();
    }
    public void configureDetailButton(){
        LinearLayout backButton = (LinearLayout) findViewById(R.id.button_detail);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this,AccountDetail.class));
            }
        });
    }

    public void fetchUser(){
        user_document= "xnJNFIe8KoGheyJXrLWf";
        DocumentReference document =dbroot.collection("User").document(user_document);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                headname.setText(documentSnapshot.getString("name"));
                Glide.with(getApplicationContext()).load(documentSnapshot.getString("avatar")).into(avatar);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}
