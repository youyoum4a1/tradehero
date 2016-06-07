package com.androidth.general.billing.googleplay.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.billing.googleplay.THIABOrderId;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import com.androidth.general.billing.googleplay.THIABPurchase;
import com.androidth.general.billing.report.THBasePurchaseReporterRx;
import com.androidth.general.network.service.AlertPlanCheckServiceWrapper;
import com.androidth.general.network.service.AlertPlanServiceWrapper;
import com.androidth.general.network.service.PortfolioServiceWrapper;
import com.androidth.general.network.service.UserServiceWrapper;
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
