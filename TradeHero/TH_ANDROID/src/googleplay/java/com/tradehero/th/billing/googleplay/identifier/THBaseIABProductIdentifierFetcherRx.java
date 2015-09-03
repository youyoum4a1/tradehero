package com.tradehero.th.billing.googleplay.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.identifier.BaseIABProductIdentiferFetcherRx;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import com.tradehero.th.billing.googleplay.THIABConstants;
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
        IABSKUList subsIABSKUs = new IABSKUList();
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T0_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T1_KEY));
        inAppIABSKUs.add(new IABSKU(THIABConstants.EXTRA_CASH_T2_KEY));

        //inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_1));
        //inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_10));
        //inAppIABSKUs.add(new IABSKU(THIABConstants.CREDIT_20));

        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_1));
        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_5));
        subsIABSKUs.add(new IABSKU(THIABConstants.ALERT_UNLIMITED));

        inAppIABSKUs.add(new IABSKU(THIABConstants.RESET_PORTFOLIO_0));
        Map<IABSKUListKey, IABSKUList> mapped = new HashMap<>();
        mapped.put(IABSKUListKey.getInApp(), inAppIABSKUs);
        mapped.put(IABSKUListKey.getSubs(), subsIABSKUs);
        return Observable.just(new ProductIdentifierListResult<>(getRequestCode(), mapped));
    }
}
