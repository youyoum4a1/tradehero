package com.ayondo.academy.api.billing;

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

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    SamsungPurchaseReportDTO()
    {
        // Necessary for Json deserialisation
        super();
    }

    public SamsungPurchaseReportDTO(String paymentId, String purchaseId, String productCode)
    {
        super();
        this.paymentId = paymentId;
        this.purchaseId = purchaseId;
        this.productCode = productCode;
    }

    public SamsungPurchaseReportDTO(SamsungPurchase samsungPurchase)
    {
        super();
        paymentId = samsungPurchase.getPaymentId();
        purchaseId = samsungPurchase.getPurchaseId();
        productCode = samsungPurchase.getItemId();
    }
    //</editor-fold>
}
