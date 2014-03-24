package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.BasePurchaseReporterHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABPurchaseReporterHolder
    extends BasePurchaseReporterHolder<
            IABSKU,
            THIABOrderId,
            THIABPurchase,
            THIABPurchaseReporter,
            IABException>
    implements THIABPurchaseReporterHolder
{
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject Lazy<PortfolioCache> portfolioCache;

    public THBaseIABPurchaseReporterHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected UserProfileCache getUserProfileCache()
    {
        return userProfileCache.get();
    }

    @Override protected PortfolioCompactListCache getPortfolioCompactListCache()
    {
        return portfolioCompactListCache.get();
    }

    @Override protected PortfolioCompactCache getPortfolioCompactCache()
    {
        return portfolioCompactCache.get();
    }

    @Override protected PortfolioCache getPortfolioCache()
    {
        return portfolioCache.get();
    }

    @Override protected THIABPurchaseReporter createPurchaseReporter()
    {
        return new THIABPurchaseReporter();
    }
}
