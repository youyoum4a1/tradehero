package com.tradehero.th.billing.googleplay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABDeveloperErrorException;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.googleplay.exception.IABInvalidQuantityException;
import com.tradehero.th.billing.googleplay.exception.IABMissingApplicablePortfolioIdException;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class THIABPurchaseOrder implements IABPurchaseOrder<IABSKU>, THPurchaseOrder<IABSKU>
{
    @NotNull private IABSKU sku;
    private int quantity;
    @NotNull private OwnedPortfolioId developerPayload;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THIABPurchaseOrder (@NotNull IABSKU sku, @NotNull OwnedPortfolioId developerPayload)
    {
        this(sku, 1, developerPayload);
        Timber.d("THIABPurchaseOrder with %s", developerPayload);
    }

    public THIABPurchaseOrder (@NotNull IABSKU sku, int quantity, @NotNull OwnedPortfolioId developerPayload)
    {
        this.sku = sku;
        this.quantity = quantity;
        if (quantity <= 0)
        {
            throw new IABInvalidQuantityException("Quantity " + quantity + " is invalid");
        }

        this.developerPayload = developerPayload;
        testOwnedPortfolioIdValid(developerPayload);
    }
    //</editor-fold>

    public void testOwnedPortfolioIdValid(@NotNull OwnedPortfolioId developerPayload)
    {
        if (!developerPayload.isValid())
        {
            throw new IABMissingApplicablePortfolioIdException("DeveloperPayload is invalid " + developerPayload);
        }
    }

    @Override @NotNull public IABSKU getProductIdentifier()
    {
        return this.sku;
    }

    @Override public int getQuantity()
    {
        return this.quantity;
    }

    @Override @NotNull public String getDeveloperPayload()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this.developerPayload);
        }
        catch (IOException e)
        {
            Timber.e("Failed to stringify developerPayload", e);
            throw new IABMissingApplicablePortfolioIdException("DeveloperPayload is invalid " + developerPayload);
        }
    }

    @Override public void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId)
    {
        this.developerPayload = applicablePortfolioId;
    }

    @NotNull @Override public OwnedPortfolioId getApplicablePortfolioId()
    {
        return developerPayload;
    }

    @JsonIgnore
    @Override public void setUserToFollow(@Nullable UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @JsonIgnore
    @Override @Nullable public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public String toString()
    {
        return "THIABPurchaseOrder{" +
                "sku=" + sku +
                ", quantity=" + quantity +
                ", developerPayload=" + developerPayload +
                ", userToFollow=" + userToFollow +
                '}';
    }
}
