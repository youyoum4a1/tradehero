package com.ayondo.academy.billing.amazon.purchase;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchase.AmazonPurchaserRx;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.amazon.THAmazonPurchaseOrder;
import com.ayondo.academy.billing.purchase.THPurchaserRx;

public interface THAmazonPurchaserRx
        extends
        AmazonPurchaserRx<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase>,
        THPurchaserRx<
                AmazonSKU,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase>
{
}
