package com.androidth.general.api.kyc;

import android.support.annotation.NonNull;

public class PhoneNumberVerifiedStatusDTO
{
    @NonNull public final String phoneNumber;
    @NonNull public final Boolean verified;

    public PhoneNumberVerifiedStatusDTO(@NonNull String phoneNumber, boolean verified)
    {
        this.phoneNumber = phoneNumber;
        this.verified = verified;
    }

    @Override public int hashCode()
    {
        return phoneNumber.hashCode() ^ verified.hashCode();
    }

    @Override public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        return o instanceof PhoneNumberVerifiedStatusDTO
                && ((PhoneNumberVerifiedStatusDTO) o).phoneNumber.equals(phoneNumber)
                && ((PhoneNumberVerifiedStatusDTO) o).verified.equals(verified);
    }
}
