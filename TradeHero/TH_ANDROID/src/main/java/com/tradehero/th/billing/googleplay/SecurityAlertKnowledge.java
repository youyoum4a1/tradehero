package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.R;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityAlertKnowledge
{
    @Inject public SecurityAlertKnowledge()
    {
    }

    public int getStockAlertIcon(int count)
    {
        if (count == 0)
        {
            return R.drawable.default_image;
        }
        else if (count <= 2)
        {
            return R.drawable.default_image;
        }
        else if (count <= 5)
        {
            return R.drawable.default_image;
        }
        else if (count <= 7)
        {
            return R.drawable.default_image;
        }
        else if (count >= IABConstants.ALERT_PLAN_UNLIMITED)
        {
            return R.drawable.default_image;
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
            case THIABConstants.SERVER_ALERT_1:
                return new IABSKU(THIABConstants.ALERT_1);

            case THIABConstants.SERVER_ALERT_5:
                return new IABSKU(THIABConstants.ALERT_5);

            case THIABConstants.SERVER_ALERT_UNLIMITED:
                return new IABSKU(THIABConstants.ALERT_UNLIMITED);
        }

        return null;
    }
}