package com.tradehero.common.billing.samsung;

/**
 * Created by xavier on 2014/4/2.
 */
public class SamsungPurchaseDTO
{
    public String itemId;
    public String paymentId;
    public String productCode;

    public SamsungPurchaseDTO()
    {
        super();
    }

    public SamsungPurchaseDTO(SamsungPurchase samsungPurchase)
    {
        super();
        itemId = samsungPurchase.getItemId();
        paymentId = samsungPurchase.getPaymentId();
        productCode = samsungPurchase.getProductCode();
    }
}
