package com.ayondo.academy.billing.samsung.purchase;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.BaseSamsungPurchaserHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import com.ayondo.academy.billing.samsung.THSamsungPurchaseOrder;
import com.ayondo.academy.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungPurchaserHolderRx
        extends BaseSamsungPurchaserHolderRx<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaserHolderRx
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaserHolderRx(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungPurchaserRx createPurchaser(
            int requestCode,
            @NonNull THSamsungPurchaseOrder purchaseOrder)
    {
        return new THBaseSamsungPurchaserRx(
                requestCode,
                context,
                mode,
                purchaseOrder,
                true);
    }
}
