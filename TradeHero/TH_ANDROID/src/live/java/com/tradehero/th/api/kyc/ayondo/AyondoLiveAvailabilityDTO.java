package com.tradehero.th.api.kyc.ayondo;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.kyc.LiveAvailabilityDTO;
import com.tradehero.th.api.market.Country;

public class AyondoLiveAvailabilityDTO implements LiveAvailabilityDTO
{
    @NonNull private final String requestorIp;
    @NonNull private final Country requestorCountry;
    private final boolean isAvailable;

    public AyondoLiveAvailabilityDTO(
            @JsonProperty("requestorIp") @NonNull String requestorIp,
            @JsonProperty("requestorCoutry") @NonNull Country requestorCountry,
            @JsonProperty("isAvailable") boolean isAvailable)
    {
        this.requestorIp = requestorIp;
        this.requestorCountry = requestorCountry;
        this.isAvailable = isAvailable;
    }

    @Override @NonNull public String getRequestorIp()
    {
        return requestorIp;
    }

    @Override @NonNull public Country getRequestorCountry()
    {
        return requestorCountry;
    }

    @Override public boolean isAvailable()
    {
        return isAvailable;
    }
}
