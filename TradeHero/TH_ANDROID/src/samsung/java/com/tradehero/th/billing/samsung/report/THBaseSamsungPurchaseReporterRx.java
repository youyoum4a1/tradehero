package com.androidth.general.billing.samsung.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.billing.report.THBasePurchaseReporterRx;
import com.androidth.general.billing.samsung.THSamsungOrderId;
import com.androidth.general.billing.samsung.THSamsungProductDetail;
import com.androidth.general.billing.samsung.THSamsungPurchase;
import com.androidth.general.network.service.AlertPlanCheckServiceWrapper;
import com.androidth.general.network.service.AlertPlanServiceWrapper;
import com.androidth.general.network.service.PortfolioServiceWrapper;
import com.androidth.general.network.service.UserServiceWrapper;
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
