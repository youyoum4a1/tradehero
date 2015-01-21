package com.tradehero.th.api.security;

import android.support.annotation.StringDef;
import com.tradehero.th.api.security.compact.WarrantDTO;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Documented
@Retention(SOURCE)
@Target({METHOD, PARAMETER, FIELD})
@StringDef(value = {
        WarrantDTO.CALL_SHORT_CODE,
        WarrantDTO.PUT_SHORT_CODE,
})
public @interface WarrantTypeShortCodeDef
{
}
