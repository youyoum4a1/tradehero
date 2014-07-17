package com.tradehero.th.api.users;

/**
 * Created by tradehero on 14-7-16.
 */
public class LoginSignUpFormDTO extends LoginFormDTO
{
    public String weibo_access_token;
    public String qq_access_token;
    public String qq_openid;
    public String facebook_access_token;
    public String linkedin_access_token;
    public String linkedin_access_token_secret;
    public String twitter_access_token;
    public String twitter_access_token_secret;
    public boolean isEmailLogin = false;

    public LoginSignUpFormDTO(LoginFormDTO loginFormDTO)
    {
        super(loginFormDTO.deviceToken, loginFormDTO.clientType, loginFormDTO.clientVersion);
    }
}
