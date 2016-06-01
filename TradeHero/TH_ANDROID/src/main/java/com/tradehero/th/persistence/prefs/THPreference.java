package com.ayondo.academy.persistence.prefs;

import javax.inject.Qualifier;

/**
 * Created by liangyx on 3/6/15.
 */
@Qualifier
public @interface THPreference
{
    String value() default "default";
}
