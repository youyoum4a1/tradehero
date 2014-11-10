package com.tradehero.th.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import timber.log.Timber;

abstract public class THBasePurchaseReporterHolder<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>,
        THPurchaseReporterType extends THPurchaseReporter<
                    ProductIdentifierType,
                THOrderIdType,
                    THProductPurchaseType,
                    BillingExceptionType>,
        BillingExceptionType extends BillingException>
    implements THPurchaseReporterHolder<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType>
{
    @NonNull protected final Lazy<UserProfileCacheRx> userProfileCache;
    @NonNull protected final Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache;
    @NonNull protected final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull protected final Provider<THPurchaseReporterType> thPurchaseReporterTypeProvider;

    @NonNull protected final Map<Integer /*requestCode*/, THPurchaseReporterType> purchaseReporters;
    @NonNull protected final Map<Integer /*requestCode*/, THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType>> parentPurchaseReportedHandlers;

    //<editor-fold desc="Constructors">
    public THBasePurchaseReporterHolder(
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<PortfolioCompactListCacheRx> portfolioCompactListCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull Provider<THPurchaseReporterType> thPurchaseReporterTypeProvider)
    {
        super();
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCache = portfolioCache;
        this.thPurchaseReporterTypeProvider = thPurchaseReporterTypeProvider;
        this.purchaseReporters = new HashMap<>();
        this.parentPurchaseReportedHandlers = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !purchaseReporters.containsKey(requestCode) &&
                !parentPurchaseReportedHandlers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        parentPurchaseReportedHandlers.remove(requestCode);
        THPurchaseReporterType purchaseReporter = purchaseReporters.get(requestCode);
        if (purchaseReporter != null)
        {
            purchaseReporter.setPurchaseReporterListener(null);
        }
        purchaseReporters.remove(requestCode);
    }

    @Override @Nullable public THPurchaseReporter.OnPurchaseReportedListener<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType,
            BillingExceptionType> getPurchaseReportedListener(int requestCode)
    {
        return parentPurchaseReportedHandlers.get(requestCode);
    }

    @Override public void registerPurchaseReportedListener(
            int requestCode,
            @Nullable THPurchaseReporter.OnPurchaseReportedListener<
                    ProductIdentifierType,
                    THOrderIdType,
                    THProductPurchaseType,
                    BillingExceptionType> purchaseReportedHandler)
    {
        parentPurchaseReportedHandlers.put(requestCode, purchaseReportedHandler);
    }

    @Override public void launchReportSequence(int requestCode, THProductPurchaseType purchase)
    {
        THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> reportedListener = createPurchaseReportedListener();
        THPurchaseReporterType purchaseReporter = thPurchaseReporterTypeProvider.get();
        purchaseReporter.setPurchaseReporterListener(reportedListener);
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(requestCode, purchase);
    }

    @NonNull protected THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType> createPurchaseReportedListener()
    {
        return new THPurchaseReporter.OnPurchaseReportedListener<ProductIdentifierType, THOrderIdType, THProductPurchaseType, BillingExceptionType>()
        {
            @Override public void onPurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
            {
                handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        };
    }

    protected void handlePurchaseReported(int requestCode, THProductPurchaseType reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        Timber.d("handlePurchaseReported Purchase info " + reportedPurchase);

        if (updatedUserPortfolio != null)
        {
            userProfileCache.get().get(updatedUserPortfolio.getBaseKey());
        }

        OwnedPortfolioId applicablePortfolioId = reportedPurchase.getApplicableOwnedPortfolioId();
        if (applicablePortfolioId != null)
        {
            portfolioCompactListCache.get().get(applicablePortfolioId.getUserBaseKey());
            // TODO put back when #68094144 is fixed
            //getPortfolioCompactCache().invalidate(applicablePortfolioId.getPortfolioIdKey());
            portfolioCache.get().get(applicablePortfolioId);
        }

        THPurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType> handler = getPurchaseReportedListener(requestCode);
        if (handler != null)
        {
            Timber.d("handlePurchaseReported passing on the purchase for requestCode %d", requestCode);
            handler.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        else
        {
            Timber.d("handlePurchaseReported No PurchaseReportedHandler for requestCode %d", requestCode);
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, THProductPurchaseType reportedPurchase, BillingExceptionType error)
    {
        Timber.e(error, "handlePurchaseReportFailed There was an exception during the report");
        THPurchaseReporter.OnPurchaseReportedListener<
                ProductIdentifierType,
                THOrderIdType,
                THProductPurchaseType,
                BillingExceptionType> handler = getPurchaseReportedListener(requestCode);
        if (handler != null)
        {
            Timber.d("handlePurchaseReportFailed passing on the exception for requestCode %d", requestCode);
            handler.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        else
        {
            Timber.d("handlePurchaseReportFailed No THIABPurchaseHandler for requestCode %d", requestCode);
        }
    }

    @Override public void onDestroy()
    {
        for (THPurchaseReporterType purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setPurchaseReporterListener(null);
            }
        }
        purchaseReporters.clear();
        parentPurchaseReportedHandlers.clear();
    }
}
