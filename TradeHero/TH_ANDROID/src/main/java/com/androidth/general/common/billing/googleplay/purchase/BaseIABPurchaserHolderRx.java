package com.androidth.general.common.billing.googleplay.purchase;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.billing.googleplay.IABPurchase;
import com.androidth.general.common.billing.googleplay.IABPurchaseOrder;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.purchase.BaseBillingPurchaserHolderRx;
import timber.log.Timber;

abstract public class BaseIABPurchaserHolderRx<
        IABSKUType extends IABSKU,
        IABPurchaseOrderType extends IABPurchaseOrder<IABSKUType>,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends BaseBillingPurchaserHolderRx<
        IABSKUType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType>
        implements IABPurchaserHolderRx<
        IABSKUType,
        IABPurchaseOrderType,
        IABOrderIdType,
        IABPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseIABPurchaserHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected IABPurchaserRx<IABSKUType, IABPurchaseOrderType, IABOrderIdType, IABPurchaseType> createPurchaser(
            int requestCode, @NonNull IABPurchaseOrderType purchaseOrder);

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        Timber.d("onActivityResult requestCode: " + requestCode + ", resultCode: " + resultCode);
        IABPurchaserRx iabPurchaser = (IABPurchaserRx) actors.get(requestCode);
        if (iabPurchaser != null)
        {
            iabPurchaser.onActivityResult(activity, requestCode, resultCode, data);
        }
        else
        {
            Timber.d("onActivityResult no handler");
        }
    }
}
