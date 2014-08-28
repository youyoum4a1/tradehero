package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.activities.CurrentActivityHolder;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
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
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NotNull THIABPurchaseCache thiabPurchaseCache)
    {
        super(currentActivityHolder, iabExceptionFactory, thiabPurchaseCache);
    }
    //</editor-fold>

    @Override @NotNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, signature);
    }
}
