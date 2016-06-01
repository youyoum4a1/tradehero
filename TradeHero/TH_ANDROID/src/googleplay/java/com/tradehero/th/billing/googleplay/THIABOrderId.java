package com.ayondo.academy.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.utils.THJsonAdapter;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.billing.THOrderId;
import java.io.IOException;

public class THIABOrderId
        extends IABOrderId
        implements THOrderId
{
    //<editor-fold desc="Constructors">
    public THIABOrderId(@NonNull String orderId)
    {
        super(orderId);
    }
    //</editor-fold>

    public void setDeveloperPayload(@NonNull OwnedPortfolioId ownedPortfolioId) throws IOException
    {
        developerPayload = THJsonAdapter.getInstance().toStringBody(ownedPortfolioId);
    }

    @Nullable public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        if (developerPayload != null)
        {
            return (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        return null;
    }
}
