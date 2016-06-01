package com.ayondo.academy.billing.googleplay.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.ayondo.academy.billing.googleplay.THIABOrderId;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
import com.ayondo.academy.billing.googleplay.THIABPurchase;
import com.ayondo.academy.billing.report.THBasePurchaseReporterRx;
import com.ayondo.academy.network.service.AlertPlanCheckServiceWrapper;
import com.ayondo.academy.network.service.AlertPlanServiceWrapper;
import com.ayondo.academy.network.service.PortfolioServiceWrapper;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;

public class THBaseIABPurchaseReporterRx
        extends THBasePurchaseReporterRx<
                        IABSKU,
                        THIABProductDetail,
                        THIABOrderId,
                        THIABPurchase>
    implements THIABPurchaseReporterRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABPurchaseReporterRx(
            int requestCode,
            @NonNull THIABPurchase purchase,
            @NonNull THIABProductDetail productDetail,
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
}
