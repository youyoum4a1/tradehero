package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.THOrderId;
import java.io.IOException;

public class THIABOrderId
        extends IABOrderId
        implements THOrderId
{
    public THIABOrderId(String orderId)
    {
        super(orderId);
    }

    public void setDeveloperPayload(OwnedPortfolioId ownedPortfolioId) throws IOException
    {
        developerPayload = THJsonAdapter.getInstance().toStringBody(ownedPortfolioId);
    }

    public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        if (developerPayload != null)
        {
            return (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        return null;
    }
}
