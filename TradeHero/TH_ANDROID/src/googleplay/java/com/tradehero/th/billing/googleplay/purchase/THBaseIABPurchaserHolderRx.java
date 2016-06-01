package com.ayondo.academy.billing.googleplay.purchase;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.purchase.BaseIABPurchaserHolderRx;
import com.tradehero.common.billing.googleplay.purchase.IABPurchaserRx;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.googleplay.THIABPurchaseOrder;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaserHolderRx
        extends BaseIABPurchaserHolderRx<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaserHolderRx
{
    @NonNull protected final Provider<Activity> activityProvider;
    @NonNull protected final IABExceptionFactory iabExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject THBaseIABPurchaserHolderRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super();
        this.activityProvider = activityProvider;
        this.iabExceptionFactory = iabExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected IABPurchaserRx<IABSKU, THIABPurchaseOrder, THIABOrderId, THIABPurchase> createPurchaser(
            int requestCode,
            @NonNull THIABPurchaseOrder purchaseOrder)
    {
        return new THBaseIABPurchaserRx(requestCode, purchaseOrder, activityProvider.get(), iabExceptionFactory);
    }
}
