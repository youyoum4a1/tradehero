package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.billing.SecurityAlertKnowledge;
import com.tradehero.th.billing.THBillingConstants;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Singleton public class THSamsungSecurityAlertKnowledge extends SecurityAlertKnowledge
{
    //<editor-fold desc="Constructors">
    @Inject public THSamsungSecurityAlertKnowledge()
    {
    }
    //</editor-fold>

    @NonNull @Override public SamsungSKU createFrom(@NonNull AlertPlanDTO alertPlanDTO)
    {
        return new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, alertPlanDTO.productIdentifier);
    }

    @Override @Nullable public SamsungSKU getServerEquivalentSKU(@NonNull ProductIdentifier localSKU)
    {
        if (localSKU instanceof SamsungSKU)
        {
            switch (((SamsungSKU) localSKU).itemId)
            {
                case THBillingConstants.SERVER_ALERT_1:
                    return new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.ALERT_1_DATA_1);

                case THBillingConstants.SERVER_ALERT_5:
                    return new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.ALERT_5_DATA_1);

                case THBillingConstants.SERVER_ALERT_UNLIMITED:
                    return new SamsungSKU(THSamsungConstants.IAP_ITEM_GROUP_ID, THSamsungConstants.ALERT_UNLIMITED_DATA_1);
            }
        }

        return null;
    }
}
