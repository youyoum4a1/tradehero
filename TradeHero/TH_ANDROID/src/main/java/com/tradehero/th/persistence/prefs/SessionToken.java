package com.tradehero.th.persistence.prefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/5/14 Time: 6:03 PM Copyright (c) TradeHero
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface SessionToken
{
}
