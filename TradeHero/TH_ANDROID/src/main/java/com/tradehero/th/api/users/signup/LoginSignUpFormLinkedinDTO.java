package com.tradehero.th.api.users.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormLinkedinDTO extends LoginSignUpFormDTO
{
    @JsonProperty("linkedin_access_token")
    public String accessToken;
    @JsonProperty("linkedin_access_token_secret")
    public String accessTokenSecret;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormLinkedinDTO(String deviceToken, DeviceType deviceType, String clientVersion)
    {
        super(deviceToken, deviceType, clientVersion);
    }
    //</editor-fold>
}
