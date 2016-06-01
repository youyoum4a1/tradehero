package com.ayondo.academy.api.live;

import android.support.annotation.NonNull;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtilPublic;
import com.google.i18n.phonenumbers.Phonemetadata;
import com.ayondo.academy.api.market.Country;
import timber.log.Timber;

public class CountryUtil
{
    public static int getPhoneCodePlusLeadingDigits(@NonNull Country country)
    {
        Phonemetadata.PhoneMetadata metadata = PhoneNumberUtilPublic.getPhoneMetadataForRegion(country.name());
        if (metadata != null)
        {
            int code = metadata.getCountryCode();
            try
            {
                return Integer.valueOf(code + metadata.getLeadingDigits());
            } catch (NumberFormatException e)
            {
                Timber.v("Failed to parse %1$s for %2$s", code + metadata.getLeadingDigits(), country.name());
                return code;
            }
        }
        else
        {
            return PhoneNumberUtil.getInstance().getCountryCodeForRegion(country.name());
        }
    }
}
