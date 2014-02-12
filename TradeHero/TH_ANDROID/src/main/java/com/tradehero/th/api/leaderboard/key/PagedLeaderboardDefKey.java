package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractPrimitiveDTOKey;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PagedLeaderboardDefKey extends LeaderboardDefKey
{
    public final static String BUNDLE_KEY_PAGE = PagedLeaderboardDefKey.class.getName() + ".page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedLeaderboardDefKey(Integer leaderboardDefKey, int page)
    {
        super(leaderboardDefKey);
        this.page = page;
    }

    public PagedLeaderboardDefKey(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equals(AbstractPrimitiveDTOKey other)
    {
        return super.equals(other) && other instanceof PagedLeaderboardDefKey &&
                equals((PagedLeaderboardDefKey) other);
    }

    public boolean equals(PagedLeaderboardDefKey other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    public int compareTo(PagedLeaderboardDefKey other)
    {
        if (this == other)
        {
            return 0;
        }

        if (other == null)
        {
            return 1;
        }

        int parentComp = super.compareTo(other);
        if (parentComp != 0)
        {
            return parentComp;
        }

        return page.compareTo(other.page);
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (page == null)
        {
            args.remove(BUNDLE_KEY_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PAGE, page);
        }
    }

    @Override public String toString()
    {
        return String.format("[key=%d; page=%d]", key, page);
    }
}
