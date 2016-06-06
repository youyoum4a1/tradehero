package com.androidth.general.utils.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ForAnalytics
{
}
