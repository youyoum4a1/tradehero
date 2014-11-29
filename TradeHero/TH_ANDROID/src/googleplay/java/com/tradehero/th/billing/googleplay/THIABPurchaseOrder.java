package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.googleplay.exception.IABInvalidQuantityException;
import com.tradehero.th.billing.googleplay.exception.IABMissingApplicablePortfolioIdException;
import java.io.IOException;
import timber.log.Timber;

public class THIABPurchaseOrder implements IABPurchaseOrder<IABSKU>, THPurchaseOrder<IABSKU>
{
    @NonNull private IABSKU sku;
    private int quantity;
    @NonNull private OwnedPortfolioId developerPayload;
    @NonNull private String type;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THIABPurchaseOrder (@NonNull IABSKU sku, @NonNull OwnedPortfolioId developerPayload)
    {
        this(sku, 1, developerPayload);
        Timber.d("THIABPurchaseOrder with %s", developerPayload);
    }

    public THIABPurchaseOrder (@NonNull IABSKU sku, int quantity, @NonNull OwnedPortfolioId developerPayload)
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

    public void testOwnedPortfolioIdValid(@NonNull OwnedPortfolioId developerPayload)
    {
        if (!developerPayload.isValid())
        {
            throw new IABMissingApplicablePortfolioIdException("DeveloperPayload is invalid " + developerPayload);
        }
    }

    @Override @NonNull public IABSKU getProductIdentifier()
    {
        return this.sku;
    }

    @Override public int getQuantity()
    {
        return this.quantity;
    }

    @Override @NonNull public String getDeveloperPayload()
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

    @Override public void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId)
    {
        this.developerPayload = applicablePortfolioId;
    }

    @NonNull @Override public OwnedPortfolioId getApplicablePortfolioId()
    {
        return developerPayload;
    }

    @Override @NonNull public String getType()
    {
        return type;
    }

    public void setType(@NonNull String type)
    {
        this.type = type;
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
