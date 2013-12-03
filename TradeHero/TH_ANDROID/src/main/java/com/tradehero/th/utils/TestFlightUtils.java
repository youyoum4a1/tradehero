package com.tradehero.th.utils;

import com.testflightapp.lib.TestFlight;
import com.tradehero.th.base.Application;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 6:26 PM Copyright (c) TradeHero */
public class TestFlightUtils
{
    public static void initialize()
    {
        TestFlight.takeOff(Application.context(), Constants.TEST_FLIGHT_TOKEN);
    }
}
