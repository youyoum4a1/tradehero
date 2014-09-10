package com.tradehero.th.api.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.billing.samsung.SamsungPurchase;

public class SamsungPurchaseReportDTO implements PurchaseReportDTO
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

    public SamsungPurchaseReportDTO()
    {
        super();
    }

    public SamsungPurchaseReportDTO(SamsungPurchase samsungPurchase)
    {
        super();
        paymentId = samsungPurchase.getPaymentId();
        purchaseId = samsungPurchase.getPurchaseId();
        productCode = samsungPurchase.getProductCode();
    }
}
