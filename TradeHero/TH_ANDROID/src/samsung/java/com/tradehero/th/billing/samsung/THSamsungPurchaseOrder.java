package com.tradehero.th.billing.samsung;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.samsung.SamsungPurchaseOrder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.samsung.exception.SamsungInvalidQuantityException;

public class THSamsungPurchaseOrder
    implements SamsungPurchaseOrder<SamsungSKU>,
        THPurchaseOrder<SamsungSKU>
{
    @NonNull protected final SamsungSKU productIdentifier;
    protected final int quantity;
    @NonNull OwnedPortfolioId applicablePortfolioId;
    @Nullable protected UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THSamsungPurchaseOrder(
            @NonNull String groupId,
            @NonNull String itemId,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this(groupId, itemId, 1, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NonNull String groupId,
            @NonNull String itemId,
            int quantity,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this(new SamsungSKU(groupId, itemId), quantity, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NonNull SamsungSKU productIdentifier,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this(productIdentifier, 1, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            @NonNull SamsungSKU productIdentifier,
            int quantity,
            @NonNull OwnedPortfolioId applicablePortfolioId)
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

    @Override @NonNull public SamsungSKU getProductIdentifier()
    {
        return productIdentifier;
    }

    @Override public int getQuantity()
    {
        return quantity;
    }

    @NonNull @Override public OwnedPortfolioId getApplicablePortfolioId()
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
