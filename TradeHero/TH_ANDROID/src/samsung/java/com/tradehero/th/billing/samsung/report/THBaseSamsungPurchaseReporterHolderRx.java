package com.androidth.general.billing.samsung.report;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.billing.report.THBasePurchaseReporterHolderRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import com.androidth.general.billing.samsung.THSamsungPurchase;
import com.androidth.general.network.service.AlertPlanCheckServiceWrapper;
import com.androidth.general.network.service.AlertPlanServiceWrapper;
import com.androidth.general.network.service.PortfolioServiceWrapper;
import com.androidth.general.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;

public class THBaseSamsungPurchaseReporterHolderRx
        extends THBasePurchaseReporterHolderRx<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaseReporterHolderRx
{
    @NonNull protected final Lazy<UserServiceWrapper> userServiceWrapper;
    @NonNull protected final Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper;
    @NonNull protected final Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper;
    @NonNull protected final Lazy<PortfolioServiceWrapper> portfolioServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseReporterHolderRx(
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper)
    {
        super();
        this.userServiceWrapper = userServiceWrapper;
        this.alertPlanServiceWrapper = alertPlanServiceWrapper;
        this.alertPlanCheckServiceWrapper = alertPlanCheckServiceWrapper;
        this.portfolioServiceWrapper = portfolioServiceWrapper;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseSamsungPurchaseReporterRx createReporter(
            int requestCode,
            @NonNull THSamsungPurchase purchase,
            @NonNull THSamsungProductDetail productDetail)
    {
        return new THBaseSamsungPurchaseReporterRx(
                requestCode,
                purchase,
                productDetail,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioServiceWrapper);
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
