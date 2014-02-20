package com.tradehero.common.log;

import com.testflightapp.lib.TestFlight;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/20/14 Time: 3:00 PM Copyright (c) TradeHero
 */
public class TestFlightTree extends Timber.HollowTree
{
    @Override public void e(Throwable t, String message, Object... args)
    {
        TestFlight.log(t.getMessage() + ":" + message);
    }

    @Override public void e(String message, Object... args)
    {
        TestFlight.log(message);
    }
}
