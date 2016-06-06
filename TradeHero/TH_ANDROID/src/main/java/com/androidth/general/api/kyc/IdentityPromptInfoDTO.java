package com.androidth.general.api.kyc;

import com.androidth.general.api.market.Country;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityPromptInfoDTO implements DTO
{
    @JsonProperty("country") public Country country;
    @JsonProperty("image") public String image;
    @JsonProperty("prompt") public String prompt;

    public IdentityPromptInfoDTO()
    {
        super();
    }

    public IdentityPromptInfoDTO(Country country, String image, String prompt)
    {
        this.country = country;
        this.image = image;
        this.prompt = prompt;
    }
}
