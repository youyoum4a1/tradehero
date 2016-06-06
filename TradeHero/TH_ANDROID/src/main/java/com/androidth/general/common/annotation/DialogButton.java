package com.androidth.general.common.annotation;

import android.support.annotation.IntDef;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Documented
@Retention(SOURCE)
@Target({METHOD, PARAMETER, FIELD})
@IntDef(value = {
        BUTTON_NEGATIVE,
        BUTTON_NEUTRAL,
        BUTTON_POSITIVE,
})
public @interface DialogButton
{
}
