package com.androidth.general.billing.googleplay.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.identifier.BaseIABProductIdentiferFetcherRx;
import com.androidth.general.common.billing.identifier.ProductIdentifierListResult;
import com.androidth.general.billing.googleplay.THIABConstants;
import java.util.HashMap;
import java.util.Map;
import rx.Observable;

public class THBaseIABProductIdentifierFetcherRx
        extends BaseIABProductIdentiferFetcherRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList>
        implements THIABProductIdentifierFetcherRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABProductIdentifierFetcherRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
    }
    //</editor-fold>

    @NonNull @Override public Observable<ProductIdentifierListResult<
            IABSKUListKey,
            IABSKU,
            IABSKUList>> get()
    {
        // TODO hard-coded while there is nothing coming from the server.
        IABSKUList inAppIABSKUs = new IABSKUList();
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T0_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T1_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T2_KEY));

        inAppIABSKUs.add(new IABSKU(THIABConstants.RESET_PORTFOLIO_0));
        Map<IABSKUListKey, IABSKUList> mapped = new HashMap<>();
        mapped.put(IABSKUListKey.getInApp(), inAppIABSKUs);
        return Observable.just(new ProductIdentifierListResult<>(getRequestCode(), mapped));
    }
}
