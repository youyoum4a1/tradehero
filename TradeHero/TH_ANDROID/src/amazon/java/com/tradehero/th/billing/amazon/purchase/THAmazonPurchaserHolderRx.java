package com.tradehero.th.billing.amazon.purchase;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchase.AmazonPurchaserHolderRx;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.amazon.THAmazonPurchaseOrder;
import com.tradehero.th.billing.purchase.THPurchaserHolderRx;

public interface THAmazonPurchaserHolderRx
        extends
        AmazonPurchaserHolderRx<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase>,
        THPurchaserHolderRx<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase>
{
}
