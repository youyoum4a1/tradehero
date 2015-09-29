package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.billing.amazon.THAmazonConstants;

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
