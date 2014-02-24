package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface THIABPurchaseReporterHolder extends
        IABPurchaseReporterHolder<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                UserProfileDTO,
                PurchaseReporter.OnPurchaseReportedListener<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        IABException>,
                IABException>
{
}
