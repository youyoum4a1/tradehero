package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABActor;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.PurchaseReportedHandler;
import com.tradehero.th.billing.PurchaseReporter;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActor extends IABActor<
                                IABSKU,
                                THIABPurchaseOrder,
                                THIABOrderId,
                                SKUPurchase,
                                THIABPurchaseHandler,
                                THIABPurchaseConsumeHandler,
                                IABException>,
        SKUDomainInformer
{
    List<THSKUDetails> getDetailsOfDomain(String domain);
    int launchReportSequence(PurchaseReportedHandler purchaseReportedHandler, SKUPurchase purchase);
    UserProfileDTO launchReportSequenceSync(SKUPurchase purchase);
}
