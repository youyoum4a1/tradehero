package com.androidth.general.billing.samsung;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.samsung.SamsungPurchaseOrder;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.billing.THPurchaseOrder;
import com.androidth.general.billing.samsung.exception.SamsungInvalidQuantityException;

public class THSamsungPurchaseOrder
    implements SamsungPurchaseOrder<SamsungSKU>,
        THPurchaseOrder<SamsungSKU>
{
    @NonNull protected final SamsungSKU productIdentifier;
    protected final int quantity;
    @NonNull OwnedPortfolioId applicablePortfolioId;
    @Nullable protected UserBaseKey userToFollow;

    public THSamsungPurchaseOrder(
            SamsungSKU productIdentifier,
            OwnedPortfolioId applicablePortfolioId)
    {
        this(productIdentifier, 1, applicablePortfolioId);
    }

    public THSamsungPurchaseOrder(
            SamsungSKU productIdentifier,
            int quantity,
            OwnedPortfolioId applicablePortfolioId)
    {
        this(productIdentifier, quantity, applicablePortfolioId, null);
    }

    public THSamsungPurchaseOrder(
            SamsungSKU productIdentifier,
            int quantity,
            OwnedPortfolioId applicablePortfolioId,
            UserBaseKey userToFollow)
    {
        this.productIdentifier = productIdentifier;
        this.quantity = quantity;
        this.applicablePortfolioId = applicablePortfolioId;
        this.userToFollow = userToFollow;
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
