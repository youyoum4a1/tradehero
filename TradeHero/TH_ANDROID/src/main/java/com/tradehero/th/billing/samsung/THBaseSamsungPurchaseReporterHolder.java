package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
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
public class THBaseSamsungPurchaseReporterHolder
    extends BasePurchaseReporterHolder<
            SamsungSKU,
            THSamsungOrderId,
            THSamsungPurchase,
            THBaseSamsungPurchaseReporter,
            SamsungException>
    implements THSamsungPurchaseReporterHolder
{
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject Lazy<PortfolioCache> portfolioCache;

    public THBaseSamsungPurchaseReporterHolder()
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

    @Override protected THBaseSamsungPurchaseReporter createPurchaseReporter()
    {
        return new THBaseSamsungPurchaseReporter();
    }
}
