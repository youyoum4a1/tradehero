package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class PagedLeaderboardKey extends LeaderboardKey
{
    public final static String BUNDLE_KEY_PAGE = PagedLeaderboardKey.class.getName() + ".page";
    public static final String STRING_SET_LEFT_PAGE = "page";
    public static final int FIRST_PAGE = 1;

    @Nullable public final Integer page;

    //<editor-fold desc="Constructors">
    public PagedLeaderboardKey(Integer leaderboardKey, Integer page)
    {
        super(leaderboardKey);
        this.page = page;
    }

    public PagedLeaderboardKey(PagedLeaderboardKey other, Integer page)
    {
        super(other.id);
        this.page = page;
    }

    public PagedLeaderboardKey(@NonNull Bundle args, @Nullable PagedLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.page = args.containsKey(BUNDLE_KEY_PAGE) ? (Integer) args.getInt(BUNDLE_KEY_PAGE) : ((defaultValues != null) ? defaultValues.page : null);
    }

    public PagedLeaderboardKey(@NonNull Set<String> catValues, @Nullable PagedLeaderboardKey defaultValues)
    {
        super(catValues, defaultValues);
        this.page = findPage(catValues, defaultValues);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (page == null ? 0 : page.hashCode());
    }

    @Override public boolean equalFields(@NonNull LeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof PagedLeaderboardKey
                && equalFields((PagedLeaderboardKey) other);
    }

    public boolean equalFields(@NonNull PagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && (page == null ? other.page == null : page.equals(other.page));
    }

    public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PagedLeaderboardKey(this, page);
    }

    @Override protected void putParameters(@NonNull Bundle args)
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

    public static Integer findPage(@NonNull Set<String> catValues, @Nullable PagedLeaderboardKey defaultValues)
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
        if (defaultValues != null)
        {
            return defaultValues.page;
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
