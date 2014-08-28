package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.THOrderId;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class THIABOrderId
        extends IABOrderId
        implements THOrderId
{
    //<editor-fold desc="Constructors">
    public THIABOrderId(@NotNull String orderId)
    {
        super(orderId);
    }
    //</editor-fold>

    public void setDeveloperPayload(@NotNull OwnedPortfolioId ownedPortfolioId) throws IOException
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
