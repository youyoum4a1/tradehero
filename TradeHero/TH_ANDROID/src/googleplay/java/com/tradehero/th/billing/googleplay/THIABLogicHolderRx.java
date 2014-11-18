package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABLogicHolderRx;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.th.billing.THBillingLogicHolderRx;

public interface THIABLogicHolderRx extends
        IABLogicHolderRx<
                        IABSKUListKey,
                        IABSKU,
                        IABSKUList,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase>,
        THBillingLogicHolderRx<
                        IABSKUListKey,
                        IABSKU,
                        IABSKUList,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase>,
        THIABProductDetailDomainInformerRx
{

}
