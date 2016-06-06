package com.androidth.general.billing.googleplay;

import com.androidth.general.common.billing.googleplay.IABLogicHolderRx;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.billing.THBillingLogicHolderRx;

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
