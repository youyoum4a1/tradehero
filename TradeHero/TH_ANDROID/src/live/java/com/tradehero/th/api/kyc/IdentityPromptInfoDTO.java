package com.tradehero.th.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.market.Country;

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