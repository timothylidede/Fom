package com.savala.fom;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import adapter.AdapterPackChat;
import models.ModelPackChat;

public class PackChat extends AppCompatActivity {
    private String packId;
    private ImageView mBack;
    private TextView mPackChatName;
    private LinearLayout mChatLayout;
    private ImageButton mAttachButton, mSendButton;
    private EditText mMessageEditText;
    private RecyclerView mChatRv;
    private ArrayList<ModelPackChat> packChatList;
    private AdapterPackChat adapterPackChat;

    private ImageButton mFomButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pack_chat);

        Intent intent = getIntent();
        packId = intent.getStringExtra("packId");

        mBack = findViewById(R.id.back);
        mPackChatName = findViewById(R.id.pack_chat_name);
        mChatLayout = findViewById(R.id.chat_layout);
        mSendButton = findViewById(R.id.send_button);
        mMessageEditText = findViewById(R.id.message_edit_text);
        mChatRv = findViewById(R.id.chatRv);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        firebaseAuth = FirebaseAuth.getInstance();
        loadGroupInfo();
        loadGroupMessages();

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageEditText.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(PackChat.this, "Type something", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });
    }

    private void loadGroupMessages() {
        //init list
        packChatList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_messages");
        ref.child(packId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                packChatList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelPackChat modelPackChat = ds.getValue(ModelPackChat.class);
                    packChatList.add(modelPackChat);
                }
                adapterPackChat = new AdapterPackChat(PackChat.this, packChatList);
                mChatRv.setAdapter(adapterPackChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timestamp = ""+ System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", ""+firebaseAuth.getUid());
        hashMap.put("message", ""+message);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("type", "" + "text");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pack_messages");
        ref.child(packId).child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        mMessageEditText.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PackChat.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("packs");
        ref.orderByChild("pack_id").equalTo(packId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String packName = "" + ds.child("pack_name").getValue();
                            mPackChatName.setText(packName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}