package com.tradehero.th.api.leaderboard;

import com.android.internal.util.Predicate;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CountryCodeListContainCodePredicate implements Predicate<CountryCodeList>
{
    @NonNull
    private final String lookedForCountryCode;

    public CountryCodeListContainCodePredicate(@NonNull String countryCode)
    {
        this.lookedForCountryCode = countryCode;
    }

    @Override public boolean apply(@Nullable CountryCodeList countryCodeList)
    {
        if (countryCodeList == null)
        {
            return false;
        }
        for (String countryCode : countryCodeList)
        {
            if (lookedForCountryCode.equals(countryCode))
            {
                return true;
            }
        }
        return false;
    }
}
