package com.tradehero.th.fragments.social.follower;

/**
 * Created by tho on 4/14/2014.
 */
public enum MessageLifeTime
{
    LIFETIME_FOREVER(0),
    LIFETIME_1_HOUR(1),
    LIFETIME_2_HOURS(2),
    LIFETIME_1_DAY(3);

    public final int id;

    private MessageLifeTime(int id)
    {
        this.id = id;
    }

    @Override public String toString()
    {
        switch (this)
        {
            case LIFETIME_FOREVER:
                return "Forever";
            case LIFETIME_1_HOUR:
                return "One hour";
            case LIFETIME_2_HOURS:
                return "Two hour";
            case LIFETIME_1_DAY:
                return "One day";
        }
        return null;
    }
}