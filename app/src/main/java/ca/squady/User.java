package ca.squady;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String,Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("name", name);
        result.put("email", email);
        result.put("phonenumber", phonenumber);
        return result;
    }
}
