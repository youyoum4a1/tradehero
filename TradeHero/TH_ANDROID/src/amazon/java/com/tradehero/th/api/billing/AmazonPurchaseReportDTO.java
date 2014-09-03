package com.tradehero.th.api.billing;

import com.tradehero.th.billing.amazon.THBaseAmazonPurchase;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseReportDTO implements PurchaseReportDTO
{
    // TODO to decide
    @NotNull public String amazonPurchaseToken;
    @NotNull public String amazonUserId;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseReportDTO()
    {
        super();
    }

    public AmazonPurchaseReportDTO(
            @NotNull String amazonPurchaseToken,
            @NotNull String amazonUserId)
    {
        this.amazonPurchaseToken = amazonPurchaseToken;
        this.amazonUserId = amazonUserId;
    }

    public AmazonPurchaseReportDTO(@NotNull THBaseAmazonPurchase amazonPurchase)
    {
        this.amazonPurchaseToken = amazonPurchase.getOrderId().receipt.getReceiptId();
        this.amazonUserId = amazonPurchase.getAmazonUserId();
    }

    public AmazonPurchaseReportDTO(@NotNull AmazonPurchaseReportDTO other)
    {
        this.amazonPurchaseToken = other.amazonPurchaseToken;
        this.amazonUserId = other.amazonUserId;
    }
    //</editor-fold>
}
