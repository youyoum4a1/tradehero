package com.tradehero.th.base;

import com.tradehero.common.application.PApplication;
import com.tradehero.common.thread.KnownExecutorServices;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.PushUtils;
import com.tradehero.th.utils.TestFlightUtils;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 3:33 PM Copyright (c) TradeHero */
public class Application extends PApplication
{
    public static final String TAG = Application.class.getSimpleName();

    @Override protected void init()
    {
        super.init();

        // Supposedly get the count of cores
        KnownExecutorServices.setCpuThreadCount(Runtime.getRuntime().availableProcessors());
        THLog.d(TAG, "Available Processors Count: " + KnownExecutorServices.getCpuThreadCount());

        DaggerUtils.initialize();

        THUser.initialize();

        EmailSignUtils.initialize();

        PushUtils.initialize();

        TestFlightUtils.initialize();

        THLog.showDeveloperKeyHash();
    }
}
