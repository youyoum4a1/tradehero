package com.tradehero.th.auth.operator;

import com.tradehero.th.api.social.SocialNetworkEnum;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Qualifier;

@Qualifier @Retention(RetentionPolicy.RUNTIME)
public @interface ConsumerSecret
{
    SocialNetworkEnum value();
}
