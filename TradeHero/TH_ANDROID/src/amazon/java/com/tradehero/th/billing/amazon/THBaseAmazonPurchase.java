package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.BaseAmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THBaseAmazonPurchase
        extends BaseAmazonPurchase<AmazonSKU, THAmazonOrderId>
        implements THAmazonPurchase
{
    @NotNull private OwnedPortfolioId applicablePortfolioId;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    protected THBaseAmazonPurchase(
            @NotNull PurchaseResponse purchaseResponse,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        super(purchaseResponse);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @NotNull @Override public THAmazonOrderId getOrderId()
    {
        return new THAmazonOrderId(purchaseResponse.getReceipt());
    }

    @NotNull @Override public AmazonSKU getProductIdentifier()
    {
        return new AmazonSKU(purchaseResponse.getReceipt().getSku());
    }

    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Nullable @Override public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NotNull @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return applicablePortfolioId;
    }

    @NotNull @Override public AmazonPurchaseReportDTO getPurchaseReportDTO()
    {
        return new AmazonPurchaseReportDTO(purchaseResponse.getReceipt().getReceiptId(), purchaseResponse.getUserData().getUserId());
    }

    @NotNull public AmazonPurchaseInProcessDTO getPurchaseToSaveDTO()
    {
        return new AmazonPurchaseInProcessDTO(this);
    }

    public void populate(@NotNull AmazonPurchaseInProcessDTO purchaseInProcessDTO)
    {
        if (!purchaseResponse.getReceipt().getReceiptId().equals(purchaseInProcessDTO.amazonPurchaseToken))
        {
            throw new IllegalArgumentException(String.format("Non-matching paymentId %s - %s", purchaseResponse.getReceipt().getReceiptId(), purchaseInProcessDTO.amazonPurchaseToken));
        }
        setApplicablePortfolioId(purchaseInProcessDTO.applicablePortfolioId);
        setUserToFollow(purchaseInProcessDTO.userToFollow);
    }

    @Override public boolean shouldConsume()
    {
        return purchaseResponse.getReceipt().getProductType().equals(ProductType.CONSUMABLE);
    }
}
