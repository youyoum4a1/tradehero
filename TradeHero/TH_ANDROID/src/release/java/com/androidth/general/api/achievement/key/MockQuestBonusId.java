package com.androidth.general.api.achievement.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MockQuestBonusId extends QuestBonusId
{
    private static final String BUNDLE_KEY = MockQuestBonusId.class.getName() + ".key";

    public int xpEarned;
    public int xpTotal;

    //<editor-fold desc="Constructors">
    public MockQuestBonusId(@NonNull Integer key, int xpEarned, int xpTotal)
    {
        super(key);
        this.xpEarned = xpEarned;
        this.xpTotal = xpTotal;
    }
    //</editor-fold>

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @Override
    public boolean equals(@Nullable Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof MockQuestBonusId))
        {
            return false;
        }
        if (!super.equals(o))
        {
            return false;
        }

        MockQuestBonusId that = (MockQuestBonusId) o;

        if (xpEarned != that.xpEarned)
        {
            return false;
        }
        return xpTotal == that.xpTotal;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + xpEarned;
        result = 31 * result + xpTotal;
        return result;
    }
}
