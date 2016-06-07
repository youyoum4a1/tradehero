package com.androidth.general.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.common.billing.googleplay.IABPurchaseOrder;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.utils.THJsonAdapter;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.billing.THPurchaseOrder;
import com.androidth.general.billing.googleplay.exception.IABInvalidQuantityException;
import com.androidth.general.billing.googleplay.exception.IABMissingApplicablePortfolioIdException;
import java.io.IOException;
import timber.log.Timber;

public class THIABPurchaseOrder implements IABPurchaseOrder<IABSKU>, THPurchaseOrder<IABSKU>
{
    @NonNull private final IABSKU sku;
    private final int quantity;
    @NonNull private final OwnedPortfolioId developerPayload;
    @NonNull private final String type;
    @Nullable private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THIABPurchaseOrder(@NonNull IABSKU sku, @NonNull String type, @NonNull OwnedPortfolioId developerPayload)
    {
        this(sku, type, developerPayload, null);
    }

    public THIABPurchaseOrder(@NonNull IABSKU sku, @NonNull String type, @NonNull OwnedPortfolioId developerPayload,
            @Nullable UserBaseKey heroId)
    {
        this(sku, 1, type, developerPayload);
        this.userToFollow = heroId;
        Timber.d("THIABPurchaseOrder with %s", developerPayload);
    }

    public THIABPurchaseOrder(@NonNull IABSKU sku, int quantity, @NonNull String type, @NonNull OwnedPortfolioId developerPayload)
    {
        this(sku, quantity, type, developerPayload, null);
    }

    public THIABPurchaseOrder(@NonNull IABSKU sku, int quantity, @NonNull String type, @NonNull OwnedPortfolioId developerPayload,
            @Nullable UserBaseKey heroId)
    {
        this.sku = sku;
        this.quantity = quantity;
        this.type = type;
        this.userToFollow = heroId;
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
        } catch (IOException e)
        {
            Timber.e("Failed to stringify developerPayload", e);
            throw new IABMissingApplicablePortfolioIdException("DeveloperPayload is invalid " + developerPayload);
        }
    }

    @NonNull @Override public OwnedPortfolioId getApplicablePortfolioId()
    {
        return developerPayload;
    }

    @Override @NonNull public String getType()
    {
        return type;
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
