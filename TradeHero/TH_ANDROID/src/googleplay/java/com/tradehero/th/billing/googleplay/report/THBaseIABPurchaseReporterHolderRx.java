package com.tradehero.th.billing.googleplay.report;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.googleplay.THIABOrderId;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.report.THBasePurchaseReporterHolderRx;
import com.tradehero.th.billing.report.THPurchaseReporterRx;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseIABPurchaseReporterHolderRx
        extends THBasePurchaseReporterHolderRx<
        IABSKU,
        THIABProductDetail,
        THIABOrderId,
        THIABPurchase>
        implements THIABPurchaseReporterHolderRx
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;
    @NonNull protected final Lazy<THIABProductDetailCacheRx> skuDetailCache;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseReporterHolderRx(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THIABProductDetailCacheRx> skuDetailCache)
    {
        super();
        this.currentUserId = currentUserId;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.userServiceWrapper = userServiceWrapper;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
        this.skuDetailCache = skuDetailCache;
    }
    //</editor-fold>

    @NonNull @Override protected THPurchaseReporterRx<IABSKU, THIABOrderId, THIABPurchase> createReporter(
            int requestCode,
            @NonNull THIABPurchase purchase,
            @NonNull THIABProductDetail productDetail)
    {
        return new THBaseIABPurchaseReporterRx(
                requestCode,
                purchase,
                productDetail,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioServiceWrapper);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
