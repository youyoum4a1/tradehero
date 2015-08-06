package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.tradehero.th.api.market.Country;

class PhoneNumberDTO
{
    @NonNull public final Country dialingCountry;
    public final int dialingPrefix;
    @NonNull public final String typedNumber;

    PhoneNumberDTO(
            @NonNull Country dialingCountry,
            int dialingPrefix, @NonNull String typedNumber)
    {
        this.dialingCountry = dialingCountry;
        this.typedNumber = typedNumber;
        this.dialingPrefix = dialingPrefix;
    }
}
