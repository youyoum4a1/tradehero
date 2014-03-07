package com.tradehero.th.utils.dagger;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by tradehero on 14-3-7.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ForCertainSecurityNews
{
}
