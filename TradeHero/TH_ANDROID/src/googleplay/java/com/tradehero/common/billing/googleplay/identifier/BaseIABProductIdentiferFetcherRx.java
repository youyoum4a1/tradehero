package com.tradehero.common.billing.googleplay.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;

abstract public class BaseIABProductIdentiferFetcherRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>>
        extends BaseIABServiceCaller
        implements IABProductIdentifierFetcherRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType>
{
    //<editor-fold desc="Constructors">
    public BaseIABProductIdentiferFetcherRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
    }
    //</editor-fold>
}
