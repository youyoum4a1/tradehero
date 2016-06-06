package com.androidth.general.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Provides object mapper for all known types.
 */
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ForApp
{
}
