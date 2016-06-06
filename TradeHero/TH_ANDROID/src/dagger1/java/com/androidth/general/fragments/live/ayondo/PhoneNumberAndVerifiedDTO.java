package com.androidth.general.fragments.live.ayondo;

import android.support.annotation.NonNull;

import com.androidth.general.api.market.Country;

class PhoneNumberAndVerifiedDTO extends com.ayondo.academyapp.fragments.live.ayondo.PhoneNumberDTO
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
