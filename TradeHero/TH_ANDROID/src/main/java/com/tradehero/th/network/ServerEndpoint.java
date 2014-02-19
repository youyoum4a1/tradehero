package com.tradehero.th.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/18/14 Time: 4:45 PM Copyright (c) TradeHero
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ServerEndpoint
{
}
