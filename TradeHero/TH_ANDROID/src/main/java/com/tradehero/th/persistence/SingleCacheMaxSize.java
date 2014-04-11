package com.tradehero.th.persistence;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

/**
 * Created by tho on 3/27/2014.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SingleCacheMaxSize
{
}
