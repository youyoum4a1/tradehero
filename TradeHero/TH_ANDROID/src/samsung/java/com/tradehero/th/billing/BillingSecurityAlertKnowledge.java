package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.billing.samsung.THSamsungConstants;

public class BillingSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    @NonNull public static SamsungSKU createFrom(@NonNull AlertPlanDTO alertPlanDTO)
    {
        return new SamsungSKU(alertPlanDTO.productIdentifier);
    }

    @Nullable public static SamsungSKU getServerEquivalentSKU(@NonNull ProductIdentifier localSKU)
    {
        if (localSKU instanceof SamsungSKU)
        {
            switch (((SamsungSKU) localSKU).itemId)
            {
                case THBillingConstants.SERVER_ALERT_1:
                    return new SamsungSKU(THSamsungConstants.ALERT_1_DATA_1);

                case THBillingConstants.SERVER_ALERT_5:
                    return new SamsungSKU(THSamsungConstants.ALERT_5_DATA_1);

                case THBillingConstants.SERVER_ALERT_UNLIMITED:
                    return new SamsungSKU(THSamsungConstants.ALERT_UNLIMITED_DATA_1);
            }
        }

        return null;
    }
}
