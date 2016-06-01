package com.ayondo.academy.billing.amazon.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonProductDetail;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.report.PurchaseReportResult;
import com.ayondo.academy.billing.report.THBasePurchaseReporterRx;
import com.ayondo.academy.network.service.AlertPlanCheckServiceWrapper;
import com.ayondo.academy.network.service.AlertPlanServiceWrapper;
import com.ayondo.academy.network.service.PortfolioServiceWrapper;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;

public class THBaseAmazonPurchaseReporterRx
        extends THBasePurchaseReporterRx<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonOrderId,
        THAmazonPurchase>
        implements THAmazonPurchaseReporterRx
{
    //<editor-fold desc="Constructors">
    public THBaseAmazonPurchaseReporterRx(
            int requestCode,
            @NonNull THAmazonPurchase purchase,
            @NonNull THAmazonProductDetail productDetail,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper)
    {
        super(
                requestCode,
                purchase,
                productDetail,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioServiceWrapper);
    }
    //</editor-fold>

    @NonNull @Override protected PurchaseReportResult<AmazonSKU,
            THAmazonOrderId,
            THAmazonPurchase> createResult(
            @NonNull UserProfileDTO userProfileDTO)
    {
        return new PurchaseReportResult<>(getRequestCode(), purchase, userProfileDTO);
    }
}
