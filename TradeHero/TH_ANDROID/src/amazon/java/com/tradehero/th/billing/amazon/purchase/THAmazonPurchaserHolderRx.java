package com.ayondo.academy.billing.amazon.purchase;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchase.AmazonPurchaserHolderRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.amazon.THAmazonPurchaseOrder;
import com.ayondo.academy.billing.purchase.THPurchaserHolderRx;

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
