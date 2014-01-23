package com.tradehero.th.widget.time;

import android.view.View;
import android.widget.TextView;
import com.tradehero.common.time.TimeFormatFloor;
import com.tradehero.common.time.TimeUnitDayUnlimited;
import com.tradehero.common.time.TimeUnitHourInDay;
import com.tradehero.common.time.TimeUnitMinuteInHour;
import com.tradehero.common.time.TimeUnitSecondInMinute;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import java.util.Date;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.units.Day;
import org.ocpsoft.prettytime.units.Hour;
import org.ocpsoft.prettytime.units.Minute;
import org.ocpsoft.prettytime.units.Second;

/**
 * Created by xavier on 1/23/14.
 */
public class TimeDisplayViewHolder
{
    public static final String TAG = TimeDisplayViewHolder.class.getSimpleName();
    public static final long MAX_DAY_COUNT = 99;

    protected TextView dayCountView;
    protected TextView hourCountView;
    protected TextView minuteCountView;
    protected TextView secondCountView;
    protected PrettyTime prettyTime;

    public TimeDisplayViewHolder()
    {
        prettyTime = new PrettyTime();
        registerTimeUnit();
    }

    protected void registerTimeUnit()
    {
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitHourInDay(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitMinuteInHour(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitSecondInMinute(), new TimeFormatFloor());
    }

    public void fetchViews(View view)
    {
        if (view != null)
        {
            dayCountView = (TextView) view.findViewById(R.id.value_day_count);
            hourCountView = (TextView) view.findViewById(R.id.value_hour_count);
            minuteCountView = (TextView) view.findViewById(R.id.value_minute_count);
            secondCountView = (TextView) view.findViewById(R.id.value_second_count);
        }
    }

    public void showDuration(Date then)
    {
        prettyTime.setReference(new Date());
        for (Duration duration: prettyTime.calculatePreciseDuration(then))
        {
            TimeUnit durationUnit = duration.getUnit();
            if (durationUnit instanceof TimeUnitDayUnlimited)
            {
                displayDayCount(duration);
            }
            else if (durationUnit instanceof TimeUnitHourInDay)
            {
                displayHourCount(duration);
            }
            else if (durationUnit instanceof TimeUnitMinuteInHour)
            {
                displayMinuteCount(duration);
            }
            else if (durationUnit instanceof TimeUnitSecondInMinute)
            {
                displaySecondCount(duration);
            }
            else
            {
                // Not caring
                THLog.d(TAG, "Unhandled duration of time " + durationUnit.getClass() + ", quantity " + duration.getQuantity());
            }
        }
    }

    public void displayDayCount(Duration duration)
    {
        assert (duration instanceof Day);
        if (dayCountView != null)
        {
            dayCountView.setText(String.format("%d", duration.getQuantity()));
        }
    }

    public void displayHourCount(Duration duration)
    {
        assert (duration instanceof Hour);
        if (hourCountView != null)
        {
            hourCountView.setText(String.format("%d", duration.getQuantity()));
        }
    }

    public void displayMinuteCount(Duration duration)
    {
        assert (duration instanceof Minute);
        if (minuteCountView != null)
        {
            minuteCountView.setText(String.format("%d", duration.getQuantity()));
        }
    }

    public void displaySecondCount(Duration duration)
    {
        assert (duration instanceof Second);
        if (secondCountView != null)
        {
            secondCountView.setText(String.format("%d", duration.getQuantity()));
        }
    }
}
