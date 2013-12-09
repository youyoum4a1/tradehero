package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReporter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface THIABActorPurchaseReporter extends
        IABActorPurchaseReporter<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                UserProfileDTO,
                PurchaseReporter.OnPurchaseReportedListener<
                        IABSKU,
                        THIABOrderId,
                        THIABPurchase,
                        Exception>,
                Exception>
{
}
