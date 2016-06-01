package com.ayondo.academy.api;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.billing.AmazonPurchaseReportDTO;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import javax.inject.Inject;

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
