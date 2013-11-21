package com.tradehero.th.billing;

import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.th.api.users.UserProfileDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/18/13 Time: 5:55 PM To change this template use File | Settings | File Templates. */
public interface PurchaseReportedHandler
{
    void handlePurchaseReported(int requestCode, SKUPurchase purchase, UserProfileDTO userProfileDTO);
    void handlePurchaseReportFailed(int requestCode, SKUPurchase purchase, Throwable exception);
}
