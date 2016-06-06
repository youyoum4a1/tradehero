package com.androidth.general.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;

public class PerPagedLeaderboardKey extends PagedLeaderboardKey
{
    public final static String BUNDLE_KEY_PER_PAGE = PerPagedLeaderboardKey.class.getName() + ".perPage";
    public static final String STRING_SET_LEFT_PER_PAGE = "perPage";

    @Nullable public final Integer perPage;

    //<editor-fold desc="Constructors">
    public PerPagedLeaderboardKey(
            @NonNull Integer leaderboardKey,
            @Nullable Integer page,
            @Nullable Integer perPage)
    {
        super(leaderboardKey, page);
        this.perPage = perPage;
    }

    public PerPagedLeaderboardKey(
            @NonNull PerPagedLeaderboardKey other,
            @NonNull Integer overrideKey,
            @Nullable Integer page)
    {
        super(overrideKey, page);
        this.perPage = other.perPage;
    }

    public PerPagedLeaderboardKey(
            @NonNull Bundle args,
            @Nullable PerPagedLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.perPage = args.containsKey(BUNDLE_KEY_PER_PAGE) ? (Integer) args.getInt(BUNDLE_KEY_PER_PAGE) : ((defaultValues != null) ? defaultValues.perPage : null);
    }

    public PerPagedLeaderboardKey(
            @NonNull Set<String> catValues,
            @Nullable PerPagedLeaderboardKey defaultValues)
    {
        super(catValues, defaultValues);
        this.perPage = findPerPage(catValues, defaultValues);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (perPage == null ? 0 : perPage.hashCode());
    }

    @Override public boolean equalFields(@NonNull PagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof PerPagedLeaderboardKey &&
                equalFields((PerPagedLeaderboardKey) other);
    }

    public boolean equalFields(@NonNull PerPagedLeaderboardKey other)
    {
        return super.equalFields(other) &&
                (perPage == null ? other.perPage == null : perPage.equals(other.perPage));
    }

    @NonNull @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new PerPagedLeaderboardKey(this, id, page);
    }

    @Override public void putParameters(@NonNull Bundle args)
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

    public static Integer findPerPage(@NonNull Set<String> catValues, @Nullable PerPagedLeaderboardKey defaultValues)
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
        if (defaultValues != null)
        {
            return defaultValues.perPage;
        }
        return null;
    }

    @Override public void putParameters(@NonNull Set<String> catValues)
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
