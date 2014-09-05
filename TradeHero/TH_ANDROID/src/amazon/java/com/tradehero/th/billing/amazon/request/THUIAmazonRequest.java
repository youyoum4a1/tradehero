package com.tradehero.th.billing.amazon.request;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.request.UIAmazonRequest;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.amazon.THAmazonPurchaseOrder;
import com.tradehero.th.billing.request.THUIBillingRequest;

public interface THUIAmazonRequest
    extends THUIBillingRequest<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            THAmazonProductDetail,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException>,
        UIAmazonRequest<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                AmazonException>
{
}
