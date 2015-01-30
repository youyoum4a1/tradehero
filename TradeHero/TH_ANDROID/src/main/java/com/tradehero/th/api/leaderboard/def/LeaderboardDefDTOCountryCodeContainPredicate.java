package com.tradehero.th.api.leaderboard.def;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import com.tradehero.th.api.leaderboard.CountryCodeListContainCodePredicate;

public class LeaderboardDefDTOCountryCodeContainPredicate implements Predicate<LeaderboardDefDTO>
{
    @NonNull
    private final CountryCodeListContainCodePredicate countryCodeListContainCodePredicate;

    //<editor-fold desc="Constructors">
    public LeaderboardDefDTOCountryCodeContainPredicate(@NonNull String countryCode)
    {
        super();
        this.countryCodeListContainCodePredicate = new CountryCodeListContainCodePredicate(countryCode);
    }
    //</editor-fold>

    @Override public boolean apply(@Nullable LeaderboardDefDTO leaderboardDefDTO)
    {
        return leaderboardDefDTO != null
                && countryCodeListContainCodePredicate.apply(leaderboardDefDTO.countryCodes);
    }
}
