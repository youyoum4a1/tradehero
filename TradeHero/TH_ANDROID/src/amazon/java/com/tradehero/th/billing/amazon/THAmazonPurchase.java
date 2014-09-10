package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.api.billing.AmazonPurchaseReportDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.billing.THProductPurchase;
import org.jetbrains.annotations.NotNull;

public interface THAmazonPurchase
    extends THProductPurchase<AmazonSKU, THAmazonOrderId>,
        AmazonPurchase<AmazonSKU, THAmazonOrderId>
{
    void setApplicablePortfolioId(@NotNull OwnedPortfolioId applicablePortfolioId);
    @NotNull @Override AmazonPurchaseReportDTO getPurchaseReportDTO();
    @NotNull AmazonPurchaseInProcessDTO getPurchaseToSaveDTO();
    boolean shouldConsume();
}
