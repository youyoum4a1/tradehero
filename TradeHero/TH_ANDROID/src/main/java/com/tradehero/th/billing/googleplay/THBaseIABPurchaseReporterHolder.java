package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

class THBaseIABPurchaseReporterHolder
    extends THBasePurchaseReporterHolder<
                IABSKU,
                THIABOrderId,
                THIABPurchase,
                THIABPurchaseReporter,
                IABException>
    implements THIABPurchaseReporterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseReporterHolder(
            @NotNull Lazy<UserProfileCache> userProfileCache,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            @NotNull Lazy<PortfolioCache> portfolioCache,
            @NotNull Provider<THIABPurchaseReporter> thiabPurchaseReporterProvider)
    {
        super(userProfileCache, portfolioCompactListCache, portfolioCache, thiabPurchaseReporterProvider);
    }
    //</editor-fold>
}
