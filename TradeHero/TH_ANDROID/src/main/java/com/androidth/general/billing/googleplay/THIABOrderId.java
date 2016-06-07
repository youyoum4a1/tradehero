package com.androidth.general.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.utils.THJsonAdapter;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.billing.THOrderId;
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