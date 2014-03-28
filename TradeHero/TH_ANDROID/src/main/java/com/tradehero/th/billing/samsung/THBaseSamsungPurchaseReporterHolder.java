package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseSamsungPurchaseReporterHolder
    extends THBasePurchaseReporterHolder<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                THBaseSamsungPurchaseReporter,
                SamsungException>
    implements THSamsungPurchaseReporterHolder
{
    @Inject UserProfileCache userProfileCache;
    @Inject PortfolioCompactListCache portfolioCompactListCache;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject PortfolioCache portfolioCache;

    public THBaseSamsungPurchaseReporterHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected UserProfileCache getUserProfileCache()
    {
        return userProfileCache;
    }

    @Override protected PortfolioCompactListCache getPortfolioCompactListCache()
    {
        return portfolioCompactListCache;
    }

    @Override protected PortfolioCompactCache getPortfolioCompactCache()
    {
        return portfolioCompactCache;
    }

    @Override protected PortfolioCache getPortfolioCache()
    {
        return portfolioCache;
    }

    @Override protected THBaseSamsungPurchaseReporter createPurchaseReporter()
    {
        return new THBaseSamsungPurchaseReporter();
    }
}
