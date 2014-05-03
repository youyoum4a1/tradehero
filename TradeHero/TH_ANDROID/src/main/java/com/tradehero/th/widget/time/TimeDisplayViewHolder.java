package com.tradehero.th.widget.time;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.tradehero.common.time.TimeFormatFloor;
import com.tradehero.common.time.TimeUnitDayUnlimited;
import com.tradehero.common.time.TimeUnitHourInDay;
import com.tradehero.common.time.TimeUnitMilliSecondInSecond;
import com.tradehero.common.time.TimeUnitMinuteInHour;
import com.tradehero.common.time.TimeUnitSecondInMinute;
import com.tradehero.th.R;
import java.util.Date;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;


public class TimeDisplayViewHolder
{
    public static final String TAG = TimeDisplayViewHolder.class.getSimpleName();
    public static final long MAX_DAY_COUNT = 99;

    protected Context context;
    protected TextView dayCountView;
    protected TextView hourCountView;
    protected TextView minuteCountView;
    protected TextView secondCountView;
    protected PrettyTime prettyTime;

    public TimeDisplayViewHolder(Context context)
    {
        this.context = context;
        prettyTime = new PrettyTime();
        registerTimeUnits();
    }

    protected void registerTimeUnits()
    {
        prettyTime.clearUnits();
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitHourInDay(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitMinuteInHour(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitSecondInMinute(), new TimeFormatFloor());
        // The milliseconds have to be added to avoid infinite loop https://github.com/ocpsoft/prettytime/issues/56
        prettyTime.registerUnit(new TimeUnitMilliSecondInSecond(), new TimeFormatFloor());
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
        display(new TimeDTO(prettyTime, then));
    }

    public void display(TimeDTO timeDTO)
    {
        displayDayCount(timeDTO.days);
        displayHourCount(timeDTO.hours);
        displayMinuteCount(timeDTO.minutes);
        displaySecondCount(timeDTO.seconds);
    }

    protected int getDayFormatResId()
    {
        return R.string.time_display_view_holder_format_day;
    }

    protected int getHourFormatResId()
    {
        return R.string.time_display_view_holder_format_hour;
    }

    protected int getMinuteFormatResId()
    {
        return R.string.time_display_view_holder_format_minute;
    }

    protected int getSecondFormatResId()
    {
        return R.string.time_display_view_holder_format_second;
    }

    public void displayDayCount(long days)
    {
        if (dayCountView != null)
        {
            dayCountView.setText(context.getString(getDayFormatResId(), days));
        }
    }

    public void displayHourCount(long hours)
    {
        if (hourCountView != null)
        {
            hourCountView.setText(context.getString(getHourFormatResId(), hours));
        }
    }

    public void displayMinuteCount(long minutes)
    {
        if (minuteCountView != null)
        {
            minuteCountView.setText(context.getString(getMinuteFormatResId(), minutes));
        }
    }

    public void displaySecondCount(long seconds)
    {
        if (secondCountView != null)
        {
            secondCountView.setText(context.getString(getSecondFormatResId(), seconds));
        }
    }

    protected static class TimeDTO
    {
        long days;
        long hours;
        long minutes;
        long seconds;

        public TimeDTO(PrettyTime prettyTime, Date then)
        {
            this();
            populate(prettyTime, then);
        }

        public TimeDTO()
        {
            this.days = 0;
            this.hours = 0;
            this.minutes = 0;
            this.seconds = 0;
        }

        public void populate(PrettyTime prettyTime, Date then)
        {
            for (Duration duration: prettyTime.calculatePreciseDuration(then))
            {
                TimeUnit durationUnit = duration.getUnit();
                if (durationUnit instanceof TimeUnitDayUnlimited)
                {
                    days = duration.getQuantity();
                }
                else if (durationUnit instanceof TimeUnitHourInDay)
                {
                    hours = duration.getQuantity();
                }
                else if (durationUnit instanceof TimeUnitMinuteInHour)
                {
                    minutes = duration.getQuantity();
                }
                else if (durationUnit instanceof TimeUnitSecondInMinute)
                {
                    seconds = duration.getQuantity();
                }
                else
                {
                    // Not caring
                }
            }
        }
    }
}
