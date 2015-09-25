package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.AlertPlanDTO;

public class BillingSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    @NonNull public static IABSKU createFrom(@NonNull AlertPlanDTO alertPlanDTO)
    {
        return new IABSKU(alertPlanDTO.productIdentifier);
    }

    @Nullable public static IABSKU getServerEquivalentSKU(@NonNull ProductIdentifier localSKU)
    {
        //if (localSKU instanceof IABSKU)
        //{
        //    switch (((IABSKU) localSKU).identifier)
        //    {
        //        case THBillingConstants.SERVER_ALERT_1:
        //            return new IABSKU(THIABConstants.ALERT_1);
        //
        //        case THBillingConstants.SERVER_ALERT_5:
        //            return new IABSKU(THIABConstants.ALERT_5);
        //
        //        case THBillingConstants.SERVER_ALERT_UNLIMITED:
        //            return new IABSKU(THIABConstants.ALERT_UNLIMITED);
        //    }
        //}

        return null;
    }
}
