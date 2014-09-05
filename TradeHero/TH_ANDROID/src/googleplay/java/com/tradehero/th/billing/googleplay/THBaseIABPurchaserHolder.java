package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchaserHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

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
    @NotNull protected final Provider<THIABPurchaser> thiabPurchaserProvider;

    //<editor-fold desc="Constructors">
    @Inject THBaseIABPurchaserHolder(
            @NotNull Provider<THIABPurchaser> thiabPurchaserProvider)
    {
        super();
        this.thiabPurchaserProvider = thiabPurchaserProvider;
    }
    //</editor-fold>

    @Override @NotNull protected THIABPurchaser createPurchaser()
    {
        return thiabPurchaserProvider.get();
    }
}
