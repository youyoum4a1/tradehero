package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 12:35 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaser extends IABPurchaser<IABSKU, THIABProductDetail, THIABOrderId, THIABPurchaseOrder, BaseIABPurchase>
{
    public static final String TAG = THIABPurchaser.class.getSimpleName();

    @Inject protected Lazy<THIABProductDetailCache> skuDetailCache;

    public THIABPurchaser(Activity activity)
    {
        super(activity);
        DaggerUtils.inject(this);
    }

    @Override protected BaseIABPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new BaseIABPurchase(itemType, purchaseData, dataSignature);
    }

    @Override protected THIABProductDetail getProductDetails(IABSKU iabsku)
    {
        return skuDetailCache.get().get(iabsku);
    }
}
