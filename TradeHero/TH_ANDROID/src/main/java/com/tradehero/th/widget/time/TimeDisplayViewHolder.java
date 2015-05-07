package com.tradehero.th.widget.time;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.time.TimeFormatFloor;
import com.tradehero.common.time.TimeUnitDayUnlimited;
import com.tradehero.common.time.TimeUnitHourInDay;
import com.tradehero.common.time.TimeUnitMilliSecondInSecond;
import com.tradehero.common.time.TimeUnitMinuteInHour;
import com.tradehero.common.time.TimeUnitSecondInMinute;
import com.tradehero.th.R;
import java.util.Date;
import javax.inject.Inject;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;

public class TimeDisplayViewHolder
{
    public static final long MAX_DAY_COUNT = 99;

    @NonNull protected final Context context;
    @InjectView(R.id.value_day_count) protected TextView dayCountView;
    @InjectView(R.id.value_hour_count) protected TextView hourCountView;
    @InjectView(R.id.value_minute_count) protected TextView minuteCountView;
    @InjectView(R.id.value_second_count) protected TextView secondCountView;
    @NonNull protected final PrettyTime prettyTime;

    //<editor-fold desc="Constructors">
    @Inject public TimeDisplayViewHolder(@NonNull Context context)
    {
        this.context = context;
        this.prettyTime = new PrettyTime();
        registerTimeUnits();
    }
    //</editor-fold>

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

    public void fetchViews(@Nullable View view)
    {
        if (view != null)
        {
            ButterKnife.inject(this, view);
        }
    }

    public void showDuration(@NonNull Date then)
    {
        prettyTime.setReference(new Date());
        display(new TimeDTO(prettyTime, then));
    }

    public void display(@NonNull TimeDTO timeDTO)
    {
        displayDayCount(timeDTO.days);
        displayHourCount(timeDTO.hours);
        displayMinuteCount(timeDTO.minutes);
        displaySecondCount(timeDTO.seconds);
    }

    @StringRes protected int getDayFormatResId()
    {
        return R.string.time_display_view_holder_format_day;
    }

    @StringRes protected int getHourFormatResId()
    {
        return R.string.time_display_view_holder_format_hour;
    }

    @StringRes protected int getMinuteFormatResId()
    {
        return R.string.time_display_view_holder_format_minute;
    }

    @StringRes protected int getSecondFormatResId()
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

        public TimeDTO(@NonNull PrettyTime prettyTime, @NonNull Date then)
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

        public void populate(@NonNull PrettyTime prettyTime, @NonNull Date then)
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
