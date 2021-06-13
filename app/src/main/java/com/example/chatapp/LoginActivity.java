package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login, signIn;
    private ProgressBar progressBar;
    FirebaseAuth auth;
    TextView resetPass;
    FirebaseUser firebaseUser;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email=email.getText().toString();
                String txt_password=password.getText().toString();
                if(checkInput(txt_email,txt_password)) {
                    login.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(txt_email, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Login fail", Toast.LENGTH_SHORT).show();
                            login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });
    }
    //khởi tạo
    public void Init() {
        email = findViewById(R.id.edt_email_login);
        password = findViewById(R.id.edt_password_login);
        login = findViewById(R.id.btn_login_login);
        signIn = findViewById(R.id.btn_signIn_login);
        progressBar=findViewById(R.id.progressBar);
        resetPass=findViewById(R.id.resetPass);
        auth = FirebaseAuth.getInstance();
    }
    private boolean checkInput(String Email,String Password){
        Email      = email.getText().toString();
        Password   = password.getText().toString();
        if (Email.trim().isEmpty()){
            Toast.makeText(this, "Email is not Empty!", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            Toast.makeText(this, "Valid email", Toast.LENGTH_SHORT).show();
            return false;
        }else if (Password.isEmpty()){
            Toast.makeText(this, "Password is not empty", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }
    private void createRequest(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
}
