package com.androidth.general.api.kyc.ayondo;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsernameValidationResultDTO
{
    @Nullable public final String username;
    public final boolean isValid;
    public final boolean isAvailable;

    public UsernameValidationResultDTO(
            @JsonProperty("username") String username,
            @JsonProperty("IsValid") boolean isValid,
            @JsonProperty("IsAvailable") boolean isAvailable)
    {
        this.username = username;
        this.isValid = isValid;
        this.isAvailable = isAvailable;
    }
}