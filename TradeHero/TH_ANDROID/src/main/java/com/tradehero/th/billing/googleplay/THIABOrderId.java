package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.IOException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 11:29 AM To change this template use File | Settings | File Templates. */
public class THIABOrderId extends IABOrderId
{
    public static final String TAG = THIABOrderId.class.getSimpleName();

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
