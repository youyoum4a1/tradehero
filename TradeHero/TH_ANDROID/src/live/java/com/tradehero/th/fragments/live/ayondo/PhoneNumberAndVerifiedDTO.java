package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;

class PhoneNumberAndVerifiedDTO
{
    public final int dialingPrefix;
    @NonNull public final String typedNumber;
    public final boolean verified;

    public PhoneNumberAndVerifiedDTO(int dialingPrefix,
            @NonNull String typedNumber,
            boolean verified)
    {
        this.dialingPrefix = dialingPrefix;
        this.typedNumber = typedNumber;
        this.verified = verified;
    }
}
