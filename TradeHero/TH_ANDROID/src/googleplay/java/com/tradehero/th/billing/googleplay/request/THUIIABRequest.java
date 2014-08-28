package com.tradehero.th.billing.googleplay.request;

import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.request.UIIABBillingRequest;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABPurchaseOrder;
import com.tradehero.th.billing.request.THUIBillingRequest;
import org.jetbrains.annotations.Nullable;

public interface THUIIABRequest
    extends THUIBillingRequest<
                IABSKUListKey,
                IABSKU,
                IABSKUList,
                THIABProductDetail,
                THIABPurchaseOrder,
                THIABOrderId,
                THIABPurchase,
                IABException>,
        UIIABBillingRequest
{
    //<editor-fold desc="Consuming Purchase">
    boolean getConsumePurchase();
    void setConsumePurchase(boolean consumePurchase);
    boolean getPopIfConsumeFailed();
    @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            IABException> getConsumptionFinishedListener();
    void setConsumptionFinishedListener(
            @Nullable IABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase,
                    IABException> consumptionFinishedListener);
    //</editor-fold>
}
