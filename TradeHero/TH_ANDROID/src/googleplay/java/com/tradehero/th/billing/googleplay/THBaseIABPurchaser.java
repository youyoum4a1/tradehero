package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.BaseIABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
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
    @NonNull protected final Lazy<THIABProductDetailCacheRx> skuDetailCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaser(
            @NonNull Activity activity,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory,
            @NonNull Lazy<THIABProductDetailCacheRx> skuDetailCache)
    {
        super(activity,
                iabExceptionFactory);
        this.skuDetailCache = skuDetailCache;
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, dataSignature);
    }

    @Override @Nullable protected THIABProductDetail getProductDetails(IABSKU iabsku)
    {
        return skuDetailCache.get().getCachedValue(iabsku);
    }
}
