package com.tradehero.th.api.leaderboard;

import com.android.internal.util.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LeaderboardDefDTOCountryCodeContainPredicate implements Predicate<LeaderboardDefDTO>
{
    @NotNull
    private final CountryCodeListContainCodePredicate countryCodeListContainCodePredicate;

    public LeaderboardDefDTOCountryCodeContainPredicate(@NotNull String countryCode)
    {
        super();
        this.countryCodeListContainCodePredicate = new CountryCodeListContainCodePredicate(countryCode);
    }

    @Contract("null -> false")
    @Override public boolean apply(@Nullable LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardDefDTO == null)
        {
            return false;
        }
        return countryCodeListContainCodePredicate.apply(leaderboardDefDTO.countryCodes);
    }
}
