package com.tradehero.th.billing.googleplay;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.THPurchaseOrder;
import com.tradehero.th.billing.googleplay.exception.MissingApplicablePortfolioIdException;
import java.io.IOException;
import timber.log.Timber;

public class THIABPurchaseOrder implements IABPurchaseOrder<IABSKU>, THPurchaseOrder<IABSKU>
{
    private IABSKU sku;
    private int quantity;
    private OwnedPortfolioId developerPayload;
    private UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public THIABPurchaseOrder (IABSKU sku, OwnedPortfolioId developerPayload) throws MissingApplicablePortfolioIdException
    {
        this(sku, 1, developerPayload);
        Timber.d("THIABPurchaseOrder with %s", developerPayload);
    }

    public THIABPurchaseOrder (IABSKU sku, int quantity, OwnedPortfolioId developerPayload) throws MissingApplicablePortfolioIdException
    {
        this.sku = sku;
        this.quantity = quantity;
        this.developerPayload = developerPayload;
        testOwnedPortfolioIdValid(developerPayload);
    }
    //</editor-fold>

    public void testOwnedPortfolioIdValid(OwnedPortfolioId developerPayload) throws MissingApplicablePortfolioIdException
    {
        if (developerPayload == null || !developerPayload.isValid())
        {
            throw new MissingApplicablePortfolioIdException("DeveloperPayload is invalid " + developerPayload);
        }
    }

    @Override public IABSKU getProductIdentifier()
    {
        return this.sku;
    }

    @Override public int getQuantity()
    {
        return this.quantity;
    }

    @Override public String getDeveloperPayload()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this.developerPayload);
        }
        catch (IOException e)
        {
            Timber.e("Failed to stringify developerPayload", e);
        }
        return "";
    }

    @Override public void setDeveloperPayload(String developerPayload)
    {
        if (developerPayload != null)
        {
            this.developerPayload = (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        else
        {
            this.developerPayload = null;
        }
    }

    @JsonIgnore
    @Override public void setUserToFollow(UserBaseKey userToFollow)
    {
        this.userToFollow = userToFollow;
    }

    @JsonIgnore
    @Override public UserBaseKey getUserToFollow()
    {
        return userToFollow;
    }

    @Override public String toString()
    {
        return new StringBuilder().append("THIABPurchaseOrder{sku:")
                .append(sku).append(", ")
                .append("quantity:").append(quantity).append(", ")
                .append("developerPayload:").append(developerPayload).append(", ")
                .append("}")
                .toString();
    }
}
