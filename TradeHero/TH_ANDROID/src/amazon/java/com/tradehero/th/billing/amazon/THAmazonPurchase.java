package com.tradehero.th.billing.amazon;

import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THProductPurchase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THAmazonPurchase extends AmazonPurchase<AmazonSKU, THAmazonOrderId>
    implements THProductPurchase<AmazonSKU, THAmazonOrderId>
{
    @NotNull private OwnedPortfolioId applicablePortfolioId;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    protected THAmazonPurchase(@NotNull PurchaseResponse purchaseResponse)
    {
        super(purchaseResponse);
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

    public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NotNull @Override public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        return applicablePortfolioId;
    }

    @NotNull @Override public AmazonPurchaseReportDTO getPurchaseReportDTO()
    {
        return new AmazonPurchaseReportDTO(purchaseResponse.getReceipt().getReceiptId());
    }
}
