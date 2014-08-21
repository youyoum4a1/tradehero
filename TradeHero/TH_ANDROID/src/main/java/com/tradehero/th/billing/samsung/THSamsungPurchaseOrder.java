package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.samsung.exception.SamsungInvalidQuantityException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THSamsungPurchaseOrder
    implements SamsungPurchaseOrder<SamsungSKU>,
        THPurchaseOrder<SamsungSKU>
{
    @NotNull protected final SamsungSKU productIdentifier;
    protected final int quantity;
    @NotNull OwnedPortfolioId applicablePortfolioId;
    @Nullable protected UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THSamsungPurchaseOrder(
            @NotNull String groupId,
            @NotNull String itemId,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this(groupId, itemId, 1, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NotNull String groupId,
            @NotNull String itemId,
            int quantity,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this(new SamsungSKU(groupId, itemId), quantity, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NotNull SamsungSKU productIdentifier,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this(productIdentifier, 1, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NotNull SamsungSKU productIdentifier,
            int quantity,
            @NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.productIdentifier = productIdentifier;
        this.quantity = quantity;
        this.applicablePortfolioId = applicablePortfolioId;
        if (quantity <= 0)
        {
            throw new SamsungInvalidQuantityException("Quantity " + quantity + " is invalid");
        }
    }
    //</editor-fold>

    @Override @NotNull public SamsungSKU getProductIdentifier()
    {
        return productIdentifier;
    }

    @Override public int getQuantity()
    {
        return quantity;
    }

    @Override public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    @NotNull @Override public OwnedPortfolioId getApplicablePortfolioId()
    {
        return applicablePortfolioId;
    }

    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @Override @Nullable public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }
}
