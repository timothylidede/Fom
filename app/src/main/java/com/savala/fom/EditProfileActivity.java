package com.savala.fom;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.User;
import models.UserAccountSettings;
import models.UserSettings;
import utils.FilePaths;
import utils.FirebaseMethods;
import utils.Permissions;

import static android.content.ContentValues.TAG;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CAMERA_PERMISSION = 1;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE_1 = 2;
    private static final int REQUEST_CODE_SELECT_IMAGE_2 = 3;
    private static final int REQUEST_CODE_SELECT_IMAGE_3 = 4;
    private static final int REQUEST_CODE_SELECT_IMAGE_1_CAMERA = 5;
    private static final int REQUEST_CODE_SELECT_IMAGE_2_CAMERA = 6;
    private static final int REQUEST_CODE_SELECT_IMAGE_3_CAMERA = 7;
    private static final int REQUEST_CODE_WRITE_PERMISSION = 5;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private Uri selectedImageUriOne, selectedImageUriTwo, selectedImageUriThree;

    private EditText mEditName;
    private TextView mEditYear, mEditEmail, mContactEdit;
    private CardView  mCardEditName, mCardChangePassword;
    private ImageView photoOneEditProfile;
    private ImageView photoTwoEditProfile;
    private ImageView photoThreeEditProfile;
    private ProgressBar mEditProgressBar;

    private Button mSubmitChangesButton;

    private String userID;
    private UserSettings mUserSettings;

    //constants
    private static final  int VERIFY_PERMISSIONS_REQUEST = 1;
    private double mPhotoUploadProgress = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);
        mCardEditName = (CardView) findViewById(R.id.card_edit_name);
        mCardChangePassword = (CardView) findViewById(R.id.card_change_password);

        mEditName = (EditText) findViewById(R.id.edit_name);

        mEditProgressBar = (ProgressBar) findViewById(R.id.editProgressBar);
        mEditYear = (TextView) findViewById(R.id.edit_year);
        mEditEmail = (TextView) findViewById(R.id.edit_email_address);
        mContactEdit = (TextView) findViewById(R.id.contact_edit);

        mSubmitChangesButton = (Button) findViewById(R.id.submit_changes_button);

        photoOneEditProfile = (ImageView) findViewById(R.id.imageView1_editProfile);
        photoTwoEditProfile = (ImageView) findViewById(R.id.imageView2_editProfile);
        photoThreeEditProfile = (ImageView) findViewById(R.id.imageView3_editProfile);

        mFirebaseMethods = new FirebaseMethods(EditProfileActivity.this);

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        setupFirebaseAuth();

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                System.out.println("Button Clicked");

                finish();
            }

        });

        mCardChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ResetPassword.class);
                startActivity(intent);
            }
        });

        mSubmitChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
            }
        });

        mContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, Settings.class);
                startActivity(intent);
            }
        });

        if(checkPermissionsArray(Permissions.PERMISSIONS)){

        }else{
            verifyPermissions(Permissions.PERMISSIONS);
        }

        photoOneEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA_PERMISSION
                    );
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_PERMISSION
                    );
                }else{
                    selectImage1();
                }
            }
        });

        photoTwoEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA_PERMISSION
                    );
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_PERMISSION
                    );
                }else{
                    selectImage2();
                }
            }
        });

        photoThreeEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_CAMERA_PERMISSION
                    );
                }else if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            EditProfileActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_PERMISSION
                    );
                }else{
                    selectImage3();
                }
            }
        });

    }

    private void selectImage1(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_1);
        }
    }

    private void selectImage2(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_2);
        }
    }

    private void selectImage3(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE_3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && requestCode == REQUEST_CODE_CAMERA_PERMISSION && requestCode == REQUEST_CODE_WRITE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage1();
                selectImage2();
                selectImage3();
            }else{
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE_1 && resultCode == RESULT_OK){
            if(data != null && data.getData() != null){
                selectedImageUriOne = data.getData();
                try{
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    selectedImageUriOne);
                    photoOneEditProfile.setImageBitmap(bitmap);

                }catch(Exception e){
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == REQUEST_CODE_SELECT_IMAGE_2 && resultCode == RESULT_OK){
            if(data != null && data.getData() != null){
                selectedImageUriTwo = data.getData();
                try{
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    selectedImageUriTwo);
                    photoTwoEditProfile.setImageBitmap(bitmap);

                }catch(Exception e){
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == REQUEST_CODE_SELECT_IMAGE_3 && resultCode == RESULT_OK){
            if(data != null && data.getData() != null){
                selectedImageUriThree = data.getData();
                try{
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    getContentResolver(),
                                    selectedImageUriThree);
                    photoThreeEditProfile.setImageBitmap(bitmap);

                }catch(Exception e){
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadPhotoOne(){
        if(selectedImageUriOne != null){
            try{

                ProgressDialog progressDialog
                        = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                FilePaths filePaths = new FilePaths();

                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_one");
                UploadTask uploadTask = storageReference.putFile(selectedImageUriOne);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Uri firebaseUrl = task.getResult();

                                    setImageOne(firebaseUrl.toString());
                                    Toast.makeText(EditProfileActivity.this, "Photo upload successful", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Handle failures
                                    // ...
                                    Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //insert into user_account_settings node
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed");
                        Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                        if(progress - 25 > mPhotoUploadProgress){
                            Toast.makeText(EditProfileActivity.this, "Uploading " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageOne(String url){
        Log.d(TAG, "setImageOne: setting new profile image " + url);

        myRef.child(EditProfileActivity.this.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(EditProfileActivity.this.getString(R.string.image_one))
                .setValue(url);
    }

    private void uploadPhotoTwo(){
        if(selectedImageUriTwo != null){
            try{

                ProgressDialog progressDialog
                        = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                FilePaths filePaths = new FilePaths();

                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_two");
                UploadTask uploadTask = storageReference.putFile(selectedImageUriTwo);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Uri firebaseUrl = task.getResult();

                                    setImageTwo(firebaseUrl.toString());
                                    Toast.makeText(EditProfileActivity.this, "Photo upload successful", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Handle failures
                                    // ...
                                    Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //insert into user_account_settings node
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed");
                        Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                        if(progress - 25 > mPhotoUploadProgress){
                            Toast.makeText(EditProfileActivity.this, "Uploading " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageTwo(String url){
        Log.d(TAG, "setImageTwo: setting new profile image " + url);

        myRef.child(EditProfileActivity.this.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(EditProfileActivity.this.getString(R.string.image_two))
                .setValue(url);
    }

    private void uploadPhotoThree(){
        if(selectedImageUriThree != null){
            try{
                ProgressDialog progressDialog
                        = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                FilePaths filePaths = new FilePaths();

                String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final StorageReference storageReference = mStorageReference
                        .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_three");
                UploadTask uploadTask = storageReference.putFile(selectedImageUriThree);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return storageReference.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Uri firebaseUrl = task.getResult();

                                    setImageThree(firebaseUrl.toString());
                                    Toast.makeText(EditProfileActivity.this, "Photo upload successful", Toast.LENGTH_SHORT).show();

                                } else {
                                    // Handle failures
                                    // ...
                                    Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //insert into user_account_settings node
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Photo upload failed");
                        Toast.makeText(EditProfileActivity.this, "Photo upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                        if(progress - 25 > mPhotoUploadProgress){
                            Toast.makeText(EditProfileActivity.this, "Uploading " + String.format("%.0f", progress), Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;
                        }

                        Log.d(TAG, "onProgress: upload progress " + progress + "% done");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setImageThree(String url){
        Log.d(TAG, "setImageThree: setting new profile image " + url);

        myRef.child(EditProfileActivity.this.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(EditProfileActivity.this.getString(R.string.image_three))
                .setValue(url);
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions");

        ActivityCompat.requestPermissions(
                EditProfileActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    //check an array of permissions
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array");

        for(int i = 0; i < permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    //check if a single permission has been verified
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(EditProfileActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }


    private void saveProfileSettings(){
        FilePaths filePaths = new FilePaths();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //image_one
        final StorageReference storageReference_one = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_one");
        String image_one = storageReference_one.getPath();

        //image_two
        final StorageReference storageReference_two = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_two");
        String image_two = storageReference_two.getPath();

        //image_three
        final StorageReference storageReference_three = mStorageReference
                .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/image_three");
        String image_three = storageReference_three.getPath();

        final String name = mEditName.getText().toString().trim();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //if user made change to their display name
                if(!mUserSettings.getSettings().getDisplay_name().equals(name)){
                    if(!name.equals("")) {
                        mFirebaseMethods.updateDisplayName(name);
                        Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Provide your name", Toast.LENGTH_SHORT).show();
                    }
                }
                //if user made change to their first image
                if(!mUserSettings.getSettings().getImage_one().equals(image_one)){
                    uploadPhotoOne();
                }
                //if user made change to their second image
                if(!mUserSettings.getSettings().getImage_two().equals(image_two)){
                    uploadPhotoTwo();
                }
                //if user made change to their their third image
                if(!mUserSettings.getSettings().getImage_three().equals(image_three)){
                    uploadPhotoThree();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to validate the username
    public static boolean isValidUsername(String name)
    {

        // Regex to check valid username.
        String regex = "^[\\w](?!.*?\\.{2})[\\w.]{1,28}[\\w]$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the username is empty
        // return false
        if (name == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given username
        // and regular expression.
        Matcher m = p.matcher(name);

        // Return if the username
        // matched the ReGex
        return m.matches();
    }

    private void setProfileWidgets(UserSettings userSettings){
        User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        try{
            Picasso.get().load(settings.getImage_one())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoOneEditProfile);
        }catch(Exception e){}

        try{
            Picasso.get().load(settings.getImage_two())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoTwoEditProfile);
        }catch(Exception e){}

        try{
            Picasso.get().load(settings.getImage_three())
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(photoThreeEditProfile);
        }catch(Exception e){}

        mEditName.setText(settings.getDisplay_name());
        mEditYear.setText(String.valueOf(user.getYear()));
        mEditEmail.setText(user.getEmail());

        mUserSettings = userSettings;
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        userID = mAuth.getCurrentUser().getUid();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mEditProgressBar.setVisibility(View.VISIBLE);
        mCardChangePassword.setVisibility(View.INVISIBLE);
        mCardEditName.setVisibility(View.INVISIBLE);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else{
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //retrieve user information from the database

                setProfileWidgets(mFirebaseMethods.getUserSettings(snapshot));
                mEditProgressBar.setVisibility(View.INVISIBLE);
                mCardChangePassword.setVisibility(View.VISIBLE);
                mCardEditName.setVisibility(View.VISIBLE);
                //retrieve images for the user
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}