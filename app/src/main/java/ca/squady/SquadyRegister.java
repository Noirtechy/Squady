package ca.squady;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SquadyRegister extends AppCompatActivity
{
    //Define firebaseauth object
    EditText registerUsername, registerName, registerEmail, registerPassword, confirmPassword, registerPhoneNumber;
    String username, name, email, password, confirmpassword, phonenumber;
    Button buttonSignup;
    ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference();

        //initializing views
        registerUsername = (EditText) findViewById(R.id.registerUsername);
        registerName = (EditText) findViewById(R.id.registerName);
        registerEmail = (EditText) findViewById(R.id.registerEmail);
        registerPassword = (EditText) findViewById(R.id.registerPassword);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        registerPhoneNumber = (EditText) findViewById(R.id.phoneNumber);

        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        progressDialog = new ProgressDialog(this);
    }

    public void registerUser(View view)
    {
        //getting email and password from edit texts
        username = registerUsername.getText().toString().trim();
        name  = registerName.getText().toString().trim();
        email = registerEmail.getText().toString().trim();
        password  = registerPassword.getText().toString().trim();
        confirmpassword = confirmPassword.getText().toString().trim();
        phonenumber = registerPhoneNumber.getText().toString().trim();

        //checking if email and passwords are empty
        if(TextUtils.isEmpty(username))
        {
            messageAlertDialog("Username is required");
            return;
        }

        if(TextUtils.isEmpty(name))
        {
            messageAlertDialog("Name is required");
            return;
        }

        if(TextUtils.isEmpty(email))
        {
            messageAlertDialog("Email is required");
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            messageAlertDialog("Password is required");
            return;
        }

        if(TextUtils.isEmpty(confirmpassword))
        {
            messageAlertDialog("Please confirm your password");
            return;
        }

        //if the email and password are not empty, display a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                //checking if success
                if(task.isSuccessful())
                {
                    User userObject = new User(username, name, email, phonenumber);
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    firebaseDatabase.child(user.getUid()).setValue(userObject);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(SquadyRegister.this);
                    builder.setMessage("Successfully Registered!");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                }
                else
                {
                    try
                    {
                        throw task.getException();
                    }
                    catch (FirebaseAuthWeakPasswordException weakPassword)
                    {
                       messageAlertDialog("Try entering a stronger password");
                    }
                    catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                    {
                        messageAlertDialog("Your email address seems invalid. Please try something else.");
                    }
                    catch (FirebaseAuthUserCollisionException existEmail)
                    {
                        messageAlertDialog("This email address is already in use. Please try something else.");
                    }
                    catch (Exception e)
                    {
                        messageAlertDialog(e.getMessage());
                    }
                }
                progressDialog.dismiss();
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
