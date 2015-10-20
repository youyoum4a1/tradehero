package com.tradehero.th.persistence.prefs;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

// TODO: Temporarily, need confirm the timeout for live and better way to handle detection
@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface IsLiveLogIn
{
}
