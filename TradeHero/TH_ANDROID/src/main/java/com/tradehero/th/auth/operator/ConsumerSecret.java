package com.tradehero.th.auth.operator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/6/14 Time: 2:45 PM Copyright (c) TradeHero
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ConsumerSecret
{
    String value() default "";
}
