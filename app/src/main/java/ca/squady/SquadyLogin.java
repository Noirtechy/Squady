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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SquadyLogin extends AppCompatActivity
{
    EditText loginEmail, loginPassword;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        //if the current user is not null i.e. already logged in, redirect to view profile.
        if(UserSessionManager.getInstance(this).isLoggedIn())
        {
            finish();
            startActivity(new Intent(this, SquadyViewProfile.class));
        }

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        progressDialog = new ProgressDialog(this);
    }

    public void userSignup(View view)
    {
        Intent intent = new Intent(SquadyLogin.this, SquadyRegister.class);
        startActivity(intent);
    }

    //method for user login
    public void userLogin(View view)
    {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Required Login Details are Incomplete", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty, display a progress dialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressDialog.dismiss();
                        //if the task is successfull
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                            firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot)
                                {
                                    String username = dataSnapshot.child("username").getValue(String.class);
                                    String name = dataSnapshot.child("name").getValue(String.class);
                                    String email = dataSnapshot.child("email").getValue(String.class);
                                    String phonenumber = dataSnapshot.child("phonenumber").getValue(String.class);

                                    User loggedInUser = new User(username, name, email, phonenumber);
                                    UserSessionManager.getInstance(SquadyLogin.this).userLogin(loggedInUser);

                                    //start the profile activity
                                    progressDialog.dismiss();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), SquadyViewProfile.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {
                                    throw databaseError.toException();
                                }
                            });
                        }
                        else
                        {
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidUserException invalidEmail)
                            {
                                messageAlertDialog("Oops! Email and Password don't match. Please try something else.");
                            }
                            catch (FirebaseAuthInvalidCredentialsException wrongPassword)
                            {
                                messageAlertDialog("Oops! Email and Password don't match. Please try something else.");
                            }
                            catch (Exception e)
                            {
                                messageAlertDialog(e.getMessage());
                            }
                        }
                    }
                });
    }

    public void messageAlertDialog (String message)
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }
}