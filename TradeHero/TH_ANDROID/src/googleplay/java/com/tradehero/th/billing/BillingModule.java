package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.IABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCache;
import com.tradehero.th.billing.googleplay.THBaseIABBillingAvailableTester;
import com.tradehero.th.billing.googleplay.THBaseIABBillingAvailableTesterHolder;
import com.tradehero.th.billing.googleplay.THBaseIABInventoryFetcher;
import com.tradehero.th.billing.googleplay.THBaseIABInventoryFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABProductIdentifierFetcher;
import com.tradehero.th.billing.googleplay.THBaseIABProductIdentifierFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseConsumerHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseFetcher;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseReporter;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseReporterHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaser;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaserHolder;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABBillingAvailableTester;
import com.tradehero.th.billing.googleplay.THIABBillingAvailableTesterHolder;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABInteractor;
import com.tradehero.th.billing.googleplay.THIABInventoryFetcher;
import com.tradehero.th.billing.googleplay.THIABInventoryFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABLogicHolderFull;
import com.tradehero.th.billing.googleplay.THIABProductIdentifierFetcher;
import com.tradehero.th.billing.googleplay.THIABProductIdentifierFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumerHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcher;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporterHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaser;
import com.tradehero.th.billing.googleplay.THIABPurchaserHolder;
import com.tradehero.th.billing.googleplay.THIABSecurityAlertKnowledge;
import com.tradehero.th.billing.googleplay.exception.THIABExceptionFactory;
import com.tradehero.th.billing.googleplay.request.BaseTHUIIABRequest;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCache;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                IABSKUListRetrievedAsyncMilestone.class,
                THIABBillingInteractor.class,
                IABServiceConnector.class,
                IABBillingAvailableTester.class,
                IABSKUListRetrievedAsyncMilestone.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
    //<editor-fold desc="Actors and Action Holders">
    @Provides
    THIABBillingAvailableTester provideBillingAvailableTest(THBaseIABBillingAvailableTester thBaseIABBillingAvailableTester)
    {
        return thBaseIABBillingAvailableTester;
    }

    @Provides
    THIABBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseIABBillingAvailableTesterHolder thBaseIABBillingAvailableTesterHolder)
    {
        return thBaseIABBillingAvailableTesterHolder;
    }

    @Provides
    THIABProductIdentifierFetcher provideProductIdentifierFetcher(THBaseIABProductIdentifierFetcher thBaseIABProductIdentifierFetcher)
    {
        return thBaseIABProductIdentifierFetcher;
    }

    @Provides
    THIABProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseIABProductIdentifierFetcherHolder thBaseIABProductIdentifierFetcherHolder)
    {
        return thBaseIABProductIdentifierFetcherHolder;
    }

    @Provides
    THIABInventoryFetcher provideInventoryFetcher(THBaseIABInventoryFetcher thBaseIABInventoryFetcher)
    {
        return thBaseIABInventoryFetcher;
    }

    @Provides
    THIABInventoryFetcherHolder provideInventoryFetcherHolder(THBaseIABInventoryFetcherHolder thBaseIABInventoryFetcherHolder)
    {
        return thBaseIABInventoryFetcherHolder;
    }

    @Provides
    THIABPurchaseFetcher providePurchaseFetcher(THBaseIABPurchaseFetcher thBaseIABPurchaseFetcher)
    {
        return thBaseIABPurchaseFetcher;
    }

    @Provides
    THIABPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseIABPurchaseFetcherHolder thBaseIABPurchaseFetcherHolder)
    {
        return thBaseIABPurchaseFetcherHolder;
    }

    @Provides
    THIABPurchaser providePurchaser(THBaseIABPurchaser thBaseIABPurchaser)
    {
        return thBaseIABPurchaser;
    }

    @Provides
    THIABPurchaserHolder providePurchaserHolder(THBaseIABPurchaserHolder thBaseIABPurchaserHolder)
    {
        return thBaseIABPurchaserHolder;
    }

    @Provides
    THIABPurchaseReporter providePurchaseReporter(THBaseIABPurchaseReporter thBaseIABPurchaseReporter)
    {
        return thBaseIABPurchaseReporter;
    }

    @Provides
    THIABPurchaseReporterHolder providePurchaseReporterHolder(THBaseIABPurchaseReporterHolder thBaseIABPurchaseReporterHolder)
    {
        return thBaseIABPurchaseReporterHolder;
    }

    @Provides
    THIABPurchaseConsumer providePurchaseConsumer(THBaseIABPurchaseConsumer thBaseIABPurchaseConsumer)
    {
        return thBaseIABPurchaseConsumer;
    }

    @Provides
    THIABPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseIABPurchaseConsumerHolder thBaseIABPurchaseConsumerHolder)
    {
        return thBaseIABPurchaseConsumerHolder;
    }
    //</editor-fold>

    @Provides SecurityAlertKnowledge provideSecurityAlertKnowledge(THIABSecurityAlertKnowledge thiabSecurityAlertKnowledge)
    {
        return thiabSecurityAlertKnowledge;
    }

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

    @Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    {
        return THIABBillingRequestFull.builder();
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestTestAvailableBuilder()
    {
        return BaseTHUIIABRequest.builder();
    }
}
