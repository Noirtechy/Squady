package ca.squady;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import com.theartofdev.edmodo.cropper.CropImageView;

public class SquadyEditProfile extends AppCompatActivity
{
    ProgressDialog progressDialog;
    private DatabaseReference firebaseDatabase;
    String username, name, email, phonenumber;
    EditText profileusername, profilename, profileemail, profilephonenumber;
    private DrawerLayout drawer;
    Toolbar toolbar_top;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private static final int GALLERY_PICK = 1;
    private FirebaseUser mCurrentuser;
    private StorageReference mImageStorage;
    private ImageView profilephoto;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_editprofile);
        profilephoto = (ImageView)findViewById(R.id.profilephoto);
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentuser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentuser.getUid());
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String image = dataSnapshot.child("image").getValue().toString();

                Picasso.with(SquadyEditProfile.this).load(image).into(profilephoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navdrawer();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    public void navdrawer(){
        toolbar_top = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar_top);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar_top, R.string.open, R.string.close);
        drawer.setDrawerListener(actionBarDrawerToggle);
    }

    public void onResume()
    {
        super.onResume();
        displayUserData();
    }

    public void displayUserData ()
    {
        profileusername = (EditText) findViewById(R.id.updateUsername);
        profilename = (EditText) findViewById(R.id.updateName);
        profileemail = (EditText) findViewById(R.id.updateEmailAddress);
        profilephonenumber = (EditText) findViewById(R.id.updatePhoneNumber);

        User user = UserSessionManager.getInstance(this).getUser();

        profileusername.setText(user.getUsername());
        profilename.setText(user.getName());
        profileemail.setText(user.getEmail());
        profilephonenumber.setText(user.getPhonenumber());
    }

    public void uploadProfilePhoto (View view)
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

        /*CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SquadyEditProfile.this);
                mProgressDialog.setTitle("Upload in Progress");
                mProgressDialog.setMessage("Please wait while your image is uploading");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                String current_user_id = mCurrentuser.getUid();
                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            String download_url = task.getResult().getDownloadUrl().toString();
                            firebaseDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SquadyEditProfile.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(SquadyEditProfile.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void saveProfileUpdate (View view)
    {
        //getting email and password from edit texts
        username = profileusername.getText().toString().trim();
        name  = profilename.getText().toString().trim();
        email = profileemail.getText().toString().trim();
        phonenumber = profilephonenumber.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phonenumber))
        {
            Toast.makeText(this, "Unable to update with empty fields. Please Check Again.", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty, display a progress dialog
        progressDialog.setMessage("Updating your profile. Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final User userObject = new User(username, name, email, phonenumber);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        firebaseDatabase.setValue(userObject).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    progressDialog.dismiss();
                    UserSessionManager.getInstance(SquadyEditProfile.this).userLogin(userObject);
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SquadyEditProfile.this);
                    builder.setMessage("Successfully Registered!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            displayUserData();
                            dialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), SquadyViewProfile.class));
                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }
}