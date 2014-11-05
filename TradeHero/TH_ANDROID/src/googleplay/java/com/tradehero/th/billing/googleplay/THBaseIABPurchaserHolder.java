package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaserHolder
    extends BaseIABPurchaserHolder<
        IABSKU,
        THIABPurchaseOrder,
        THIABOrderId,
        THIABPurchase,
        THIABPurchaser,
        IABException>
    implements THIABPurchaserHolder
{
    @NonNull protected final Provider<THIABPurchaser> thiabPurchaserProvider;

    //<editor-fold desc="Constructors">
    @Inject THBaseIABPurchaserHolder(
            @NonNull Provider<THIABPurchaser> thiabPurchaserProvider)
    {
        super();
        this.thiabPurchaserProvider = thiabPurchaserProvider;
    }
    //</editor-fold>

    @Override @NonNull protected THIABPurchaser createPurchaser()
    {
        return thiabPurchaserProvider.get();
    }
}
