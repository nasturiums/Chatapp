package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatapp.Adapter.APIservice;
import com.example.chatapp.Adapter.MessageAdapter;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Client;
import com.example.chatapp.Notification.Data;
import com.example.chatapp.Notification.MyRespone;
import com.example.chatapp.Notification.Sender;
import com.example.chatapp.Notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username,text_send;
    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;
    ImageButton send;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;

    APIservice apIservice;
    String userID;

    Boolean notifi=false;
    ValueEventListener seenlistener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //tool bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // dấu quay lại
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        apIservice= Client.getClient("https://fcm.googleapis.com/").create(APIservice.class);


        profile_image=findViewById(R.id.image_profile);
        username=findViewById(R.id.username);
        text_send=findViewById(R.id.text_send);
        send=findViewById(R.id.btn_send);
        recyclerView=findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        intent=getIntent();
        userID=intent.getStringExtra("userid");
        //send message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifi=true;
                String msg=text_send.getText().toString();
                if(msg!=" "){
                    sendMessage(fuser.getUid(),userID,msg);
                }else {
                    Toast.makeText(MessageActivity.this, "You can't send empty Message!", Toast.LENGTH_SHORT).show();
                }
                text_send.setText(" ");
            }
        });
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference=FirebaseDatabase.getInstance().getReference("User").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                username.setText(user.getName());
                //profile_image.setImageResource(R.mipmap.ic_launcher);
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                }
                readMessage(fuser.getUid(),userID,user.getImageURL());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SeenMessages(userID);
    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        hashMap.put("isseen",false);
        reference.child("Chats").push().setValue(hashMap);
        //add user for chatfragment
        DatabaseReference chatRef=FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fuser.getUid())
                .child(userID);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String msg=message;
        reference=FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user=snapshot.getValue(User.class);
                if(notifi) {
                    sendNotification(receiver, user.getId(), msg);
                }
                notifi=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void sendNotification(String receiver,String usename,String message){
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Token");
        Query query=tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Token token=dataSnapshot.getValue(Token.class);
                    Data data=new Data(fuser.getUid(),R.mipmap.ic_launcher,usename+" : "+message,"New Message",userID);
                    Sender sender=new Sender(data,token.getToken());

                    apIservice.sendNotification(sender).enqueue(new Callback<MyRespone>() {
                        @Override
                        public void onResponse(Call<MyRespone> call, Response<MyRespone> response) {
                            if(response.code()==200){
                                if(response.body().success!=1){
                                    Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<MyRespone> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void readMessage(String myID,String userID,String imageurl){
        mChat=new ArrayList<>();
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren() ){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myID)&&chat.getSender().equals(userID)||
                            chat.getReceiver().equals(userID)&&chat.getSender().equals(myID)){
                        mChat.add(chat);
                    }
                }
                messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageurl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SeenMessages(String userID){
        reference=FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener=reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid())&&chat.getSender().equals(userID)){
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("isseen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void status(String status){
        reference= FirebaseDatabase.getInstance().getReference("User").child(fuser.getUid());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenlistener);
        status("Offline");
    }
}