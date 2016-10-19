package com.androidth.general.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.androidth.general.common.time.TimeFormatFloor;
import com.androidth.general.adapters.WrapperRecyclerAdapter;
import java.util.Date;
import java.util.List;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Second;
import timber.log.Timber;

public class CompetitionTimeExtraItem implements WrapperRecyclerAdapter.ExtraItem
{
    public static final long MAX_DAY_COUNT = 99;
    public static final String DIGIT_FORMAT = "%02d";
    public static final int VIEW_TYPE_TIME = 100;
    private final PrettyTime prettyTime;
    public final Date until;

    private long days = -1;
    private long hours = -1;
    private long minutes = -1;
    private long seconds = -1;

    public String dayString;
    public String hoursString;
    public String minutesString;
    public String secondsString;

    public CompetitionTimeExtraItem(Date until)
    {
        this.until = until;
        prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        prettyTime.registerUnit(new Day(), new TimeFormatFloor());
        prettyTime.registerUnit(new Hour(), new TimeFormatFloor());
        prettyTime.registerUnit(new Minute(), new TimeFormatFloor());
        prettyTime.registerUnit(new Second(), new TimeFormatFloor());
        updateDate(new Date());
    }

    @Override public int getViewType()
    {
        return VIEW_TYPE_TIME;
    }

    public void updateDate(@NonNull Date now)
    {
        prettyTime.setReference(now);
        List<Duration> durations = prettyTime.calculatePreciseDuration(until);
        for (Duration duration : durations)
        {
            TimeUnit durationUnit = duration.getUnit();
            if (durationUnit instanceof Day)
            {
                long d = duration.getQuantity();
                updateDayString(d);
            }
            else if (durationUnit instanceof Hour)
            {
                long h = duration.getQuantity();
                updateHourString(h);
            }
            else if (durationUnit instanceof Minute)
            {
                long m = duration.getQuantity();
                updateMinuteString(m);
            }
            else if (durationUnit instanceof Second)
            {
                long s = duration.getQuantity();
                updateSecondString(s);
//                Timber.d("Second %d", s);
            }
        }
    }

    protected void updateSecondString(long s)
    {
        if (s != seconds)
        {
            seconds = s;
            secondsString = String.format(DIGIT_FORMAT, seconds);
        }
    }

    protected void updateMinuteString(long m)
    {
        if (m != minutes)
        {
            minutes = m;
            minutesString = String.format(DIGIT_FORMAT, minutes);
        }
    }

    protected void updateHourString(long h)
    {
        if (h != hours)
        {
            hours = h;
            hoursString = String.format(DIGIT_FORMAT, hours);
        }
    }

    protected void updateDayString(long d)
    {
        if (d > MAX_DAY_COUNT)
        {
            d = MAX_DAY_COUNT;
        }
        if (d != days)
        {
            days = d;
            dayString = String.format(DIGIT_FORMAT, days);
        }
    }
}
