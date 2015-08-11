package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import java.util.List;

public class LiveTradingSituationDTO implements DTO
{
    @NonNull public final List<LiveBrokerSituationDTO> brokerSituations;

    public LiveTradingSituationDTO(
            @JsonProperty("brokerSituations") @NonNull List<LiveBrokerSituationDTO> brokerSituations)
    {
        this.brokerSituations = brokerSituations;
    }

    @Override public String toString()
    {
        return "LiveTradingSituationDTO{" +
                "brokerSituations=" + brokerSituations +
                '}';
    }
}
