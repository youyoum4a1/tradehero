package com.tradehero.th.api.live;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;

public class TradingAvailableDTO implements DTO
{
    public final boolean isLiveTradingAvailable;
    @Nullable public final LiveBrokerDTO broker;

    public TradingAvailableDTO(
            @JsonProperty("isLiveTradingAvailable") boolean isLiveTradingAvailable,
            @JsonProperty("broker") @Nullable LiveBrokerDTO broker)
    {
        this.isLiveTradingAvailable = isLiveTradingAvailable;
        this.broker = broker;
    }
}
