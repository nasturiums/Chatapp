package com.example.chatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.Model.Chat;
import com.example.chatapp.Model.User;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUser;
    private boolean isChat;
    private boolean isseen;
    String lastMg;
    public UserAdapter(Context mContext,List<User>mUser,boolean isChat) {
        this.mUser=mUser;
        this.mContext=mContext;
        this.isChat=isChat;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        User user=mUser.get(position);
        holder.username.setText(user.getName());
        //holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
        }
        if(isChat) {
            if (user.getStatus().equals("Online")) {
                holder.image_on.setVisibility(View.VISIBLE);
                holder.image_off.setVisibility(View.GONE);
            } else {
                holder.image_on.setVisibility(View.GONE);
                holder.image_off.setVisibility(View.VISIBLE);
            }
        }else {
            holder.image_on.setVisibility(View.GONE);
            holder.image_off.setVisibility(View.GONE);
        }
        if(isChat){
            checkLastMessage(user.getId(),holder.last_mg);
        }else {
            holder.last_mg.setVisibility(View.GONE);
        }
        MessageIsSeen(user.getId(),holder.last_mg,holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView image_on;
        private ImageView image_off;
        private TextView last_mg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.image);
            image_on=itemView.findViewById(R.id.image_on);
            image_off=itemView.findViewById(R.id.image_off);
            last_mg=itemView.findViewById(R.id.lastMessage);
        }
    }
    private void MessageIsSeen(String userID,TextView last_mg,UserAdapter.ViewHolder holder){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot :snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userID)){
                        if(chat.isIsseen()==false){
                            holder.last_mg.setTypeface(Typeface.create("serif-monospace", Typeface.BOLD));
                        }
                        else{
                            holder.last_mg.setTypeface(Typeface.create("serif-monospace", Typeface.NORMAL));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkLastMessage(String userID,TextView last_mg){
        lastMg="default";
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat=dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userID)||
                            chat.getReceiver().equals(userID)&&chat.getSender().equals(firebaseUser.getUid())){
                        lastMg=chat.getMessage();
                    }
                }
                switch(lastMg){
                    case "default":
                        last_mg.setText("No message");
                        break;
                    default:
                        last_mg.setText(lastMg);
                        break;
                }
                lastMg="default";
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
