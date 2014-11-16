package com.tradehero.th.billing.googleplay.purchase;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.purchase.BaseIABPurchaserRx;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import org.json.JSONException;

public class THBaseIABPurchaserRx
        extends BaseIABPurchaserRx<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaserRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABPurchaserRx(
            int requestCode,
            @NonNull THIABPurchaseOrder purchaseOrder,
            @NonNull Activity activity,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode,
                purchaseOrder,
                activity,
                iabExceptionFactory);
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchase createPurchase(String itemType, String purchaseData, String dataSignature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, dataSignature);
    }
}
