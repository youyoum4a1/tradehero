package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.api.alert.AlertPlanDTO;
import com.ayondo.academy.billing.amazon.THAmazonConstants;

public class BillingSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    @NonNull public static AmazonSKU createFrom(@NonNull AlertPlanDTO alertPlanDTO)
    {
        return new AmazonSKU(alertPlanDTO.productIdentifier);
    }

    @Nullable public static AmazonSKU getServerEquivalentSKU(@NonNull ProductIdentifier localSKU)
    {
        return null;
    }
}
