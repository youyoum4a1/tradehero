package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.api.billing.AmazonPurchaseInProcessDTO;
import com.ayondo.academy.api.billing.AmazonPurchaseReportDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.UserBaseKey;

public class THComposedAmazonPurchase implements THAmazonPurchase
{
    @NonNull public final Receipt receipt;
    @NonNull private final AmazonPurchaseInProcessDTO purchaseInProcessDTO;

    //<editor-fold desc="Constructors">
    public THComposedAmazonPurchase(
            @NonNull Receipt receipt,
            @NonNull AmazonPurchaseInProcessDTO purchaseInProcessDTO)
    {
        this.receipt = receipt;
        this.purchaseInProcessDTO = purchaseInProcessDTO;
    }
    //</editor-fold>

    @NonNull @Override public THAmazonOrderId getOrderId()
    {
        return new THAmazonOrderId(receipt);
    }

    @NonNull @Override public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(receipt.getSku());
    }

    @NonNull @Override public AmazonPurchaseInProcessDTO getPurchaseToSaveDTO()
    {
        return purchaseInProcessDTO;
    }

    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.purchaseInProcessDTO.userToFollow = userToFollow;
    }

    @Nullable @Override public UserBaseKey getUserToFollow()
    {
        return purchaseInProcessDTO.userToFollow;
    }

    @NonNull @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return purchaseInProcessDTO.applicablePortfolioId;
    }

    @Override public void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this.purchaseInProcessDTO.applicablePortfolioId = applicablePortfolioId;
    }

    @NonNull @Override public AmazonPurchaseReportDTO getPurchaseReportDTO()
    {
        return new AmazonPurchaseReportDTO(purchaseInProcessDTO);
    }

    @NonNull @Override public String getAmazonUserId()
    {
        return purchaseInProcessDTO.amazonUserId;
    }

    @Override public boolean shouldConsume()
    {
        return receipt.getProductType().equals(ProductType.CONSUMABLE);
    }

    @Override public boolean isCancelled()
    {
        return receipt.isCanceled();
    }
}
