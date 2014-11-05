package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Deprecated
public class SortedPerPagedLeaderboardKey extends PerPagedLeaderboardKey
{
    public final static String BUNDLE_KEY_SORT_TYPE = SortedPerPagedLeaderboardKey.class.getName() + ".sortType";

    public final Integer sortType;

    //<editor-fold desc="Constructors">
    public SortedPerPagedLeaderboardKey(Integer leaderboardDefKey, Integer page, Integer perPage, Integer sortType)
    {
        super(leaderboardDefKey, page, perPage);
        this.sortType = sortType;
    }

    public SortedPerPagedLeaderboardKey(SortedPerPagedLeaderboardKey other, Integer overrideKey, Integer page)
    {
        super(other, overrideKey, page);
        this.sortType = other.sortType;
    }

    public SortedPerPagedLeaderboardKey(Bundle args, @Nullable SortedPerPagedLeaderboardKey defaultValues)
    {
        super(args, defaultValues);
        this.sortType = args.containsKey(BUNDLE_KEY_SORT_TYPE) ? args.getInt(BUNDLE_KEY_SORT_TYPE) : ((defaultValues != null) ? defaultValues.sortType : null);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return super.hashCode() ^ (sortType == null ? 0 : sortType.hashCode());
    }

    @Override public boolean equalFields(@NonNull PerPagedLeaderboardKey other)
    {
        return super.equalFields(other)
                && other instanceof SortedPerPagedLeaderboardKey
                && equalFields((SortedPerPagedLeaderboardKey) other);
    }

    public boolean equalFields(@NonNull SortedPerPagedLeaderboardKey other)
    {
        return super.equalFields(other) &&
                (sortType == null ? other.sortType == null : sortType.equals(other.sortType));
    }

    @Override public PagedLeaderboardKey cloneAtPage(int page)
    {
        return new SortedPerPagedLeaderboardKey(this, id, page);
    }

    @Override public void putParameters(@NonNull Bundle args)
    {
        super.putParameters(args);
        if (sortType == null)
        {
            args.remove(BUNDLE_KEY_SORT_TYPE);
        }
        else
        {
            args.putInt(BUNDLE_KEY_SORT_TYPE, sortType);
        }
    }
}
