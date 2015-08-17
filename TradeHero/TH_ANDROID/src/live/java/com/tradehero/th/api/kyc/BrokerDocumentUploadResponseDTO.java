package com.tradehero.th.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class BrokerDocumentUploadResponseDTO implements DTO
{
    public final String url;

    public BrokerDocumentUploadResponseDTO(
            @JsonProperty("guid") String url)
    {
        this.url = url;
    }
}
