package com.tradehero.common.billing.samsung;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xavier on 2014/4/2.
 */
public class SamsungPurchaseDTO
{
    public static final String PAYMENT_ID_JSON_KEY = "samsung_payment_id";
    public static final String PURCHASE_ID_JSON_KEY = "samsung_purchase_id";
    public static final String PRODUCT_CODE_JSON_KEY = "samsung_product_code";

    @JsonProperty(PAYMENT_ID_JSON_KEY)
    public String paymentId;
    @JsonProperty(PURCHASE_ID_JSON_KEY)
    public String purchaseId;
    @JsonProperty(PRODUCT_CODE_JSON_KEY)
    public String productCode;

    public SamsungPurchaseDTO()
    {
        super();
    }

    public SamsungPurchaseDTO(SamsungPurchase samsungPurchase)
    {
        super();
        paymentId = samsungPurchase.getPaymentId();
        purchaseId = samsungPurchase.getPurchaseId();
        productCode = samsungPurchase.getProductCode();
    }
}
