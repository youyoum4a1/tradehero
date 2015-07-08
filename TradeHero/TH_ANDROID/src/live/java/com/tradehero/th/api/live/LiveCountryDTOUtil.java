package com.tradehero.th.api.live;

import android.support.annotation.NonNull;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtilPublic;
import com.google.i18n.phonenumbers.Phonemetadata;
import com.tradehero.th.api.market.Country;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class LiveCountryDTOUtil
{
    @NonNull public static List<LiveCountryDTO> getFilterByPhoneCountryCode(
            @NonNull List<? extends LiveCountryDTO> liveCountryDTOs,
            int phoneCountryCode)
    {
        List<String> regions = PhoneNumberUtil.getInstance().getRegionCodesForCountryCode(phoneCountryCode);
        List<Country> countries = new ArrayList<>();
        for (String region : regions)
        {
            try
            {
                countries.add(Enum.valueOf(Country.class, region));
            } catch (Exception e)
            {
                Timber.e(e, "Failed to find country for region %s", region);
            }
        }
        return getFilterByCountry(liveCountryDTOs, countries);
    }

    @NonNull public static List<LiveCountryDTO> getFilterByCountry(
            @NonNull List<? extends LiveCountryDTO> liveCountryDTOs,
            @NonNull List<Country> countries)
    {
        List<LiveCountryDTO> filtered = new ArrayList<>();

        for (LiveCountryDTO candidate : liveCountryDTOs)
        {
            if (countries.contains(candidate.country))
            {
                filtered.add(candidate);
            }
        }

        return filtered;
    }

    public static int getPhoneCodePlusLeadingDigits(@NonNull LiveCountryDTO liveCountryDTO)
    {
        Phonemetadata.PhoneMetadata metadata = PhoneNumberUtilPublic.getPhoneMetadataForRegion(liveCountryDTO.country.name());
        if (metadata != null)
        {
            int code = metadata.getCountryCode();
            try
            {
                return Integer.valueOf(code + metadata.getLeadingDigits());
            } catch (NumberFormatException e)
            {
                Timber.v("Failed to parse %1$s for %2$s", code + metadata.getLeadingDigits(), liveCountryDTO.country.name());
                return code;
            }
        }
        else
        {
            return PhoneNumberUtil.getInstance().getCountryCodeForRegion(liveCountryDTO.country.name());
        }
    }
}
