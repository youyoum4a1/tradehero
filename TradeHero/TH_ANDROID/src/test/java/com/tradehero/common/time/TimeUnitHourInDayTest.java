package com.tradehero.common.time;

import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.prettytime.Duration;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.TimeUnit;
import org.ocpsoft.prettytime.impl.DurationImpl;
import org.ocpsoft.prettytime.impl.ResourcesTimeFormat;
import org.ocpsoft.prettytime.units.Day;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by xavier on 1/23/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class TimeUnitHourInDayTest
{
    public static final String TAG = TimeUnitHourInDayTest.class.getSimpleName();

    @Test public void calculatePreciseDurationOnlyDay()
    {
        Date later = new Date(2013, 1, 12);

        PrettyTime prettyTime = new PrettyTime(new Date(2013, 1, 10));
        List<Duration> durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(1, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof Day);
        assertEquals(2, durationList.get(0).getQuantity());

        prettyTime.setReference(new Date(2013, 1, 9));
        durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(1, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof Day);
        assertEquals(3, durationList.get(0).getQuantity());
    }

    @Test public void calculatePreciseDurationDayHour()
    {
        Date later = new Date(2013, 1, 12, 14, 0);
        PrettyTime prettyTime = new PrettyTime(new Date(2013, 1, 10));
        List<Duration> durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(2, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof Day);
        assertEquals(2, durationList.get(0).getQuantity());
        assertEquals(14, durationList.get(1).getQuantity());
    }

    @Test public void calculatePreciseDurationDay50()
    {
        Date later = new Date(2013, 1, 20);
        PrettyTime prettyTime = new PrettyTime(new Date(2013, 0, 1));
        List<TimeUnit> units = prettyTime.clearUnits();
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());

        List<Duration> durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(1, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof TimeUnitDay);
        assertEquals(50, durationList.get(0).getQuantity());
    }

    @Test public void calculatePreciseDurationDay50Hour4()
    {
        Date later = new Date(2013, 1, 20, 4, 0);
        PrettyTime prettyTime = new PrettyTime(new Date(2013, 0, 1));
        List<TimeUnit> units = prettyTime.clearUnits();
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitHourInDay(), new TimeFormatFloor());

        List<Duration> durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(2, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof TimeUnitDay);
        assertEquals(50, durationList.get(0).getQuantity());
        assertTrue(durationList.get(1).getUnit() instanceof TimeUnitHourInDay);
        assertEquals(4, durationList.get(1).getQuantity());
    }

    @Test public void calculatePreciseDurationDay50Hour4Reversed()
    {
        Date later = new Date(2013, 1, 20, 4, 0);
        PrettyTime prettyTime = new PrettyTime(new Date(2013, 0, 1));
        List<TimeUnit> units = prettyTime.clearUnits();
        prettyTime.registerUnit(new TimeUnitHourInDay(), new TimeFormatFloor());
        prettyTime.registerUnit(new TimeUnitDayUnlimited(), new TimeFormatFloor());

        List<Duration> durationList = prettyTime.calculatePreciseDuration(later);
        assertEquals(2, durationList.size());
        assertTrue(durationList.get(0).getUnit() instanceof TimeUnitDay);
        assertEquals(50, durationList.get(0).getQuantity());
        assertTrue(durationList.get(1).getUnit() instanceof TimeUnitHourInDay);
        assertEquals(4, durationList.get(1).getQuantity());
    }
}
