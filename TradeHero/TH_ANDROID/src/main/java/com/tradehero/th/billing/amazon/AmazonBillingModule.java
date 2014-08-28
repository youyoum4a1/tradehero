package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THBillingRequest;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
                //IABSKUListRetrievedAsyncMilestone.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class AmazonBillingModule
{
    //<editor-fold desc="Actors and Action Holders">
    //@Provides THIABBillingAvailableTester provideBillingAvailableTest(THBaseIABBillingAvailableTester thBaseIABBillingAvailableTester)
    //{
    //    return thBaseIABBillingAvailableTester;
    //}
    //
    //@Provides THIABBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseIABBillingAvailableTesterHolder thBaseIABBillingAvailableTesterHolder)
    //{
    //    return thBaseIABBillingAvailableTesterHolder;
    //}
    //
    //@Provides THIABProductIdentifierFetcher provideProductIdentifierFetcher(THBaseIABProductIdentifierFetcher thBaseIABProductIdentifierFetcher)
    //{
    //    return thBaseIABProductIdentifierFetcher;
    //}
    //
    //@Provides THIABProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseIABProductIdentifierFetcherHolder thBaseIABProductIdentifierFetcherHolder)
    //{
    //    return thBaseIABProductIdentifierFetcherHolder;
    //}
    //
    //@Provides THIABInventoryFetcher provideInventoryFetcher(THBaseIABInventoryFetcher thBaseIABInventoryFetcher)
    //{
    //    return thBaseIABInventoryFetcher;
    //}
    //
    //@Provides THIABInventoryFetcherHolder provideInventoryFetcherHolder(THBaseIABInventoryFetcherHolder thBaseIABInventoryFetcherHolder)
    //{
    //    return thBaseIABInventoryFetcherHolder;
    //}
    //
    //@Provides THIABPurchaseFetcher providePurchaseFetcher(THBaseIABPurchaseFetcher thBaseIABPurchaseFetcher)
    //{
    //    return thBaseIABPurchaseFetcher;
    //}
    //
    //@Provides THIABPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseIABPurchaseFetcherHolder thBaseIABPurchaseFetcherHolder)
    //{
    //    return thBaseIABPurchaseFetcherHolder;
    //}
    //
    //@Provides THIABPurchaser providePurchaser(THBaseIABPurchaser thBaseIABPurchaser)
    //{
    //    return thBaseIABPurchaser;
    //}
    //
    //@Provides THIABPurchaserHolder providePurchaserHolder(THBaseIABPurchaserHolder thBaseIABPurchaserHolder)
    //{
    //    return thBaseIABPurchaserHolder;
    //}
    //
    //@Provides THIABPurchaseReporter providePurchaseReporter(THBaseIABPurchaseReporter thBaseIABPurchaseReporter)
    //{
    //    return thBaseIABPurchaseReporter;
    //}
    //
    //@Provides THIABPurchaseReporterHolder providePurchaseReporterHolder(THBaseIABPurchaseReporterHolder thBaseIABPurchaseReporterHolder)
    //{
    //    return thBaseIABPurchaseReporterHolder;
    //}
    //
    //@Provides THIABPurchaseConsumer providePurchaseConsumer(THBaseIABPurchaseConsumer thBaseIABPurchaseConsumer)
    //{
    //    return thBaseIABPurchaseConsumer;
    //}
    //
    //@Provides THIABPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseIABPurchaseConsumerHolder thBaseIABPurchaseConsumerHolder)
    //{
    //    return thBaseIABPurchaseConsumerHolder;
    //}
    //</editor-fold>

    //@Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
    //{
    //    return THIABAlertDialogUtil;
    //}

    //<editor-fold desc="Caches">
    //@Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(IABSKUListCache iabskuListCache)
    //{
    //    return iabskuListCache;
    //}
    //
    //@Provides @Singleton ProductDetailCache provideProductDetailCache(THIABProductDetailCache productDetailCache)
    //{
    //    return productDetailCache;
    //}
    //
    //@Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(IABPurchaseCache purchaseCache)
    //{
    //    return purchaseCache;
    //}
    //
    //@Provides @Singleton IABPurchaseCache provideIABPurchaseCache(THIABPurchaseCache purchaseCache)
    //{
    //    return purchaseCache;
    //}
    //</editor-fold>

    //@Provides BillingExceptionFactory provideBillingExceptionFactory(IABExceptionFactory exceptionFactory)
    //{
    //    return exceptionFactory;
    //}
    //
    //@Provides IABExceptionFactory provideIABExceptionFactory(THIABExceptionFactory exceptionFactory)
    //{
    //    return exceptionFactory;
    //}
    //
    //@Provides @Singleton THBillingLogicHolder provideTHBillingActor(THIABLogicHolder logicHolder)
    //{
    //    return logicHolder;
    //}
    //
    //@Provides @Singleton THIABLogicHolder provideTHIABLogicHolder(THIABLogicHolderFull thiabLogicHolderFull)
    //{
    //    return thiabLogicHolderFull;
    //}
    //
    //@Provides @Singleton THBillingInteractor provideTHBillingInteractor(THIABInteractor thiabInteractor)
    //{
    //    return thiabInteractor;
    //}
    //
    //@Provides @Singleton THIABInteractor provideTHIABInteractor(THIABBillingInteractor thiabInteractor)
    //{
    //    return thiabInteractor;
    //}
    //
    //@Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    //{
    //    return THIABBillingRequestFull.builder();
    //}
    //
    //@Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestTestAvailableBuilder()
    //{
    //    return BaseTHUIIABRequest.builder();
    //}
}
