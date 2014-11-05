package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.THProductPurchase;
import android.support.annotation.NonNull;

public interface THAmazonPurchase
    extends THProductPurchase<AmazonSKU, THAmazonOrderId>,
        AmazonPurchase<AmazonSKU, THAmazonOrderId>
{
    void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId);
    @NonNull @Override AmazonPurchaseReportDTO getPurchaseReportDTO();
    @NonNull AmazonPurchaseInProcessDTO getPurchaseToSaveDTO();
    boolean shouldConsume();
}
