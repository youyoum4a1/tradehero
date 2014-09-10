package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

public class THBaseIABPurchaser
        extends BaseIABPurchaser<
                IABSKU,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>
    implements THIABPurchaser
{
    @NotNull protected final Lazy<THIABProductDetailCache> skuDetailCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaser(
            @NotNull Provider<Activity> activityProvider,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NotNull Lazy<THIABProductDetailCache> skuDetailCache)
    {
        super(activityProvider,
                iabExceptionFactory);
        this.skuDetailCache = skuDetailCache;
    }
    //</editor-fold>

    @Override @NotNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, dataSignature);
    }

    @Override @Nullable protected THIABProductDetail getProductDetails(IABSKU iabsku)
    {
        return skuDetailCache.get().get(iabsku);
    }
}
