package com.ayondo.academy.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class BrokerApplicationDTO implements DTO
{
    @JsonProperty("id") public int id;
    @JsonProperty("brokerId") public int brokerId;
    @JsonProperty("userId") public int userId;
    @JsonProperty("guid") public String guid;

    public BrokerApplicationDTO()
    {
        super();
    }
}
