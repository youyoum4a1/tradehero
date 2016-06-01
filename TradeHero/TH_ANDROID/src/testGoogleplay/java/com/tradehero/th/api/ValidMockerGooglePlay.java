package com.ayondo.academy.api;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.billing.GooglePlayPurchaseReportDTO;
import com.ayondo.academy.api.billing.PurchaseReportDTO;
import javax.inject.Inject;

public class ValidMockerGooglePlay extends ValidMocker
{
    @Inject public ValidMockerGooglePlay()
    {
        super();
    }

    @Override public Object mockValidParameter(@NonNull Class<?> type)
    {
        if (type.equals(PurchaseReportDTO.class))
        {
            return new GooglePlayPurchaseReportDTO("data", "signature");
        }
        return super.mockValidParameter(type);
    }
}
