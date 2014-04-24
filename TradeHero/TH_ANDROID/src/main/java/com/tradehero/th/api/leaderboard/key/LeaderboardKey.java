package com.tradehero.th.api.leaderboard.key;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import timber.log.Timber;

public class LeaderboardKey extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = LeaderboardKey.class.getName() + ".key";
    public static final String STRING_SET_VALUE_SEPARATOR = ":";
    public static final String STRING_SET_LEFT_KEY = "key";

    //<editor-fold desc="Constructors">
    public LeaderboardKey(Integer key)
    {
        super(key);
    }

    public LeaderboardKey(Bundle args)
    {
        super(args);
    }

    public LeaderboardKey(Set<String> catValues)
    {
        super(findKey(catValues));
    }
    //</editor-fold>

    @JsonIgnore
    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_KEY;
    }

    public static Integer findKey(Set<String> catValues)
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
        putKey(catValues, this.key);
    }

    public static void putKey(Set<String> catValues, Integer key)
    {
        if (key != null)
        {
            catValues.add(STRING_SET_LEFT_KEY + STRING_SET_VALUE_SEPARATOR + key);
        }
    }

    @Override public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            Timber.e("Failed toString", e);
            return "";
        }
    }
}
