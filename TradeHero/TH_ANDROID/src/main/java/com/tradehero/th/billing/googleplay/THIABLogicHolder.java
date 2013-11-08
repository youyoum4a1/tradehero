package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.BaseIABActor;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.PurchaseFetcher;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import java.util.List;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
abstract public class THIABLogicHolder
    extends BaseIABActor<IABSKU, THSKUDetails, IABOrderId, SKUPurchase, SKUDetailsPurchaser, THIABPurchaseHandler>
    implements THIABActor
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();


    public THIABLogicHolder(Activity activity)
    {
        super(activity);
    }

    protected SKUDetailsPurchaser createPurchaser(final int requestCode)
    {
        SKUDetailsPurchaser purchaser = new SKUDetailsPurchaser(getActivity());
        purchaser.setPurchaseFinishedListener(purchaseFinishedListeners.get(requestCode));
        return purchaser;
    }
}
