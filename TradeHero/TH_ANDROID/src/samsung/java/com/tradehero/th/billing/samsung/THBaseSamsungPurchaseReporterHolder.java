package com.tradehero.th.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseSamsungPurchaseReporterHolder
    extends THBasePurchaseReporterHolder<
                SamsungSKU,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungPurchaseReporter,
                SamsungException>
    implements THSamsungPurchaseReporterHolder
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<THSamsungProductDetailCacheRx> productDetailCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseReporterHolder(
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THSamsungProductDetailCacheRx> productDetailCache)
    {
        super(userProfileCache, portfolioCompactListCache, portfolioCache);
        this.currentUserId = currentUserId;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.productDetailCache = productDetailCache;
    }
    //</editor-fold>

    @NonNull @Override protected THSamsungPurchaseReporter createPurchaseReporter(int requestCode)
    {
        return new THBaseSamsungPurchaseReporter(
                requestCode,
                currentUserId,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioCompactListCache,
                portfolioServiceWrapper,
                productDetailCache);
    }
}
