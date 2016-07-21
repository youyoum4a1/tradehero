package com.androidth.general.api.live;

import android.support.annotation.NonNull;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtilPublic;
import com.androidth.general.api.market.Country;
import com.google.i18n.phonenumbers.nano.Phonemetadata;

import timber.log.Timber;

public class CountryUtil
{
    public static int getPhoneCodePlusLeadingDigits(@NonNull Country country)
    {
        Phonemetadata.PhoneMetadata metadata = PhoneNumberUtilPublic.getPhoneMetadataForRegion(country.name());
        if (metadata != null)
        {
            int code = metadata.countryCode;
            try
            {
                return Integer.valueOf(code + Integer.valueOf(metadata.leadingDigits));
            } catch (NumberFormatException e)
            {
                Timber.v("Failed to parse %1$s for %2$s", code + Integer.valueOf(metadata.leadingDigits), country.name());
                return code;
            }
        }
        else
        {
            return PhoneNumberUtil.getInstance().getCountryCodeForRegion(country.name());
        }
    }
}
