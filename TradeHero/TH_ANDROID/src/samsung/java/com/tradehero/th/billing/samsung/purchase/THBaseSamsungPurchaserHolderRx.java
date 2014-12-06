package com.tradehero.th.billing.samsung.purchase;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.BaseSamsungPurchaserHolderRx;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.samsung.ForSamsungBillingMode;
import com.tradehero.th.billing.samsung.ProcessingPurchase;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
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
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaserHolderRx(
            @NonNull Context context,
            @ForSamsungBillingMode int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super();
        this.context = context;
        this.mode = mode;
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
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
                true,
                processingPurchaseStringSet);
    }
}
