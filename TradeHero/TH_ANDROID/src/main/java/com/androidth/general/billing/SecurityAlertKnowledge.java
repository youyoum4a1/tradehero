package com.androidth.general.billing;

import android.support.annotation.DrawableRes;
import com.androidth.general.common.billing.BillingConstants;
import com.androidth.general.R;

public class SecurityAlertKnowledge
{
    @DrawableRes public static int getStockAlertIcon(int count)
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
}
