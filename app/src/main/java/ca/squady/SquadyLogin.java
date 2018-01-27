package ca.squady;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;

public class SquadyLogin extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_login);
    }

    public void onClick(View view)
    {
        Intent intent = new Intent(this, SquadyRegister.class);
        startActivity(intent);
    }

    public void onClickLogin(View view)
    {
        Intent intent = new Intent(this, SquadyViewProfile.class);
        startActivity(intent);
    }
}