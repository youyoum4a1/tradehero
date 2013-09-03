package com.tradehero.th.base;

import com.tradehero.common.application.PApplication;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.TwitterUtils;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 3:33 PM Copyright (c) TradeHero */
public class Application extends PApplication
{
    @Override protected void init()
    {
        super.init();
        NetworkEngine.initialize();
        THUser.initialize();
        EmailSignUtils.initialize();
        FacebookUtils.initialize(Application.getResourceString(R.string.FACEBOOK_APP_ID));
        TwitterUtils.initialize(
                Application.getResourceString(R.string.TWITTER_CONSUMER_KEY),
                Application.getResourceString(R.string.TWITTER_CONSUMER_SECRET));
        LinkedInUtils.initialize(
                Application.getResourceString(R.string.LINKEDIN_CONSUMER_KEY),
                Application.getResourceString(R.string.LINKEDIN_CONSUMER_SECRET));
        THLog.showDeveloperKeyHash();
    }
}
