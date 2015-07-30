package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsernameValidationResultDTO
{
    @Nullable public final String username;
    public final boolean isValid;
    public final boolean isAvailable;

    public UsernameValidationResultDTO(
            @JsonProperty("username") String username,
            @JsonProperty("isValid") boolean isValid,
            @JsonProperty("isAvailable") boolean isAvailable)
    {
        this.username = username;
        this.isValid = isValid;
        this.isAvailable = isAvailable;
    }
}