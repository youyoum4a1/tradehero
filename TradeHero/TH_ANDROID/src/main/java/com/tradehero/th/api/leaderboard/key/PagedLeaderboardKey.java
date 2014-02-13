package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractPrimitiveDTOKey;
import java.util.Iterator;
import java.util.Set;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 3:30 PM To change this template use File | Settings | File Templates. */
public class PagedLeaderboardKey extends LeaderboardKey
{
    public final static String BUNDLE_KEY_PAGE = PagedLeaderboardKey.class.getName() + ".page";
    public static final String STRING_SET_LEFT_PAGE = "page";

    public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedLeaderboardKey(Integer leaderboardKey, Integer page)
    {
        super(leaderboardKey);
        this.page = page;
    }

    public PagedLeaderboardKey(PagedLeaderboardKey other, Integer page)
    {
        super(other.key);
        this.page = page;
    }

    public PagedLeaderboardKey(Bundle args)
    {
        super(args);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? args.getInt(BUNDLE_KEY_PAGE) : null;
    }

    public PagedLeaderboardKey(Set<String> catValues)
    {
        super(catValues);
        this.page = findPage(catValues);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equals(AbstractPrimitiveDTOKey other)
    {
        return super.equals(other) && other instanceof PagedLeaderboardKey &&
                equals((PagedLeaderboardKey) other);
    }

    public boolean equals(PagedLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (page == null ? other.page == null : page.equals(other.page));
    }

    public int compareTo(PagedLeaderboardKey other)
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

    public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PagedLeaderboardKey(this, page);
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

    public static Integer findPage(Set<String> catValues)
    {
        Iterator<String> iterator = catValues.iterator();
        String catValue;
        String[] split;
        while (iterator.hasNext())
        {
            catValue = iterator.next();
            split = catValue.split(STRING_SET_VALUE_SEPARATOR);
            if (split[0].equals(STRING_SET_LEFT_PAGE))
            {
                return Integer.valueOf(split[1]);
            }
        }
        return null;
    }

    @Override public void putParameters(Set<String> catValues)
    {
        super.putParameters(catValues);
        putPage(catValues, this.page);
    }

    public static void putPage(Set<String> catValues, Integer page)
    {
        if (page != null)
        {
            catValues.add(STRING_SET_LEFT_PAGE + STRING_SET_VALUE_SEPARATOR + page);
        }
    }
}
