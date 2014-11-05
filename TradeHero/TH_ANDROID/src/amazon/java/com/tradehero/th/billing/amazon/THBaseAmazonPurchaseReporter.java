package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.amazon.exception.AmazonMissingCachedProductDetailException;
import com.tradehero.th.billing.amazon.exception.AmazonPurchaseReportRetrofitException;
import com.tradehero.th.billing.amazon.exception.AmazonUnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import dagger.Lazy;
import java.io.IOException;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import retrofit.RetrofitError;
import timber.log.Timber;

public class THBaseAmazonPurchaseReporter
        extends THBasePurchaseReporter<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonProductDetailTuner,
        THAmazonOrderId,
        THAmazonPurchase,
        AmazonException>
    implements THAmazonPurchaseReporter
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseReporter(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THAmazonProductDetailCache> skuDetailCache)
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

    @Override public void reportPurchase(int requestCode, @NonNull THAmazonPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;

        // TODO do something when info is not available
        productDetail = productDetailCache.get().get(purchase.getProductIdentifier());
        if (productDetail == null)
        {
            notifyListenerReportFailed(new AmazonMissingCachedProductDetailException(purchase.getProductIdentifier() + " is missing from the cache"));
            return;
        }

        if (!reportPurchase())
        {
            notifyListenerReportFailed(new AmazonUnhandledSKUDomainException(productDetail.domain + " is not handled by this method"));
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
        notifyListenerReportFailed(new AmazonPurchaseReportRetrofitException(error));
    }
}
