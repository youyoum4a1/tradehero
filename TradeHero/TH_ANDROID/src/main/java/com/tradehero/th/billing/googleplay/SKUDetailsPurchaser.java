package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.th.persistence.billing.ProductDetailCache;
import com.tradehero.th.persistence.billing.googleplay.THSKUDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 12:35 PM To change this template use File | Settings | File Templates. */
public class SKUDetailsPurchaser extends IABPurchaser<IABSKU, THSKUDetails, THIABPurchaseOrder, THIABOrderId, SKUPurchase>
{
    public static final String TAG = SKUDetailsPurchaser.class.getSimpleName();

    @Inject protected Lazy<THSKUDetailCache> skuDetailCache;

    public SKUDetailsPurchaser(Activity activity)
    {
        super(activity);
        DaggerUtils.inject(this);
    }

    @Override protected SKUPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new SKUPurchase(itemType, purchaseData, dataSignature);
    }

    @Override protected THSKUDetails getProductDetails(IABSKU iabsku)
    {
        return skuDetailCache.get().get(iabsku);
    }
}
