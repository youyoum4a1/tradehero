package com.ayondo.academy.fragments.leaderboard;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.ayondo.academy.R;
import com.ayondo.academy.api.portfolio.AssetClass;
import timber.log.Timber;

public enum LeaderboardType
{
    STOCKS(R.string.stocks, AssetClass.STOCKS),
    FX(R.string.fx, AssetClass.FX);

    @StringRes public final int titleResId;
    @NonNull public final AssetClass assetClass;

    //<editor-fold desc="Constructors">
    LeaderboardType(
            @StringRes int titleResId,
            @NonNull AssetClass assetClass)
    {
        this.titleResId = titleResId;
        this.assetClass = assetClass;
    }
    //</editor-fold>

    public int getLeaderboardTypeId()
    {
        return assetClass.getValue();
    }

    public static LeaderboardType from(int value)
    {
        for (LeaderboardType leaderboardType : values())
        {
            if (leaderboardType.assetClass.getValue() == value)
            {
                return leaderboardType;
            }
        }
        Timber.e("Unknown id for LeaderboardType %d", value);
        return null;
    }
}
