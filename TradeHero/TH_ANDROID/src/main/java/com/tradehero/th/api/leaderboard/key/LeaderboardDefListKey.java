package com.ayondo.academy.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.PagedDTOKey;

public class LeaderboardDefListKey implements PagedDTOKey
{
    private static final String BUNDLE_KEY_PAGE = LeaderboardDefListKey.class.getName() + ".page";

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public LeaderboardDefListKey(@Nullable Integer page)
    {
        super();
        this.page = page;
    }

    public LeaderboardDefListKey(@NonNull Bundle args)
    {
        super();
        if (args.containsKey(BUNDLE_KEY_PAGE))
        {
            this.page = args.getInt(BUNDLE_KEY_PAGE);
        }
        else
        {
            this.page = null;
        }
    }
    //</editor-fold>

    @Nullable @Override public Integer getPage()
    {
        return page;
    }

    @Override public int hashCode()
    {
        return (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equals(@Nullable Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof LeaderboardDefListKey))
        {
            return false;
        }
        return page == null
                ? ((LeaderboardDefListKey) other).page == null
                : page.equals(((LeaderboardDefListKey) other).page);
    }

    @NonNull public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    public void putParameters(@NonNull Bundle args)
    {
        if (page != null)
        {
            args.putInt(BUNDLE_KEY_PAGE, page);
        }
        else
        {
            args.remove(BUNDLE_KEY_PAGE);
        }
    }
}
