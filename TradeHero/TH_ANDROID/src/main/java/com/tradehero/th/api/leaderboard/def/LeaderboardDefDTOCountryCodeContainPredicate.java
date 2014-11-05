package com.tradehero.th.api.leaderboard.def;

import com.android.internal.util.Predicate;
import com.tradehero.th.api.leaderboard.CountryCodeListContainCodePredicate;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LeaderboardDefDTOCountryCodeContainPredicate implements Predicate<LeaderboardDefDTO>
{
    @NonNull
    private final CountryCodeListContainCodePredicate countryCodeListContainCodePredicate;

    public LeaderboardDefDTOCountryCodeContainPredicate(@NonNull String countryCode)
    {
        super();
        this.countryCodeListContainCodePredicate = new CountryCodeListContainCodePredicate(countryCode);
    }

    @Override public boolean apply(@Nullable LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO == null)
        {
            return false;
        }
        return countryCodeListContainCodePredicate.apply(leaderboardDefDTO.countryCodes);
    }
}
