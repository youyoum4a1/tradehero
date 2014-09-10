package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.billing.SecurityAlertKnowledge;
import com.tradehero.th.billing.THBillingConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class THIABSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    //<editor-fold desc="Constructors">
    @Inject public THIABSecurityAlertKnowledge()
    {
    }
    //</editor-fold>

    @NotNull @Override public IABSKU createFrom(@NotNull AlertPlanDTO alertPlanDTO)
    {
        return new IABSKU(alertPlanDTO.productIdentifier);
    }

    @Override @Nullable public IABSKU getServerEquivalentSKU(@NotNull ProductIdentifier localSKU)
    {
        if (localSKU instanceof IABSKU)
        {
            switch (((IABSKU) localSKU).identifier)
            {
                case THBillingConstants.SERVER_ALERT_1:
                    return new IABSKU(THIABConstants.ALERT_1);

                case THBillingConstants.SERVER_ALERT_5:
                    return new IABSKU(THIABConstants.ALERT_5);

                case THBillingConstants.SERVER_ALERT_UNLIMITED:
                    return new IABSKU(THIABConstants.ALERT_UNLIMITED);
            }
        }

        return null;
    }
}
