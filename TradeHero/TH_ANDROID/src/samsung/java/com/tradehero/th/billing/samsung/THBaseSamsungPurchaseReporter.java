package com.tradehero.th.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.samsung.exception.SamsungMissingCachedProductDetailException;
import com.tradehero.th.billing.samsung.exception.SamsungPurchaseReportRetrofitException;
import com.tradehero.th.billing.samsung.exception.SamsungUnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import dagger.Lazy;
import java.io.IOException;
import javax.inject.Inject;
import retrofit.RetrofitError;
import timber.log.Timber;

public class THBaseSamsungPurchaseReporter
        extends THBasePurchaseReporter<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungProductDetailTuner,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements THSamsungPurchaseReporter
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseReporter(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THSamsungProductDetailCacheRx> skuDetailCache)
    {
        super(
                currentUserId,
                alertPlanServiceWrapper,
                alertPlanCheckServiceWrapper,
                userServiceWrapper,
                portfolioCompactListCache,
                portfolioServiceWrapper,
                skuDetailCache);
    }
    //</editor-fold>

    @Override public void reportPurchase(int requestCode, @NonNull THSamsungPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;

        // TODO do something when info is not available
        productDetail = productDetailCache.get().getValue(purchase.getProductIdentifier());
        if (productDetail == null)
        {
            notifyListenerReportFailed(new SamsungMissingCachedProductDetailException(purchase.getProductIdentifier() + " is missing from the cache"));
            return;
        }

        if (!reportPurchase())
        {
            notifyListenerReportFailed(new SamsungUnhandledSKUDomainException(productDetail.domain + " is not handled by this method"));
        }
    }

    @Override protected void handleCallbackFailed(RetrofitError error)
    {
        Timber.e(error, "Failed reporting to TradeHero server");
        Timber.d("Is network error %s", error.isNetworkError());
        Timber.d("url %s", error.getUrl());
        try
        {
            Timber.d("body %s", IOUtils.errorToBodyString(error));
        }
        catch (IOException e)
        {
            Timber.e(e, "Failed to decode error body");
        }
        notifyListenerReportFailed(new SamsungPurchaseReportRetrofitException(error));
    }
}
