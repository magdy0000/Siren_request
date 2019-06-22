package com.free.project.siren;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText username;
    private AppCompatEditText password;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;
    private AppCompatImageButton loginBtn;
    private AppCompatImageButton signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializations :
        FirebaseApp.initializeApp(this);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_button);
        mLoginProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        signupBtn = findViewById(R.id.register_button);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginProcess();

            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

    }

    private void loginProcess() {

        String user_name = username.getText().toString();
        String user_password = password.getText().toString();

        if (!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_password)){


            mLoginProgress.setTitle("Loading .. Please wait :)");
            mLoginProgress.setMessage("Please wait while logging in .");
            mLoginProgress.setCanceledOnTouchOutside(false);
            mLoginProgress.show();



            mAuth.signInWithEmailAndPassword(user_name, user_password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                mLoginProgress.dismiss();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toast.makeText(LoginActivity.this, "Login Success!", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                                //updateUI(null);
                                mLoginProgress.dismiss();
                            }

                            // ...
                        }
                    });



        }else if (TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(user_password)){
            Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        }else if (!TextUtils.isEmpty(user_name) && TextUtils.isEmpty(user_password)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(user_name) && TextUtils.isEmpty(user_password)){
            Toast.makeText(this, "Please Fill Information", Toast.LENGTH_SHORT).show();
        }





    }


}