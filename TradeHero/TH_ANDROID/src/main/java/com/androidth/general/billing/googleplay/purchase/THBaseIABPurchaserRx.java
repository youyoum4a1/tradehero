package com.androidth.general.billing.googleplay.purchase;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.purchase.BaseIABPurchaserRx;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.googleplay.THIABPurchaseOrder;
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
                iabExceptionFactory
        );
    }
    //</editor-fold>

    @NonNull @Override protected THIABPurchase createPurchase(
            @NonNull String purchaseData,
            @NonNull String dataSignature)
            throws JSONException
    {
        THIABPurchase purchase = super.createPurchase(purchaseData, dataSignature);
        if (purchaseOrder.getUserToFollow() != null)
        {
            purchase.setUserToFollow(purchaseOrder.getUserToFollow());
        }
        return purchase;
    }

    @Override @NonNull protected THIABPurchase createPurchase(
            @NonNull String itemType,
            @NonNull String purchaseData,
            @NonNull String dataSignature) throws JSONException
    {
        return new THIABPurchase(itemType, purchaseData, dataSignature);
    }
}
