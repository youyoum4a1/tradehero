package com.ayondo.academy.api.billing;

import android.support.annotation.NonNull;
import com.ayondo.academy.billing.amazon.THBaseAmazonPurchase;

public class AmazonPurchaseReportDTO implements PurchaseReportDTO
{
    // TODO to decide
    @NonNull public String amazonSku;
    @NonNull public String amazonPurchaseToken;
    @NonNull public String amazonUserId;

    //<editor-fold desc="Constructors">
    protected AmazonPurchaseReportDTO()
    {
        super();
    }

    public AmazonPurchaseReportDTO(
            @NonNull String amazonSku,
            @NonNull String amazonPurchaseToken,
            @NonNull String amazonUserId)
    {
        this.amazonSku = amazonSku;
        this.amazonPurchaseToken = amazonPurchaseToken;
        this.amazonUserId = amazonUserId;
    }

    public AmazonPurchaseReportDTO(@NonNull THBaseAmazonPurchase amazonPurchase)
    {
        this.amazonSku = amazonPurchase.getProductIdentifier().skuId;
        this.amazonPurchaseToken = amazonPurchase.getOrderId().receipt.getReceiptId();
        this.amazonUserId = amazonPurchase.getAmazonUserId();
    }

    public AmazonPurchaseReportDTO(@NonNull AmazonPurchaseReportDTO other)
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
