package com.ayondo.academy.persistence.timing;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.prefs.LongPreference;

public class TimingIntervalPreference extends LongPreference
{
    public static final long MILLISECOND = 1;
    public static final long SECOND = 1000 * MILLISECOND;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;
    public static final long MONTH = 30 * DAY;
    public static final long YEAR = (long) (365.242f * DAY);

    public final long interval;

    //<editor-fold desc="Constructors">
    public TimingIntervalPreference(
            @NonNull SharedPreferences preference,
            @NonNull String key,
            long interval)
    {
        super(preference, key, System.currentTimeMillis());
        this.interval = interval;
    }
    //</editor-fold>

    public boolean isItTime()
    {
        return get() < System.currentTimeMillis();
    }

    public void justHandled()
    {
        pushInFuture(interval);
    }

    public void pushInFuture(long byDuration)
    {
        set(System.currentTimeMillis() + byDuration);
    }

    public void addInFuture(long byDuration)
    {
        set(get() + byDuration);
    }
}
