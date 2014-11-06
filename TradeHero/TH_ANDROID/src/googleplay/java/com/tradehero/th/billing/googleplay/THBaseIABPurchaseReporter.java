package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.googleplay.exception.IABMissingCachedProductDetailException;
import com.tradehero.th.billing.googleplay.exception.IABPurchaseReportRetrofitException;
import com.tradehero.th.billing.googleplay.exception.IABUnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanCheckServiceWrapper;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import timber.log.Timber;

public class THBaseIABPurchaseReporter
        extends THBasePurchaseReporter<
                IABSKU,
                THIABProductDetail,
                THIABProductDetailTuner,
                THIABOrderId,
                THIABPurchase,
                IABException>
    implements THIABPurchaseReporter
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABPurchaseReporter(
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NonNull Lazy<AlertPlanCheckServiceWrapper> alertPlanCheckServiceWrapper,
            @NonNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NonNull Lazy<THIABProductDetailCacheRx> skuDetailCache)
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

    @Override public void reportPurchase(int requestCode, @NonNull THIABPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;

        // TODO do something when info is not available
        productDetail = productDetailCache.get().getValue(purchase.getProductIdentifier());
        if (productDetail == null)
        {
            notifyListenerReportFailed(new IABMissingCachedProductDetailException(purchase.getProductIdentifier() + " is missing from the cache"));
            return;
        }

        if (!reportPurchase())
        {
            throw new IABUnhandledSKUDomainException(productDetail.getDomain() + " is not handled by this method");
        }
    }

    @Override protected void handleCallbackFailed(RetrofitError error)
    {
        Timber.e("Failed reporting to TradeHero server", error);
        notifyListenerReportFailed(new IABPurchaseReportRetrofitException(error));
    }

}
