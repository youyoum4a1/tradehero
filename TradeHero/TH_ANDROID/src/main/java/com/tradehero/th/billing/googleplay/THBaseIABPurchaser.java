package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 12:35 PM To change this template use File | Settings | File Templates. */
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
    public static final String TAG = THBaseIABPurchaser.class.getSimpleName();

    @Inject protected Lazy<THIABProductDetailCache> skuDetailCache;

    public THBaseIABPurchaser()
    {
        super();
    }

    @Override protected THIABPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, dataSignature);
    }

    @Override protected THIABProductDetail getProductDetails(IABSKU iabsku)
    {
        return skuDetailCache.get().get(iabsku);
    }
}
