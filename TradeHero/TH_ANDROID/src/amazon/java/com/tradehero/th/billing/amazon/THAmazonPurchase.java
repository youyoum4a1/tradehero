package com.ayondo.academy.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.api.billing.AmazonPurchaseInProcessDTO;
import com.ayondo.academy.api.billing.AmazonPurchaseReportDTO;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.billing.THProductPurchase;

public interface THAmazonPurchase
    extends THProductPurchase<AmazonSKU, THAmazonOrderId>,
        AmazonPurchase<AmazonSKU, THAmazonOrderId>
{
    void setApplicablePortfolioId(@NonNull OwnedPortfolioId applicablePortfolioId);
    @NonNull @Override AmazonPurchaseReportDTO getPurchaseReportDTO();
    @NonNull AmazonPurchaseInProcessDTO getPurchaseToSaveDTO();
    boolean shouldConsume();
}
