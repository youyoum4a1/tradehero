package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
public class THIABPurchaseFetcher
        extends IABPurchaseFetcher<IABSKU, THIABOrderId, THIABPurchase>
{
    public static final String TAG = THIABPurchaseFetcher.class.getSimpleName();

    @Inject protected THIABPurchaseCache thiabPurchaseCache;

    //<editor-fold desc="Constructors">
    public THIABPurchaseFetcher(Context ctx)
    {
        super(ctx);
        DaggerUtils.inject(this);
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
