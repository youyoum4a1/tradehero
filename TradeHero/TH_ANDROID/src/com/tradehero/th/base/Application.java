package com.tradehero.th.base;

import com.tradehero.kit.application.PApplication;
import com.tradehero.kit.utils.THLog;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.utils.FacebookUtils;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 3:33 PM Copyright (c) TradeHero */
public class Application extends PApplication
{
    @Override protected void init()
    {
        super.init();
        NetworkEngine.initialize();
        FacebookUtils.initialize("431745923529834");
        THLog.showDeveloperKeyHash();
    }
}
