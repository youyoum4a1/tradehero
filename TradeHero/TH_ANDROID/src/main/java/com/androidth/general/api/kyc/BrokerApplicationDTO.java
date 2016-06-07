package com.androidth.general.api.kyc;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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