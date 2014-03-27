package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
public class THBaseIABPurchaseFetcher
        extends BaseIABPurchaseFetcher<
        IABSKU,
        THIABOrderId,
        THIABPurchase>
    implements THIABPurchaseFetcher
{
    public static final String TAG = THBaseIABPurchaseFetcher.class.getSimpleName();

    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseFetcher()
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
