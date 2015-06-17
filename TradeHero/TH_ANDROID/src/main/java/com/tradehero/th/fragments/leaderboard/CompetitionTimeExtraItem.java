package com.tradehero.th.fragments.leaderboard;

import android.support.annotation.NonNull;
import com.tradehero.common.time.TimeFormatFloor;
import com.tradehero.common.time.TimeUnitDayUnlimited;
import com.tradehero.common.time.TimeUnitHourInDay;
import com.tradehero.common.time.TimeUnitMilliSecondInSecond;
import com.tradehero.common.time.TimeUnitMinuteInHour;
import com.tradehero.common.time.TimeUnitSecondInMinute;
import com.tradehero.th.adapters.WrapperRecyclerAdapter;
import java.util.Date;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;

public class CompetitionTimeExtraItem implements WrapperRecyclerAdapter.ExtraItem
{
    public static final long MAX_DAY_COUNT = 99;
    public static final String DIGIT_FORMAT = "%02d";
    public static final int VIEW_TYPE_TIME = 100;
    private final PrettyTime prettyTime;
    public final Date until;

    private long days;
    private long hours;
    private long minutes;
    private long seconds;

    public String dayString;
    public String hoursString;
    public String minutesString;
    public String secondsString;

    public CompetitionTimeExtraItem(Date until)
    {
        this.until = until;
        prettyTime = new PrettyTime();
        prettyTime.clearUnits();
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitHourInDay(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitMinuteInHour(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitSecondInMinute(), new TimeFormatFloor());
        // The milliseconds have to be added to avoid infinite loop https://github.com/ocpsoft/prettytime/issues/56
        prettyTime.registerUnit(new TimeUnitMilliSecondInSecond(), new TimeFormatFloor());
        updateDate(new Date());
    }

    @Override public int getViewType()
    {
        return VIEW_TYPE_TIME;
    }

    public void updateDate(@NonNull Date now)
    {
        prettyTime.setReference(now);
        for (Duration duration : prettyTime.calculatePreciseDuration(until))
        {
            TimeUnit durationUnit = duration.getUnit();
            if (durationUnit instanceof TimeUnitDayUnlimited)
            {
                long d = duration.getQuantity();
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
            else if (durationUnit instanceof TimeUnitHourInDay)
            {
                long h = duration.getQuantity();
                if (h != hours)
                {
                    hours = h;
                    hoursString = String.format(DIGIT_FORMAT, hours);
                }
            }
            else if (durationUnit instanceof TimeUnitMinuteInHour)
            {
                long m = duration.getQuantity();
                if (m != minutes)
                {
                    minutes = m;
                    minutesString = String.format(DIGIT_FORMAT, minutes);
                }
            }
            else if (durationUnit instanceof TimeUnitSecondInMinute)
            {
                long s = duration.getQuantity();
                if (s != seconds)
                {
                    seconds = s;
                    secondsString = String.format(DIGIT_FORMAT, seconds);
                }
            }
            else
            {
                // Not caring
            }
        }
    }
}
