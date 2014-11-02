package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonPurchaseReporterHolder
    extends THBasePurchaseReporterHolder<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonPurchaseReporter,
                AmazonException>
    implements THAmazonPurchaseReporterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseReporterHolder(
            @NotNull Lazy<UserProfileCache> userProfileCache,
            @NotNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NotNull Lazy<PortfolioCacheRx> portfolioCache,
            @NotNull Provider<THAmazonPurchaseReporter> thAmazonPurchaseReporterProvider)
    {
        super(userProfileCache, portfolioCompactListCache, portfolioCache, thAmazonPurchaseReporterProvider);
    }
    //</editor-fold>
}
