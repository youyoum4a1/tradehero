package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 9:15 PM To change this template use File | Settings | File Templates. */
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
