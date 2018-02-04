package ca.squady;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class SquadyViewProfile extends AppCompatActivity
{
    private DrawerLayout drawer;
    Toolbar toolbar_top;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.squady_viewprofile);

        navdrawer();
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
        final TextView profileusername = (TextView) findViewById(R.id.usernameHeader);
        final TextView profilename = (TextView) findViewById(R.id.profilename);
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
        Intent login = new Intent(SquadyViewProfile.this, SquadyEditProfile.class);
        startActivity(login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}