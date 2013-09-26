package com.tradehero.common.annotation;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 7:43 PM Copyright (c) TradeHero
 * Originally from Square, dagger sample
 * */
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Qualifier @Retention(RUNTIME)
public @interface ForApplication {
}