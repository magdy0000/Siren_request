package com.free.project.siren;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity{

    private AppCompatEditText mFirstName,mLastName,mPhoneNumber,mEmail,mPassword;
    private AppCompatImageButton mSaveBtn,mExitBtn;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mFirstName = findViewById(R.id.register_first_name);
        mLastName = findViewById(R.id.register_last_name);
        mPhoneNumber = findViewById(R.id.register_phone_number);
        mEmail = findViewById(R.id.register_email);
        mPassword = findViewById(R.id.register_password);
        mSaveBtn = findViewById(R.id.register_save_button);
        mExitBtn = findViewById(R.id.register_exit_button);


        mRegProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                creatNewAccount();

            }
        });
    }

    private void creatNewAccount() {

        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();


        if (!TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(lastName) || !TextUtils.isEmpty(phoneNumber) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

            mRegProgress.setTitle("Registering User");
            mRegProgress.setMessage("Please wait while we create your account !");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.show();

            registerUser(firstName,lastName,phoneNumber,email,password);

        } else {

            Toast.makeText(this, "Please Fill your Information!", Toast.LENGTH_SHORT).show();
        }




    }

    private void registerUser(final String firstName, final String lastName, final String phoneNumber, final String email, String password) {


        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){



                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);


                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("first_name", firstName);
                    userMap.put("last_name", lastName);
                    userMap.put("image", "default");
                    userMap.put("phone_number", phoneNumber);
                    userMap.put("email", email);


                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                mRegProgress.dismiss();

                                Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });



                }else {
                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }


            }
        });



    }

}
