package com.androidth.general.billing.googleplay.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.SkuTypeValue;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.purchasefetch.BaseIABPurchaseFetcherRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
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
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
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
