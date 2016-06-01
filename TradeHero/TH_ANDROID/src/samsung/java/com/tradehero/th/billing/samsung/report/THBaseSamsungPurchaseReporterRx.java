package com.ayondo.academy.billing.samsung.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.ayondo.academy.billing.report.THBasePurchaseReporterRx;
import com.ayondo.academy.billing.samsung.THSamsungOrderId;
import com.ayondo.academy.billing.samsung.THSamsungProductDetail;
import com.ayondo.academy.billing.samsung.THSamsungPurchase;
import com.ayondo.academy.network.service.AlertPlanCheckServiceWrapper;
import com.ayondo.academy.network.service.AlertPlanServiceWrapper;
import com.ayondo.academy.network.service.PortfolioServiceWrapper;
import com.ayondo.academy.network.service.UserServiceWrapper;
import dagger.Lazy;

public class THBaseSamsungPurchaseReporterRx
        extends THBasePurchaseReporterRx<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaseReporterRx
{
    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaseReporterRx(
            int requestCode,
            @NonNull THSamsungPurchase purchase,
            @NonNull THSamsungProductDetail productDetail,
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
