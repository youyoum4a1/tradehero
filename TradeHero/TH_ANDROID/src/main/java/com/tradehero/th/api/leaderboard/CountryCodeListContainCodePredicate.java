package com.tradehero.th.api.leaderboard;

import com.android.internal.util.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CountryCodeListContainCodePredicate implements Predicate<CountryCodeList>
{
    @NotNull
    private final String lookedForCountryCode;

    public CountryCodeListContainCodePredicate(@NotNull String countryCode)
    {
        this.lookedForCountryCode = countryCode;
    }

    @Contract("null -> false")
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
