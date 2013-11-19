package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABOrderId;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.ByteArrayOutputStream;
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
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        THJsonAdapter.getInstance().toBody(ownedPortfolioId).writeTo(byteArrayOutputStream);
        developerPayload = byteArrayOutputStream.toString("UTF-8");
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
