package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText name,email,password,confirmPass;
    private Button register,cancel;
    private String Name ,Email, Password, ConfirmPassword;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Init();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                if(checkInput()){
                    Register(Name,Email,Password,ConfirmPassword);
                }
                else {
                    register.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void Register(String name,String email,String password,String ConfirmPassword){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String userID=firebaseUser.getUid();
                            reference= FirebaseDatabase.getInstance().getReference("User").child(userID);
                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("id",userID);
                            hashMap.put("name",name);
//                            hashMap.put("email",email);
//                            hashMap.put("password",password);
                            hashMap.put("imageURL","default");
                            hashMap.put("status","Offline");
                            hashMap.put("search",name.toLowerCase());
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Register fail!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
    }
    void Init(){
        name=findViewById(R.id.edt_name_register);
        email=findViewById(R.id.edt_email_register);
        password=findViewById(R.id.edt_password_register);
        confirmPass=findViewById(R.id.edt_confirmPass_register);
        register=findViewById(R.id.btn_register);
        cancel=findViewById(R.id.btn_cancel);
        progressBar=findViewById(R.id.progressBar);
        auth=FirebaseAuth.getInstance();
    }
    boolean checkInput(){
        Name       = name.getText().toString();
        Email      = email.getText().toString();
        Password   = password.getText().toString();
        ConfirmPassword = confirmPass.getText().toString();
        if (TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Name is empty!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(Email)){
            Toast.makeText(this, "Email is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(Password)){
            Toast.makeText(this, "Password is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(ConfirmPassword)) {
            Toast.makeText(this, "ConfirmPassword is empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (!Password.equals(ConfirmPassword)){
            Toast.makeText(this, "ConfirmPassword not correct!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}