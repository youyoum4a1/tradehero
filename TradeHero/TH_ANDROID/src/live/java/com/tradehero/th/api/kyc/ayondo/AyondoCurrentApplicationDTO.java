package com.ayondo.academy.api.kyc.ayondo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class AyondoCurrentApplicationDTO implements DTO
{
    @JsonProperty("application") public AyondoAccountCreationDTO application;
    @JsonProperty("isSubmitted") public boolean isSubmitted;
}
