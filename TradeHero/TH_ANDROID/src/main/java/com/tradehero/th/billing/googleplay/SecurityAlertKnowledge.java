package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/10/14.
 */
@Singleton public class SecurityAlertKnowledge
{
    public static final String TAG = SecurityAlertKnowledge.class.getSimpleName();

    @Inject public SecurityAlertKnowledge()
    {
    }

    public int getStockAlertIcon(int count)
    {
        if (count == 0)
        {
            return 0;
        }
        else if (count <= 2)
        {
            return R.drawable.buy_alerts_2;
        }
        else if (count <= 5)
        {
            return R.drawable.buy_alerts_5;
        }
        else if (count >= Constants.ALERT_PLAN_UNLIMITED)
        {
            return R.drawable.buy_alerts_infinite;
        }
        else
        {
            throw new IllegalArgumentException("Unexpected count " + count);
        }
    }

    public IABSKU getServerEquivalentSKU(IABSKU localSKU)
    {
        if (localSKU == null)
        {
            return null;
        }

        switch (localSKU.identifier)
        {
            case THIABSKUFetcher.SERVER_ALERT_1:
                return new IABSKU(THIABSKUFetcher.ALERT_1);

            case THIABSKUFetcher.SERVER_ALERT_5:
                return new IABSKU(THIABSKUFetcher.ALERT_5);

            case THIABSKUFetcher.SERVER_ALERT_UNLIMITED:
                return new IABSKU(THIABSKUFetcher.ALERT_UNLIMITED);

        }

        return null;
    }
}
