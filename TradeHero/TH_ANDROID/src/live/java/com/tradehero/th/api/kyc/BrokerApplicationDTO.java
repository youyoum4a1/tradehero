package com.tradehero.th.api.kyc;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class BrokerApplicationDTO implements DTO
{
    public final int id;
    public final int brokerId;
    public final int userId;
    public final String guid;

    public BrokerApplicationDTO(
            @JsonProperty("id") int id,
            @JsonProperty("brokerId") int brokerId,
            @JsonProperty("userId") int userId,
            @JsonProperty("guid") String guid)
    {

        this.id = id;
        this.brokerId = brokerId;
        this.userId = userId;
        this.guid = guid;
    }
}
