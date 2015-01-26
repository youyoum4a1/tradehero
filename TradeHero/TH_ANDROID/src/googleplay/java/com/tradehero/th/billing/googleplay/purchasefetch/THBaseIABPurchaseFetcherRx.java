package com.tradehero.th.billing.googleplay.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SkuTypeValue;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.purchasefetch.BaseIABPurchaseFetcherRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import javax.inject.Inject;
import org.json.JSONException;

public class THBaseIABPurchaseFetcherRx
        extends BaseIABPurchaseFetcherRx<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaseFetcherRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory,
            @NonNull BillingServiceBinderObservable billingServiceBinderObservable)
    {
        super(requestCode, context, iabExceptionFactory, billingServiceBinderObservable);
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchase createPurchase(
            @NonNull @SkuTypeValue String itemType,
            @NonNull String purchaseData,
            @NonNull String signature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, signature);
    }
}
