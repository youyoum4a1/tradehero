package com.ayondo.academy.api.kyc;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.market.Country;

public interface LiveAvailabilityDTO
{
    @NonNull String getRequestorIp();

    @NonNull Country getRequestorCountry();

    boolean isAvailable();
}
