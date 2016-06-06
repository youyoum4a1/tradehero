package com.androidth.general.common.annotation;

import android.support.annotation.IntDef;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Documented
@Retention(SOURCE)
@Target({METHOD, PARAMETER, FIELD})
@IntDef(value = {
        VISIBLE,
        INVISIBLE,
        GONE,
})
public @interface ViewVisibilityValue
{
}
