package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaser;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.json.JSONException;

public class THIABPurchaser extends IABPurchaser<IABSKU, THIABProductDetail, THIABOrderId, THIABPurchaseOrder, THIABPurchase, IABException>
{
    @Inject protected Lazy<THIABProductDetailCache> skuDetailCache;

    public THIABPurchaser()
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
