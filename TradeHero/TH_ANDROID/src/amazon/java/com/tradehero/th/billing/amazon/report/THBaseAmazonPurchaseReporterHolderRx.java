package com.tradehero.th.billing.amazon.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.report.THBasePurchaseReporterHolderRx;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseAmazonPurchaseReporterHolderRx
        extends THBasePurchaseReporterHolderRx<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonOrderId,
        THAmazonPurchase>
        implements THAmazonPurchaseReporterHolderRx
{
    @NonNull protected final THAmazonProductDetailCacheRx productDetailCache;
    @NonNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseReporterHolderRx(
            @NonNull THAmazonProductDetailCacheRx productDetailCache,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper)
    {
        super();
        this.productDetailCache = productDetailCache;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonPurchaseReporterRx createReporter(
            int requestCode,
            @NonNull THAmazonPurchase purchase,
            @NonNull THAmazonProductDetail productDetail)
    {
        return new THBaseAmazonPurchaseReporterRx(
                requestCode,
                purchase,
                productDetail,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioServiceWrapper);
    }
}
