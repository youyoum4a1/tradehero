package com.androidth.general.common.billing.googleplay;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@Target({METHOD, PARAMETER, FIELD})
@StringDef({IABConstants.ITEM_TYPE_INAPP, IABConstants.ITEM_TYPE_SUBS})
public @interface SkuTypeValue
{
}
