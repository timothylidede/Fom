package utils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.savala.fom.R;

import models.User;
import models.UserAccountSettings;
import models.UserSettings;

import static android.content.ContentValues.TAG;

public class FirebaseMethods {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userID;
    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void updateDisplayName(String name){
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_name))
                .setValue(name);
    }

    public void updateDescription(String description){
        Log.d(TAG, "updateUserAccountSettings: updating user account settings");
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_description))
                .setValue(description);
    }

    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username to " + username);

        myRef.child(mContext.getString(R.string.dbname_user))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }



    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSetting: retrieving user account settings from the database");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))){
                try{
                    settings.setDisplay_name(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDisplay_name()
                    );
                    settings.setUser_id(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUser_id()
                    );
                    settings.setConnections_count(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getConnections_count()
                    );
                    settings.setPacks_following_count(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPacks_following_count()
                    );
                    settings.setPacks_count(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPacks_count()
                    );
                    settings.setPotentials_count(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPotentials_count()
                    );
                    settings.setImage_one(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getImage_one()
                    );
                    settings.setImage_two(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getImage_two()
                    );
                    settings.setImage_three(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getImage_three()
                    );
                }catch(NullPointerException e){
                    Log.d(TAG, "getUserAccountSetting: NullPointerException " + e.getMessage());
                }
            }
            if(ds.getKey().equals(mContext.getString(R.string.dbname_user))){
                try{
                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );
                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id()
                    );
                    user.setYear(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getYear()
                    );

                }catch(NullPointerException e){
                    Log.d(TAG, "getUserAccountSetting: NullPointerException " + e.getMessage());
                }
            }
        }
        return new UserSettings(user, settings);
    }
}
