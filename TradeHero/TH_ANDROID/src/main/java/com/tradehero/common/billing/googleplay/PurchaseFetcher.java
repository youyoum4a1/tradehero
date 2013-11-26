package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
public class PurchaseFetcher extends IABPurchaseFetcher<IABSKU, THIABOrderId, SKUPurchase>
{
    public static final String TAG = PurchaseFetcher.class.getSimpleName();

    public PurchaseFetcher(Context ctx)
    {
        super(ctx);
    }

    @Override protected SKUPurchase createPurchase(String itemType, String purchaseData, String signature) throws JSONException
    {
        return new SKUPurchase(itemType, purchaseData, signature);
    }
}
