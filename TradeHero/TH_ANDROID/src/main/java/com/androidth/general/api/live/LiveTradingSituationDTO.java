package com.androidth.general.api.live;

import android.support.annotation.NonNull;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonProperty;

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
