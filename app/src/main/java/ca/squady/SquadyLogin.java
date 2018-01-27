package ca.squady;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class SquadyLogin extends AppCompatActivity
{
    EditText loginEmail, loginPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_login);

        firebaseAuth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://squady-b9d45.firebaseio.com/");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);
                String firebasename = map.get("name");
                String firebaseemail = map.get("email");
                String firebasephonenumber = map.get("phonenumber");
                String firebaseusername = map.get("username");

                //i go toast am
                Toast.makeText(SquadyLogin.this, firebasename + " " + firebaseemail+ " ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        //if the objects getcurrentuser method is not null, means user is already logged in
        if(firebaseAuth.getCurrentUser() != null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(), SquadyViewProfile.class));
        }

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        progressDialog = new ProgressDialog(this);
    }

    public void userSignup(View view){
        Intent intent = new Intent(SquadyLogin.this, SquadyRegister.class);
        startActivity(intent);
    }

    //method for user login
    public void userLogin(View view)
    {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty, display a progress dialog
        progressDialog.setMessage("Logging in Please Wait...");
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
                    //start the profile activity
                    finish();
                    startActivity(new Intent(getApplicationContext(), SquadyViewProfile.class));
                }
                else
                {
                    messageAlertDialog("Wrong Credentials. Please Try Again.");
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