package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.googleplay.IABLogicHolder;
import com.tradehero.common.billing.googleplay.IABPurchaseConsumer;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABLogicHolder extends
        THIABProductDetailDomainInformer,
        IABLogicHolder<
                        IABSKU,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>
{
    @Deprecated
    THIABInventoryFetcherHolder getInventoryFetcherHolder();
    @Deprecated
    THIABPurchaseFetcherHolder getPurchaseFetcherHolder();
    @Deprecated
    THIABPurchaserHolder getPurchaserHolder();
    @Deprecated
    THIABPurchaseConsumerHolder getPurchaseConsumerHolder();
    @Deprecated
    THIABPurchaseReporterHolder getPurchaseReporterHolder();
}
