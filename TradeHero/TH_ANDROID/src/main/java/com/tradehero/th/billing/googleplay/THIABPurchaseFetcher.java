package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import org.json.JSONException;

public class THIABPurchaseFetcher
        extends IABPurchaseFetcher<IABSKU, THIABOrderId, THIABPurchase>
{
    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THIABPurchaseFetcher()
    {
        super();
    }
    //</editor-fold>

    @Override protected IABPurchaseCache<IABSKU, THIABOrderId, THIABPurchase> getPurchaseCache()
    {
        return thiabPurchaseCache;
    }

    @Override protected THIABPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, signature);
    }
}
