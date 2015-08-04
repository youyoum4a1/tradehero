package com.tradehero.th.api.kyc;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.Country;

public interface LiveAvailabilityDTO
{
    @NonNull String getRequestorIp();

    @NonNull Country getRequestorCountry();

    boolean isAvailable();
}
