package ca.squady;

/**
 * Created by Ifeoluwa David on 2018-01-27.
 */

public class User
{
    private String username, name, email, phonenumber;
    private int userID;

    public User(String username, String name, String email, String phonenumber)
    {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
    }

    public String getUsername()
    {
        return username;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }
}
