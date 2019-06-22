package com.free.project.siren;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private AppCompatEditText mFirstName,mLastName,mPhoneNumber,mEmail;
    private AppCompatImageButton mSaveBtn;
    private AppCompatTextView mChangePhoto;
    private CircleImageView mDisplayImage;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private static final int GALLERY_PICK = 1;
    private ProgressDialog mProgressDialog;
    private String download_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirstName = findViewById(R.id.profile_first_name);
        mLastName = findViewById(R.id.profile_last_name);
        mPhoneNumber = findViewById(R.id.profile_phone_number);
        mEmail = findViewById(R.id.profile_email);
        mChangePhoto = findViewById(R.id.profile_change_picture);
        mDisplayImage = findViewById(R.id.profile_account_image);
        mSaveBtn = findViewById(R.id.save_button);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();


        try {
            String current_uid = mCurrentUser.getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
            mUserDatabase.keepSynced(true);
        }catch (NullPointerException ignored){}

        
        
        retrieveData();

        AppCompatImageButton exitBtn = findViewById(R.id.profile_exit_button);
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);

            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mProgressDialog = new ProgressDialog(ProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image ..");
                mProgressDialog.setMessage("Please wait while upload and process the image :) <3");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                savingData();

            }
        });


    }

    private void savingData() {


        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        if (!TextUtils.isEmpty(firstName) ||  !TextUtils.isEmpty(firstName) || !TextUtils.isEmpty(firstName)) {

            Map updateData = new HashMap();
            updateData.put("first_name", firstName);
            updateData.put("last_name", lastName);
            updateData.put("phone_number", phoneNumber);


            mUserDatabase.updateChildren(updateData).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful()) {


                        mProgressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Sucess Uploaded!", Toast.LENGTH_SHORT).show();

                    } else {

                        mProgressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                    }


                }

            });

        }else {
            Toast.makeText(this, "Please Fill Info !", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();


            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(ProfileActivity.this);
                mProgressDialog.setTitle("Uploading Image ..");
                mProgressDialog.setMessage("Please wait while upload and process the image :) <3");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                String current_user_id = mCurrentUser.getUid();


                final StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){
                            download_url = task.getResult().toString();
                            updateData();

                        }else {
                            Toast.makeText(ProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
              //  Log.e("upload image error :", error.toString());
            }
        }
    }

    private void updateData() {

        Map update_hashMap = new HashMap();
        update_hashMap.put("image",download_url);

        mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (task.isSuccessful()){


                    mProgressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Sucess Uploaded!", Toast.LENGTH_SHORT).show();

                }else {

                    Toast.makeText(ProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();

                }


            }

        });
    }

/*


*/


    private void retrieveData() {

        try {

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String first_name = dataSnapshot.child("first_name").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();
                    String last_name = dataSnapshot.child("last_name").getValue().toString();
                    String phone_number = dataSnapshot.child("phone_number").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();


                    mFirstName.setText(first_name);
                    mLastName.setText(last_name);
                    mPhoneNumber.setText(phone_number);
                    mEmail.setText(email);

                    if (!image.equals("default")) {

                        Picasso.get().load(image).placeholder(R.mipmap.logo).into(mDisplayImage);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (NullPointerException ignored){
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }


    }
}
