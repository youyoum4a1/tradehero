package com.tradehero.th.api;

import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class ValidMockerAmazon extends ValidMocker
{
    @Inject public ValidMockerAmazon()
    {
        super();
    }

    @Override public Object mockValidParameter(@NonNull Class<?> type)
    {
        if (type.equals(PurchaseReportDTO.class))
        {
            return new AmazonPurchaseReportDTO("sku", "purchaseToken", "userId");
        }
        return super.mockValidParameter(type);
    }
}
