package ca.squady;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SquadyEditProfile extends AppCompatActivity
{
    ProgressDialog progressDialog;
    private DatabaseReference firebaseDatabase;
    String username, name, email, phonenumber;
    EditText profileusername, profilename, profileemail, profilephonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_editprofile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
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
        //Logout functionality temporarily in edit profile button for easy testing
        finishAffinity();
        UserSessionManager.getInstance(getApplicationContext()).logout();

        Intent login = new Intent(SquadyEditProfile.this, SquadyLogin.class);
        startActivity(login);
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