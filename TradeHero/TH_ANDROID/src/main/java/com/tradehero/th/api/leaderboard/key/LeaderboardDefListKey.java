package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.AbstractStringDTOKey;

public class LeaderboardDefListKey extends AbstractStringDTOKey
        implements PagedDTOKey
{
    static final String BUNDLE_KEY_KEY = LeaderboardDefKey.class.getName() + ".key";
    private static final String BUNDLE_KEY_PAGE = LeaderboardDefListKey.class.getName() + ".page";

    private static final String ALL = "all";

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public LeaderboardDefListKey(@NonNull String key)
    {
        super(key);
        this.page = null;
    }

    public LeaderboardDefListKey(@Nullable Integer page)
    {
        super(ALL);
        this.page = page;
    }

    public LeaderboardDefListKey(
            @NonNull String key,
            @Nullable Integer page)
    {
        super(key);
        this.page = page;
    }

    public LeaderboardDefListKey(@NonNull Bundle args)
    {
        super(args);
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

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    @Override public Integer getPage()
    {
        return page;
    }

    @Override public int hashCode()
    {
        return super.hashCode()
                ^ (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return super.equals(other)
                && (other instanceof LeaderboardDefListKey)
                && (page == null ?
                ((LeaderboardDefListKey) other).page == null :
                page.equals(((LeaderboardDefListKey) other).page));
    }

    @Override public void putParameters(@NonNull Bundle args)
    {
        super.putParameters(args);
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
