package com.tradehero.th.api.billing;

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
    //</editor-fold>
}
