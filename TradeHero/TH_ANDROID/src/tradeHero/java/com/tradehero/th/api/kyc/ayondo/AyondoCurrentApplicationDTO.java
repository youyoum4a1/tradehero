package com.androidth.general.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.common.persistence.DTO;

public class AyondoCurrentApplicationDTO implements DTO
{
    @JsonProperty("application") public AyondoAccountCreationDTO application;
    @JsonProperty("isSubmitted") public boolean isSubmitted;
}
