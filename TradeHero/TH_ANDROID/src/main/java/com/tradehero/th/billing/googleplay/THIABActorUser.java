package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABActorUser;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorUser extends IABActorUser<
                                            IABSKU,
                                            THSKUDetails,
                                            IABOrderId,
                                            SKUPurchase,
                                            THIABPurchaseHandler,
                                            THIABActor,
                                            IABException>
{
}
