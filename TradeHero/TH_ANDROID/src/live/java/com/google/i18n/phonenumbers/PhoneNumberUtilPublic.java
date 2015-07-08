package com.google.i18n.phonenumbers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PhoneNumberUtilPublic
{
    @Nullable public static Phonemetadata.PhoneMetadata getPhoneMetadataForRegion(@NonNull String region)
    {
        return PhoneNumberUtil.getInstance().getMetadataForRegion(region);
    }
}
