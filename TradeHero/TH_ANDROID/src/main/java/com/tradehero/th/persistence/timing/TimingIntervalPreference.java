package com.tradehero.th.persistence.timing;

import android.content.SharedPreferences;
import com.tradehero.common.persistence.prefs.LongPreference;
import org.jetbrains.annotations.NotNull;

public class TimingIntervalPreference extends LongPreference
{
    public static final long MILLISECOND = 1;
    public static final long SECOND = 1000 * MILLISECOND;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;
    public static final long MONTH = 30 * DAY;
    public static final long YEAR = 365 * DAY;

    public final long interval;

    //<editor-fold desc="Constructors">
    public TimingIntervalPreference(
            @NotNull SharedPreferences preference,
            @NotNull String key,
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
}
