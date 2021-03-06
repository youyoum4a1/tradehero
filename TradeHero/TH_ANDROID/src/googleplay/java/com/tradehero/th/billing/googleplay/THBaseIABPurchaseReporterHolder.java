package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABPurchaseReporterHolder
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
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Provider<THIABPurchaseReporter> thiabPurchaseReporterProvider)
    {
        super(userProfileCache, portfolioCompactListCache, portfolioCache, thiabPurchaseReporterProvider);
    }
    //</editor-fold>
}
