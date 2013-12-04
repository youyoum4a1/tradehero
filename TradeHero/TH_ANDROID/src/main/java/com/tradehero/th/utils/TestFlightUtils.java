package com.tradehero.th.utils;

import com.testflightapp.lib.TestFlight;
import com.tradehero.th.base.Application;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 6:26 PM Copyright (c) TradeHero */
public class TestFlightUtils
{
    public static void initialize()
    {
        if (Constants.TEST_FLIGHT_ENABLED)
        {
            TestFlight.takeOff(Application.context(), Constants.TEST_FLIGHT_TOKEN);
        }
    }

    public static void end()
    {
        if (Constants.TEST_FLIGHT_ENABLED)
        {
            TestFlight.land();
        }
    }

    public static void passCheckpoint(String checkPoint)
    {
        if (Constants.TEST_FLIGHT_ENABLED && Constants.TEST_FLIGHT_REPORT_CHECKPOINT)
        {
            TestFlight.passCheckpoint(checkPoint);
        }
    }

    public static void log(String log)
    {
        if (Constants.TEST_FLIGHT_ENABLED && Constants.TEST_FLIGHT_REPORT_LOG)
        {
            TestFlight.log(log);
        }
    }

    public static void log(String log, Throwable throwable)
    {
        if (Constants.TEST_FLIGHT_ENABLED && Constants.TEST_FLIGHT_REPORT_LOG)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);
            log(log + "\n" + sw.toString());
        }
    }

    public static void log(String tag, String msg, Throwable throwable)
    {
        if (Constants.TEST_FLIGHT_ENABLED && Constants.TEST_FLIGHT_REPORT_LOG)
        {
            log (tag + " " + msg, throwable);
        }
    }
}
