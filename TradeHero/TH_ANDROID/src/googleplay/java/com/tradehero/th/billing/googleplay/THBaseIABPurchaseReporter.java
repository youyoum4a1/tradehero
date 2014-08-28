package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.THBasePurchaseReporter;
import com.tradehero.th.billing.googleplay.exception.IABMissingCachedProductDetailException;
import com.tradehero.th.billing.googleplay.exception.IABPurchaseReportRetrofitException;
import com.tradehero.th.billing.googleplay.exception.IABUnhandledSKUDomainException;
import com.tradehero.th.network.service.AlertPlanServiceWrapper;
import com.tradehero.th.network.service.PortfolioServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
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
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<AlertPlanServiceWrapper> alertPlanServiceWrapper,
            @NotNull Lazy<UserServiceWrapper> userServiceWrapper,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            @NotNull Lazy<PortfolioServiceWrapper> portfolioServiceWrapper,
            @NotNull Lazy<THIABProductDetailCache> skuDetailCache)
    {
        super(
                currentUserId,
                alertPlanServiceWrapper,
                userServiceWrapper,
                portfolioCompactListCache,
                portfolioServiceWrapper,
                skuDetailCache);
    }
    //</editor-fold>

    @Override public void reportPurchase(int requestCode, @NotNull THIABPurchase purchase)
    {
        this.requestCode = requestCode;
        this.purchase = purchase;

        // TODO do something when info is not available
        productDetail = productDetailCache.get().get(purchase.getProductIdentifier());
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
