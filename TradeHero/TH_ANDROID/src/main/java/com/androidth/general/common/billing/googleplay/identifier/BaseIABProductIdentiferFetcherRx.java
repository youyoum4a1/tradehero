package com.androidth.general.common.billing.googleplay.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.BaseIABSKUList;
import com.androidth.general.common.billing.googleplay.BaseIABServiceCaller;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;

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
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
    }
    //</editor-fold>
}
