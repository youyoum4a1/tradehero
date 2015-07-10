package com.tradehero.th.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class IdentityPromptInfoDTO implements DTO
{
    public final String image;
    public final String prompt;

    public IdentityPromptInfoDTO(
            @JsonProperty("image") String image,
            @JsonProperty("prompt") String prompt)
    {
        this.image = image;
        this.prompt = prompt;
    }
}
