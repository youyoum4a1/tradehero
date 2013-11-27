package com.tradehero.common.billing.googleplay;

import android.content.Context;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
public class BaseIABPurchaseFetcher
        extends IABPurchaseFetcher<IABSKU, THIABOrderId, BaseIABPurchase>
{
    public static final String TAG = BaseIABPurchaseFetcher.class.getSimpleName();

    public BaseIABPurchaseFetcher(Context ctx)
    {
        super(ctx);
    }

    @Override protected BaseIABPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new BaseIABPurchase(itemType, purchaseData, signature);
    }
}
