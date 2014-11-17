package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporterHolder;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseAmazonPurchaseReporterHolder
        extends THBasePurchaseReporterHolder<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase,
        THAmazonPurchaseReporter,
        AmazonException>
        implements THAmazonPurchaseReporterHolder
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<THAmazonProductDetailCacheRx> skuDetailCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseReporterHolder(
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THAmazonProductDetailCacheRx> skuDetailCache)
    {
        super(userProfileCache, portfolioCompactListCache, portfolioCache);
        this.currentUserId = currentUserId;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.skuDetailCache = skuDetailCache;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonPurchaseReporter createPurchaseReporter(int requestCode)
    {
        return new THBaseAmazonPurchaseReporter(requestCode,
                currentUserId,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioCompactListCache,
                portfolioServiceWrapper,
                skuDetailCache);
    }
}
