package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingConstants;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertPlanDTO;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class SecurityAlertKnowledge
{
    //<editor-fold desc="Constructors">
    public SecurityAlertKnowledge()
    {
    }
    //</editor-fold>

    public int getStockAlertIcon(int count)
    {
        if (count == 0)
        {
            return R.drawable.default_image;
        }
        else if (count <= 2)
        {
            return R.drawable.buy_alerts_2;
        }
        else if (count <= 5)
        {
            return R.drawable.buy_alerts_5;
        }
        else if (count <= 7)
        {
            return R.drawable.alerts_7;
        }
        else if (count >= BillingConstants.ALERT_PLAN_UNLIMITED)
        {
            return R.drawable.buy_alerts_infinite;
        }
        else
        {
            throw new IllegalArgumentException("Unexpected count " + count);
        }
    }

    @NonNull public abstract ProductIdentifier createFrom(@NonNull AlertPlanDTO alertPlanDTO);

    @Nullable public abstract ProductIdentifier getServerEquivalentSKU(@NonNull ProductIdentifier localProductId);
}
