package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.ayondo.academy.api.alert.AlertPlanDTO;

public class BillingSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    @NonNull public static SamsungSKU createFrom(@NonNull AlertPlanDTO alertPlanDTO)
    {
        return new SamsungSKU(alertPlanDTO.productIdentifier);
    }

    @Nullable public static SamsungSKU getServerEquivalentSKU(@NonNull ProductIdentifier localSKU)
    {
        return null;
    }
}
