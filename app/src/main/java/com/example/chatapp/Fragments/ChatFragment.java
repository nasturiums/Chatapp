package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapter.UserAdapter;
import com.example.chatapp.Model.ChatLists;
import com.example.chatapp.Model.User;
import com.example.chatapp.Notification.Token;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> muUser;
    FirebaseUser fuser;
    DatabaseReference reference;
    private List<ChatLists> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView=view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        userList=new ArrayList<>();
        //Lấy user mà đã chat
        reference= FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ChatLists chatLists=dataSnapshot.getValue(ChatLists.class);
                    userList.add(chatLists);

                }
                chatLists();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }
    private void updateToken(String token){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Token");
        Token token1=new Token(token);
        reference.child(fuser.getUid()).setValue(token1);
    }

    private void chatLists() {
        muUser=new ArrayList<>();
       reference=FirebaseDatabase.getInstance().getReference("User");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                muUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatLists chatLists:userList){
                        if(user.getId().equals(chatLists.getId())){
                            muUser.add(user);
                        }
                    }
                }
                userAdapter=new UserAdapter(getContext(),muUser,true);
                recyclerView.setAdapter(userAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
//    private void readChat() {
//        muUser=new ArrayList<>();
//        reference=FirebaseDatabase.getInstance().getReference("User");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                muUser.clear();
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User user=dataSnapshot.getValue(User.class);
//                    //Display 1 user from chat
//                    for (String id:userList){
//                        assert user != null;
//                        if(user.getId().equals(id)){
//                            if(muUser.size()!=0){
//                                int flag=0;
//                                for(User u : muUser) {
//                                    if (user.getId().equals(u.getId())) {
//                                        flag = 1;
//                                        break;
//                                    }
//                                }
//                                if(flag==0)
//                                    muUser.add(user);
//                            }else{
//
//                                muUser.add(user);
//                            }
//                        }
//                    }
//                }
//                userAdapter=new UserAdapter(getContext(),muUser,true);
//                recyclerView.setAdapter(userAdapter);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

}