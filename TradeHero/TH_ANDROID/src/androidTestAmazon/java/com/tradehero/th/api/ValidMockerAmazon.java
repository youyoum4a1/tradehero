package com.tradehero.th.api;

import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.billing.PurchaseReportDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ValidMockerAmazon extends ValidMocker
{
    @Inject public ValidMockerAmazon()
    {
        super();
    }

    @Override public Object mockValidParameter(@NotNull Class<?> type)
    {
        if (type.equals(PurchaseReportDTO.class))
        {
            return new AmazonPurchaseReportDTO("purchaseToken", "userId");
        }
        return super.mockValidParameter(type);
    }
}
