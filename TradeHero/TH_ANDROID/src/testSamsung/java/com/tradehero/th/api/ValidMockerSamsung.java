package com.ayondo.academy.api;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import com.ayondo.academy.api.billing.SamsungPurchaseReportDTO;
import javax.inject.Inject;

public class ValidMockerSamsung extends ValidMocker
{
    @Inject public ValidMockerSamsung()
    {
        super();
    }

    @Override public Object mockValidParameter(@NonNull Class<?> type)
    {
        if (type.equals(PurchaseReportDTO.class))
        {
            return new SamsungPurchaseReportDTO("paymentId", "purchaseId", "productCode");
        }
        return super.mockValidParameter(type);
    }
}
