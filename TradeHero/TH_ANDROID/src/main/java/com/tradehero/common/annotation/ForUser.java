package com.tradehero.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created by tradehero on 14-7-9.
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ForUser
{
}
