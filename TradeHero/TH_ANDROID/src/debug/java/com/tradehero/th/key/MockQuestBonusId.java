package com.tradehero.th.key;

import com.tradehero.th.api.achievement.key.QuestBonusId;

public class MockQuestBonusId extends QuestBonusId
{
    private static final String BUNDLE_KEY = MockQuestBonusId.class.getName() + ".key";

    public int xpEarned;
    public int xpTotal;

    public MockQuestBonusId(Integer key, int xpEarned, int xpTotal)
    {
        super(key);
        this.xpEarned = xpEarned;
        this.xpTotal = xpTotal;
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof MockQuestBonusId)) return false;
        if (!super.equals(o)) return false;

        MockQuestBonusId that = (MockQuestBonusId) o;

        if (xpEarned != that.xpEarned) return false;
        if (xpTotal != that.xpTotal) return false;

        return true;
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
