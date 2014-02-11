package com.tradehero.th.utils.dagger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that we are referring to the UI thread
 * Created by xavier on 2/11/14.
 */
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface ForUIThread
{
}
