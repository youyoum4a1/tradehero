package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
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
            @NotNull Provider<Activity> activityProvider,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NotNull THIABPurchaseCache thiabPurchaseCache)
    {
        super(activityProvider, iabExceptionFactory, thiabPurchaseCache);
    }
    //</editor-fold>

    @Override @NotNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, signature);
    }
}
