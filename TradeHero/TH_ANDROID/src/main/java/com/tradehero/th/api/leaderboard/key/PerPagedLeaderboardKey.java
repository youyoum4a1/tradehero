package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import java.util.Iterator;
import java.util.Set;


public class PerPagedLeaderboardKey extends PagedLeaderboardKey
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedLeaderboardKey.class.getName() + ".perPage";
    public static final String STRING_SET_LEFT_PER_PAGE = "perPage";

    public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedLeaderboardKey(Integer leaderboardKey, Integer page, Integer perPage)
    {
        super(leaderboardKey, page);
        this.perPage = perPage;
    }

    public PerPagedLeaderboardKey(PerPagedLeaderboardKey other, Integer overrideKey, Integer page)
    {
        super(overrideKey, page);
        this.perPage = other.perPage;
    }

    public PerPagedLeaderboardKey(Bundle args)
    {
        super(args);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? args.getInt(BUNDLE_KEY_PER_PAGE) : null;
    }

    public PerPagedLeaderboardKey(Set<String> catValues)
    {
        super(catValues);
        this.perPage = findPerPage(catValues);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equals(PagedLeaderboardKey other)
    {
        return super.equals(other) && other instanceof PerPagedLeaderboardKey &&
                equals((PerPagedLeaderboardKey) other);
    }

    public boolean equals(PerPagedLeaderboardKey other)
    {
        return other != null &&
                super.equals(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    public int compareTo(PerPagedLeaderboardKey other)
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

        return perPage.compareTo(other.perPage);
    }

    @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PerPagedLeaderboardKey(this, key, page);
    }

    @Override public void putParameters(Bundle args)
    {
        super.putParameters(args);
        if (perPage == null)
        {
            args.remove(BUNDLE_KEY_PER_PAGE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
        }
    }

    public static Integer findPerPage(Set<String> catValues)
    {
        Iterator<String> iterator = catValues.iterator();
        String catValue;
        String[] split;
        while (iterator.hasNext())
        {
            catValue = iterator.next();
            split = catValue.split(STRING_SET_VALUE_SEPARATOR);
            if (split[0].equals(STRING_SET_LEFT_PER_PAGE))
            {
                return Integer.valueOf(split[1]);
            }
        }
        return null;
    }

    @Override public void putParameters(Set<String> catValues)
    {
        super.putParameters(catValues);
        putPerPage(catValues, this.perPage);
    }

    public static void putPerPage(Set<String> catValues, Integer perPage)
    {
        if (perPage != null)
        {
            catValues.add(STRING_SET_LEFT_PER_PAGE + STRING_SET_VALUE_SEPARATOR + perPage);
        }
    }
}
