package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONException;

public class THBaseIABPurchaseFetcher
        extends BaseIABPurchaseFetcher<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
    implements THIABPurchaseFetcher
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseFetcher(
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NonNull THIABPurchaseCacheRx thiabPurchaseCache)
    {
        super(context, iabExceptionFactory, thiabPurchaseCache);
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, signature);
    }
}
