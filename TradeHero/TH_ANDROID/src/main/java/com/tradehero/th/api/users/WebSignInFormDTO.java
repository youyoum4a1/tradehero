package com.tradehero.th.api.users;


public class WebSignInFormDTO
{
    public static final String TAG = WebSignInFormDTO.class.getSimpleName();

    public String email;
    public String password;

    public WebSignInFormDTO()
    {
        super();
    }

    public boolean isValid()
    {
        return email != null && !email.isEmpty() &&
                password != null && password.length() >= 6;
    }
}
