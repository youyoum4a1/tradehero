package com.tradehero.th.billing.amazon.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.amazon.THAmazonOrderId;
import com.tradehero.th.billing.amazon.THAmazonProductDetail;
import com.tradehero.th.billing.amazon.THAmazonPurchase;
import com.tradehero.th.billing.report.PurchaseReportResult;
import com.tradehero.th.billing.report.THBasePurchaseReporterRx;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
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
