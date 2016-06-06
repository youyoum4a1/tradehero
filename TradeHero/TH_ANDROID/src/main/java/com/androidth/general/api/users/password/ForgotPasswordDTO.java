package com.androidth.general.api.users.password;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForgotPasswordDTO
{
    @JsonProperty("FoundUserEmail")
    public boolean foundUserEmail;
}
