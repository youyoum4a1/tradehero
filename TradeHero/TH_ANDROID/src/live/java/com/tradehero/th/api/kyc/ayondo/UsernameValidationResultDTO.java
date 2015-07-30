package com.tradehero.th.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UsernameValidationResultDTO
{
    public final boolean isValid;
    public final boolean isAvailable;

    public UsernameValidationResultDTO(
            @JsonProperty("isValid") boolean isValid,
            @JsonProperty("isAvailable") boolean isAvailable)
    {
        this.isValid = isValid;
        this.isAvailable = isAvailable;
    }
}