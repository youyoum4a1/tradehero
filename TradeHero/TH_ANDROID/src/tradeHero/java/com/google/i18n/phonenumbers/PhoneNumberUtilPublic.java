package com.google.i18n.phonenumbers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.i18n.phonenumbers.nano.Phonemetadata;

public class PhoneNumberUtilPublic
{
    @Nullable public static Phonemetadata.PhoneMetadata getPhoneMetadataForRegion(@NonNull String region)
    {
        return PhoneNumberUtil.getInstance().getMetadataForRegion(region);
    }
}
