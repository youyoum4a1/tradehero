package com.tradehero.common.billing.samsung;

import android.support.annotation.IntDef;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Qualifier;

import static com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper.IAP_MODE_COMMERCIAL;
import static com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper.IAP_MODE_TEST_FAIL;
import static com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper.IAP_MODE_TEST_SUCCESS;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

@Documented
@Target({METHOD, PARAMETER, FIELD})
@IntDef(value = {
        IAP_MODE_TEST_FAIL,
        IAP_MODE_COMMERCIAL,
        IAP_MODE_TEST_SUCCESS,
})
// For Dagger injection
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface SamsungBillingMode
{
}
