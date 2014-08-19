package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.IABPurchaseCache;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.request.UIIABBillingRequest;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;
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
                IABSKUListRetrievedAsyncMilestone.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class GooglePlayBillingModule
{
    //<editor-fold desc="Actors and Action Holders">
    @Provides THIABBillingAvailableTester provideBillingAvailableTest(THBaseIABBillingAvailableTester thBaseIABBillingAvailableTester)
    {
        return thBaseIABBillingAvailableTester;
    }

    @Provides THIABBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseIABBillingAvailableTesterHolder thBaseIABBillingAvailableTesterHolder)
    {
        return thBaseIABBillingAvailableTesterHolder;
    }

    @Provides THIABProductIdentifierFetcher provideProductIdentifierFetcher(THBaseIABProductIdentifierFetcher thBaseIABProductIdentifierFetcher)
    {
        return thBaseIABProductIdentifierFetcher;
    }

    @Provides THIABProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseIABProductIdentifierFetcherHolder thBaseIABProductIdentifierFetcherHolder)
    {
        return thBaseIABProductIdentifierFetcherHolder;
    }

    @Provides THIABInventoryFetcher provideInventoryFetcher(THBaseIABInventoryFetcher thBaseIABInventoryFetcher)
    {
        return thBaseIABInventoryFetcher;
    }

    @Provides THIABInventoryFetcherHolder provideInventoryFetcherHolder(THBaseIABInventoryFetcherHolder thBaseIABInventoryFetcherHolder)
    {
        return thBaseIABInventoryFetcherHolder;
    }

    @Provides THIABPurchaseFetcher providePurchaseFetcher(THBaseIABPurchaseFetcher thBaseIABPurchaseFetcher)
    {
        return thBaseIABPurchaseFetcher;
    }

    @Provides THIABPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseIABPurchaseFetcherHolder thBaseIABPurchaseFetcherHolder)
    {
        return thBaseIABPurchaseFetcherHolder;
    }

    @Provides THIABPurchaser providePurchaser(THBaseIABPurchaser thBaseIABPurchaser)
    {
        return thBaseIABPurchaser;
    }

    @Provides THIABPurchaserHolder providePurchaserHolder(THBaseIABPurchaserHolder thBaseIABPurchaserHolder)
    {
        return thBaseIABPurchaserHolder;
    }

    @Provides THIABPurchaseReporter providePurchaseReporter(THBaseIABPurchaseReporter thBaseIABPurchaseReporter)
    {
        return thBaseIABPurchaseReporter;
    }

    @Provides THIABPurchaseReporterHolder providePurchaseReporterHolder(THBaseIABPurchaseReporterHolder thBaseIABPurchaseReporterHolder)
    {
        return thBaseIABPurchaseReporterHolder;
    }

    @Provides THIABPurchaseConsumer providePurchaseConsumer(THBaseIABPurchaseConsumer thBaseIABPurchaseConsumer)
    {
        return thBaseIABPurchaseConsumer;
    }

    @Provides THIABPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseIABPurchaseConsumerHolder thBaseIABPurchaseConsumerHolder)
    {
        return thBaseIABPurchaseConsumerHolder;
    }
    //</editor-fold>

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
    {
        return THIABAlertDialogUtil;
    }

    //<editor-fold desc="Caches">
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
    //</editor-fold>

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

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THIABInteractor thiabInteractor)
    {
        return thiabInteractor;
    }

    @Provides @Singleton THIABInteractor provideTHIABInteractor(THIABBillingInteractor thiabInteractor)
    {
        return thiabInteractor;
    }

    @Provides BillingRequest provideBillingRequest(THBillingRequest request)
    {
        return request;
    }

    @Provides THBillingRequest provideTHBillingRequest(THIABBillingRequestFull request)
    {
        return request;
    }

    @Provides UIBillingRequest provideUIBillingRequest(THUIBillingRequest request)
    {
        return request;
    }

    @Provides UIIABBillingRequest provideUIIABBillingRequest(THUIIABBillingRequest request)
    {
        return request;
    }

    @Provides THUIBillingRequest provideTHUIBillingRequest(THUIIABBillingRequest request)
    {
        return request;
    }
}
