package com.androidth.general.api.leaderboard.key;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.persistence.DTOKey;
import com.androidth.general.common.utils.THJsonAdapter;
import com.androidth.general.api.portfolio.AssetClass;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import timber.log.Timber;

public class LeaderboardKey implements DTOKey
{
    public static final String BUNDLE_KEY_ID = LeaderboardKey.class.getName() + ".id";
    public static final String STRING_SET_VALUE_SEPARATOR = ":";
    public static final String STRING_SET_LEFT_KEY = "id";

    @NonNull public final Integer id;
    @Nullable private AssetClass assetClass;

    //<editor-fold desc="Constructors">
    public LeaderboardKey(int id)
    {
        super();
        this.id = id;
    }

    public LeaderboardKey(int id, @Nullable AssetClass assetClass)
    {
        this.id = id;
        this.assetClass = assetClass;
    }

    public LeaderboardKey(@NonNull Bundle args)
    {
        this(args.getInt(BUNDLE_KEY_ID));
    }

    public LeaderboardKey(@NonNull Bundle args, @SuppressWarnings("UnusedParameters") @Nullable LeaderboardKey defaultValues)
    {
        this(args);
    }

    public LeaderboardKey(@NonNull Set<String> catValues, @Nullable LeaderboardKey defaultValues)
    {
        //noinspection ConstantConditions
        this(findKey(catValues, defaultValues));
    }
    //</editor-fold>

    @NonNull public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    protected void putParameters(@NonNull Bundle args)
    {
        args.putInt(BUNDLE_KEY_ID, id);
    }

    @Nullable
    public static Integer findKey(@NonNull Set<String> catValues, @Nullable LeaderboardKey defaultValues)
    {
        Iterator<String> iterator = catValues.iterator();
        String catValue;
        String[] split;
        while (iterator.hasNext())
        {
            catValue = iterator.next();
            split = catValue.split(STRING_SET_VALUE_SEPARATOR);
            if (split[0].equals(STRING_SET_LEFT_KEY))
            {
                return Integer.valueOf(split[1]);
            }
        }
        if (defaultValues != null)
        {
            return defaultValues.id;
        }
        return null;
    }

    @JsonIgnore
    public HashSet<String> getFilterStringSet()
    {
        HashSet<String> set = new HashSet<>();
        putParameters(set);
        return set;
    }

    public void putParameters(Set<String> catValues)
    {
        putKey(catValues, this.id);
    }

    public static void putKey(Set<String> catValues, Integer key)
    {
        if (key != null)
        {
            catValues.add(STRING_SET_LEFT_KEY + STRING_SET_VALUE_SEPARATOR + key);
        }
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other != null
                && other.getClass().equals(getClass())
                && equalFields((LeaderboardKey) other);
    }

    protected boolean equalFields(@NonNull LeaderboardKey other)
    {
        return id.equals(other.id)
                && (assetClass == null ? other.assetClass == null : assetClass.equals(other.assetClass));
    }

    @Override public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        } catch (IOException e)
        {
            Timber.e(e, "Failed toString");
            return "";
        }
    }

    public AssetClass getAssetClass()
    {
        return assetClass;
    }

    public void setAssetClass(@NonNull AssetClass assetClass)
    {
        this.assetClass = assetClass;
    }
}
