package com.tradehero.th.auth;

import com.tradehero.RobolectricMavenTestRunner;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class FacebookAuthenticationProviderTest
{
    @Test public void testDateParsing() throws ParseException
    {
        String received = "2014-07-31T01:00:32.000Z";
        Date parsed = FacebookAuthenticationProvider.preciseDateFormat.parse(received);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 32);
        calendar.set(Calendar.MILLISECOND, 0);
        Date expected = new Date(calendar.getTimeInMillis());
        assertEquals(expected, parsed);
    }
}
