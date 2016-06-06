package com.androidth.general.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.api.alert.AlertPlanDTO;

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
