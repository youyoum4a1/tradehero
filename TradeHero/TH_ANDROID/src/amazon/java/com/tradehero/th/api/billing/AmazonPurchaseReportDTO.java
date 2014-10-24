package com.tradehero.th.api.billing;

import com.tradehero.th.billing.amazon.THBaseAmazonPurchase;
import org.jetbrains.annotations.NotNull;

public class AmazonPurchaseReportDTO implements PurchaseReportDTO
{
    // TODO to decide
    @NotNull public String amazonSku;
    @NotNull public String amazonPurchaseToken;
    @NotNull public String amazonUserId;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseReportDTO()
    {
        super();
    }

    public AmazonPurchaseReportDTO(
            @NotNull String amazonSku,
            @NotNull String amazonPurchaseToken,
            @NotNull String amazonUserId)
    {
        this.amazonSku = amazonSku;
        this.amazonPurchaseToken = amazonPurchaseToken;
        this.amazonUserId = amazonUserId;
    }

    public AmazonPurchaseReportDTO(@NotNull THBaseAmazonPurchase amazonPurchase)
    {
        this.amazonSku = amazonPurchase.getProductIdentifier().skuId;
        this.amazonPurchaseToken = amazonPurchase.getOrderId().receipt.getReceiptId();
        this.amazonUserId = amazonPurchase.getAmazonUserId();
    }

    public AmazonPurchaseReportDTO(@NotNull AmazonPurchaseReportDTO other)
    {
        this.amazonSku = other.amazonSku;
        this.amazonPurchaseToken = other.amazonPurchaseToken;
        this.amazonUserId = other.amazonUserId;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "AmazonPurchaseReportDTO{" +
                "amazonSku='" + amazonSku + '\'' +
                ", amazonPurchaseToken='" + amazonPurchaseToken + '\'' +
                ", amazonUserId='" + amazonUserId + '\'' +
                '}';
    }
}
