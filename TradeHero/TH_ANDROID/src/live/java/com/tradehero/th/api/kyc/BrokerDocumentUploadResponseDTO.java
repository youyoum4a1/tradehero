package com.tradehero.th.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class BrokerDocumentUploadResponseDTO implements DTO
{
    @JsonProperty("guid") public String url;

    public BrokerDocumentUploadResponseDTO()
    {
        super();
    }
}
