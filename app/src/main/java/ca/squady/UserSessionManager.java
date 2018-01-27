package ca.squady;

/**
 * Created by Chigozie on 1/27/2018.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class UserSessionManager
{
    private static final String SHARED_PREF_NAME = "squady";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_NAME = "keyname";
    private static final String KEY_EMAIL = "keyemail";
    private static final String KEY_PHONENUMBER = "keyphonenumber";

    private static UserSessionManager mInstance;
    private static Context mCtx;

    private UserSessionManager(Context context) {
        mCtx = context;
    }

    public static synchronized UserSessionManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserSessionManager(context);
        }
        return mInstance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(UserInformation user)
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_NAME, user.getName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONENUMBER, user.getPhonenumber());
        editor.apply();
    }

    //this method checks whether user is already logged in or not
    public boolean isLoggedIn()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    //this method gives the logged in user
    public UserInformation getUser()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserInformation
        (
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_NAME, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_PHONENUMBER, null)
        );
    }

    //this method will logout the user
    public void logout()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, SquadyLogin.class));
    }
}
