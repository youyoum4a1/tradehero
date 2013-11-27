package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ActorPurchaseReporter;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 3:47 PM To change this template use File | Settings | File Templates. */
public interface IABActorPurchaseReporter<
        IABSKUType extends IABSKU,
        IABOrderIdType extends IABOrderId,
        IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>,
        UserProfileDTOType extends UserProfileDTO>
    extends ActorPurchaseReporter<IABSKUType, IABOrderIdType, IABPurchaseType, UserProfileDTOType>
{
}
