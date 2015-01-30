package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SectorLeaderboardDefListKey extends LeaderboardDefListKey
{
    static final String SECTOR = "Sector";

    //<editor-fold desc="Constructors">
    public SectorLeaderboardDefListKey(@Nullable Integer page)
    {
        super(SECTOR, page);
    }

    public SectorLeaderboardDefListKey(@NonNull Bundle args)
    {
        super(args);
        if (!key.equals(SECTOR))
        {
            throw new IllegalStateException("Key cannot be " + key);
        }
    }
    //</editor-fold>
}
