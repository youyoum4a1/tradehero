package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.Receipt;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THComposedAmazonPurchase implements THAmazonPurchase
{
    @NotNull public final Receipt receipt;
    @NotNull private final AmazonPurchaseInProcessDTO purchaseInProcessDTO;

    //<editor-fold desc="Constructors">
    public THComposedAmazonPurchase(
            @NotNull Receipt receipt,
            @NotNull AmazonPurchaseInProcessDTO purchaseInProcessDTO)
    {
        this.receipt = receipt;
        this.purchaseInProcessDTO = purchaseInProcessDTO;
    }
    //</editor-fold>

    @NotNull @Override public THAmazonOrderId getOrderId()
    {
        return new THAmazonOrderId(receipt);
    }

    @NotNull @Override public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(receipt.getSku());
    }

    @NotNull @Override public AmazonPurchaseInProcessDTO getPurchaseToSaveDTO()
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

    @NotNull @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return purchaseInProcessDTO.applicablePortfolioId;
    }

    @Override public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.purchaseInProcessDTO.applicablePortfolioId = applicablePortfolioId;
    }

    @NotNull @Override public AmazonPurchaseReportDTO getPurchaseReportDTO()
    {
        return new AmazonPurchaseReportDTO(purchaseInProcessDTO);
    }

    @NotNull @Override public String getAmazonUserId()
    {
        return purchaseInProcessDTO.amazonUserId;
    }

    @Override public boolean shouldConsume()
    {
        return receipt.getProductType().equals(ProductType.CONSUMABLE);
    }
}
