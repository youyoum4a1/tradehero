package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;
import timber.log.Timber;

public enum LeaderboardType
{
    STOCKS(R.string.stocks, AssetClass.STOCKS),
    FX(R.string.fx, AssetClass.FX);

    @StringRes private int titleResId;
    private AssetClass assetClass;

    LeaderboardType(int titleResId, AssetClass assetClass)
    {
        this.titleResId = titleResId;
        this.assetClass = assetClass;
    }

    public int getTitleResId()
    {
        return titleResId;
    }

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

    public AssetClass getAssetClass()
    {
        return assetClass;
    }
}
