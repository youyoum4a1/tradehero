package com.tradehero.th.api.form;

import com.tradehero.common.utils.THLog;
import org.json.JSONException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:04 PM Copyright (c) TradeHero */
public class UserFormDTO
{
    private static final String TAG = UserFormDTO.class.getName();
    // Signup with email
    public String email;
    public String username;
    public String password;
    public String passwordConfirmation;
    public String firstName;
    public String lastName;
    public String displayName;

    //notifications settings
    public Boolean pushNotificationsEnabled;
    public Boolean emailNotificationsEnabled;

    // facebook
    public String facebook_access_token;

    // linkedin
    public String linkedin_access_token;
    public String linkedin_access_token_secret;

    // twitter
    public String twitter_access_token;
    public String twitter_access_token_secret;

    // optional
    public String biography;
    public String location;
    public String website;
    public String deviceToken;

    public UserFormDTO(JSONObject json)
    {
        try
        {
            if (json.has("type"))
            {
                String type = json.getString("type");
                if ("facebook".equals(type))
                {
                    facebook_access_token = json.getString("access_token");
                }
                else if ("twitter".equals(type))
                {
                    twitter_access_token = json.getString("auth_token");
                    twitter_access_token_secret = json.getString("auth_token_secret");
                    email = json.getString("email");
                }
                else if ("linkedin".equals(type))
                {
                    linkedin_access_token = json.getString("auth_token");
                    linkedin_access_token_secret = json.getString("auth_token_secret");
                }
            }
        }
        catch (JSONException e)
        {
            THLog.e(TAG, "Parsing error", e);
        }
    }

    public UserFormDTO()
    {
    }
}
