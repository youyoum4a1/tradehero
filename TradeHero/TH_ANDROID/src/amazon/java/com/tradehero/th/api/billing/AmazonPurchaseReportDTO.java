package com.tradehero.th.api.billing;

import com.tradehero.th.billing.amazon.THBaseAmazonPurchase;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseReportDTO implements PurchaseReportDTO
{
    // TODO to decide
    @NotNull public String amazonReceiptId;

    //<editor-fold desc="Constructors">
    public AmazonPurchaseReportDTO(@NotNull String amazonReceiptId)
    {
        this.amazonReceiptId = amazonReceiptId;
    }

    public AmazonPurchaseReportDTO(@NotNull THBaseAmazonPurchase amazonPurchase)
    {
        this.amazonReceiptId = amazonPurchase.getOrderId().receipt.getReceiptId();
    }
    //</editor-fold>
}
