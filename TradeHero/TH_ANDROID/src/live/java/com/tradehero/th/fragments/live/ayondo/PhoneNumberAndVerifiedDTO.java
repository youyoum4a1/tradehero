package com.ayondo.academy.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.market.Country;

class PhoneNumberAndVerifiedDTO extends PhoneNumberDTO
{
    public final boolean verified;

    public PhoneNumberAndVerifiedDTO(
            @NonNull Country dialingCountry,
            int dialingPrefix,
            @NonNull String typedNumber,
            boolean verified)
    {
        super(dialingCountry, dialingPrefix, typedNumber);
        this.verified = verified;
    }
}
