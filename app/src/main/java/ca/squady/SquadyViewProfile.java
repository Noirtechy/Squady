package ca.squady;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SquadyViewProfile extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_viewprofile);

        final TextView profileusername = (TextView) findViewById(R.id.usernameHeader);
        final TextView profilename = (TextView) findViewById(R.id.profilename);
        final TextView profilelocation = (TextView) findViewById(R.id.profilelocation);
        final TextView profileemail = (TextView) findViewById(R.id.profileemail);
        final TextView profilephonenumber = (TextView) findViewById(R.id.profilephonenumber);

        User user = UserSessionManager.getInstance(this).getUser();

        profileusername.setText(user.getUsername());
        profilename.setText(user.getName());
        profileemail.setText(user.getEmail());
        profilephonenumber.setText(user.getPhonenumber());
    }

    public void editProfile (View view)
    {
        //Logout functionality temporarily in edit profile button for easy testing
        finishAffinity();
        UserSessionManager.getInstance(getApplicationContext()).logout();
        Intent login = new Intent(SquadyViewProfile.this, SquadyLogin.class);
        startActivity(login);
    }
}
