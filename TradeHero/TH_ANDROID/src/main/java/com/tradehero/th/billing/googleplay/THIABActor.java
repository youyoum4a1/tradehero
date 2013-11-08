package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABActor;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActor extends IABActor<IABSKU, THSKUDetails, IABOrderId, IABException, SKUPurchase, THIABPurchaseHandler>
{
    List<THSKUDetails> getDetailsOfDomain(String domain);
    int launchPurchaseSequence(THIABPurchaseHandler purchaseHandler, THSKUDetails skuDetails, String extraData);
}
