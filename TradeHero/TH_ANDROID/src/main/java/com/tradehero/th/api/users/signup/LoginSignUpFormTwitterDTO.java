package com.tradehero.th.api.users.signup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.api.users.LoginSignUpFormDTO;

public class LoginSignUpFormTwitterDTO extends LoginSignUpFormDTO
{
    @JsonProperty("twitter_access_token")
    public String accessToken;
    @JsonProperty("twitter_access_token_secret")
    public String accessTokenSecret;

    //<editor-fold desc="Constructors">
    public LoginSignUpFormTwitterDTO(String deviceToken, DeviceType deviceType, String clientVersion, String device_access_token)
    {
        super(deviceToken, deviceType, clientVersion, device_access_token);
    }
    //</editor-fold>
}
