package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import javax.inject.Inject;

public class THBaseSamsungPurchaserHolder
        extends BaseSamsungPurchaserHolder<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaser,
        SamsungException>
        implements THSamsungPurchaserHolder
{
    @NonNull protected final Context context;
    protected final int mode;
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaserHolder(
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

    @NonNull @Override protected THSamsungPurchaser createPurchaser(int requestCode)
    {
        return new THBaseSamsungPurchaser(requestCode,
                context,
                mode,
                samsungExceptionFactory,
                processingPurchaseStringSet);
    }
}
