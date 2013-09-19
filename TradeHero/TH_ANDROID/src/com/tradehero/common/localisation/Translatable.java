package com.tradehero.common.localisation;

import java.lang.annotation.RetentionPolicy;

/** Created with IntelliJ IDEA. User: xavier Date: 9/16/13 Time: 12:51 PM To change this template use File | Settings | File Templates. */
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface Translatable
{
    int stringResourceId();
}
