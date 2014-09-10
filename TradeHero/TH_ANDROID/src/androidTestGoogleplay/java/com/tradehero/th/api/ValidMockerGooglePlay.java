package com.tradehero.th.api;

import com.tradehero.th.api.ValidMocker;
import com.tradehero.th.api.billing.GooglePlayPurchaseReportDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ValidMockerGooglePlay extends ValidMocker
{
    @Inject public ValidMockerGooglePlay()
    {
        super();
    }

    @Override public Object mockValidParameter(@NotNull Class<?> type)
    {
        if (type.equals(PurchaseReportDTO.class))
        {
            return new GooglePlayPurchaseReportDTO("data", "signature");
        }
        return super.mockValidParameter(type);
    }
}
