package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.googleplay.exception.THIABExceptionFactory;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.googleplay.request.THUIIABBillingRequest;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                IABServiceConnector.class,
                BaseIABBillingAvailableTester.class,
                BaseIABInventoryFetcher.class,
                THBaseIABPurchaseFetcher.class,
                THBaseIABInventoryFetcher.class,
                THBaseIABPurchaser.class,
                THBaseIABPurchaseReporter.class,
                THIABLogicHolderFull.class,
                THBaseIABPurchaseConsumer.class,
                THBaseIABInventoryFetcherHolder.class,
                THBaseIABPurchaseReporterHolder.class,
                THIABPurchaseFetchMilestone.class,
                IABSKUListRetrievedAsyncMilestone.class,
                THIABBillingInteractor.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class THIABModule
{
    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
    {
        return THIABAlertDialogUtil;
    }

    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(IABSKUListCache iabskuListCache)
    {
        return iabskuListCache;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THIABProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(IABPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton IABPurchaseCache provideIABPurchaseCache(THIABPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides BillingExceptionFactory provideBillingExceptionFactory(IABExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides IABExceptionFactory provideIABExceptionFactory(THIABExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THIABLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THIABLogicHolder provideTHIABLogicHolder(THIABLogicHolderFull thiabLogicHolderFull)
    {
        return thiabLogicHolderFull;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides THBillingInteractor provideTHBillingInteractor(THIABBillingInteractor thiabInteractor)
    {
        return thiabInteractor;
    }

    @Provides THBillingRequest provideBillingRequest(THIABBillingRequestFull request)
    {
        return request;
    }

    @Provides THUIBillingRequest provideUIBillingRequest(THUIIABBillingRequest request)
    {
        return request;
    }
}
