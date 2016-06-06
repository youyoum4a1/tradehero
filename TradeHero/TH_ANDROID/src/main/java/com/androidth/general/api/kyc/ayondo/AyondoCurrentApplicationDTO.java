package com.androidth.general.api.kyc.ayondo;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AyondoCurrentApplicationDTO implements DTO
{
    @JsonProperty("application") public AyondoAccountCreationDTO application;
    @JsonProperty("isSubmitted") public boolean isSubmitted;
}
