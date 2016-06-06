package com.ayondo.academyapp.api.kyc;

import android.support.annotation.NonNull;
import com.androidth.general.api.market.Country;

public interface LiveAvailabilityDTO
{
    @NonNull String getRequestorIp();

    @NonNull Country getRequestorCountry();

    boolean isAvailable();
}
