package com.tradehero.th.billing;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.amazon.AmazonPurchaseCache;
import com.tradehero.common.billing.amazon.exception.AmazonExceptionFactory;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.amazon.ProcessingPurchase;
import com.tradehero.th.billing.amazon.THAmazonBillingAvailableTester;
import com.tradehero.th.billing.amazon.THAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THAmazonInventoryFetcher;
import com.tradehero.th.billing.amazon.THAmazonInventoryFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonLogicHolder;
import com.tradehero.th.billing.amazon.THAmazonLogicHolderFull;
import com.tradehero.th.billing.amazon.THAmazonProductIdentifierFetcher;
import com.tradehero.th.billing.amazon.THAmazonProductIdentifierFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseConsumer;
import com.tradehero.th.billing.amazon.THAmazonPurchaseConsumerHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseFetcher;
import com.tradehero.th.billing.amazon.THAmazonPurchaseFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseReporter;
import com.tradehero.th.billing.amazon.THAmazonPurchaseReporterHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaser;
import com.tradehero.th.billing.amazon.THAmazonPurchaserHolder;
import com.tradehero.th.billing.amazon.THAmazonSecurityAlertKnowledge;
import com.tradehero.th.billing.amazon.THBaseAmazonBillingAvailableTester;
import com.tradehero.th.billing.amazon.THBaseAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonInventoryFetcher;
import com.tradehero.th.billing.amazon.THBaseAmazonInventoryFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonProductIdentifierFetcher;
import com.tradehero.th.billing.amazon.THBaseAmazonProductIdentifierFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseConsumer;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseConsumerHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseFetcher;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseReporter;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseReporterHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaser;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaserHolder;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.persistence.billing.AmazonSKUListCache;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCache;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCache;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
    public static final String PREF_PROCESSING_PURCHASES = "AMAZON_PROCESSING_PURCHASES";

    //<editor-fold desc="Caches">
    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(AmazonSKUListCache amazonSkuListCache)
    {
        return amazonSkuListCache;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THAmazonProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(AmazonPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton AmazonPurchaseCache provideIABPurchaseCache(THAmazonPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }
    //</editor-fold>

    @Provides BillingExceptionFactory provideBillingExceptionFactory(AmazonExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides AmazonExceptionFactory provideAmazonExceptionFactory(THAmazonExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    //<editor-fold desc="Actors and Action Holders">
    @Provides THAmazonBillingAvailableTester provideBillingAvailableTest(THBaseAmazonBillingAvailableTester thBaseAmazonBillingAvailableTester)
    {
        return thBaseAmazonBillingAvailableTester;
    }

    @Provides THAmazonBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseAmazonBillingAvailableTesterHolder thBaseAmazonBillingAvailableTesterHolder)
    {
        return thBaseAmazonBillingAvailableTesterHolder;
    }

    @Provides THAmazonProductIdentifierFetcher provideProductIdentifierFetcher(THBaseAmazonProductIdentifierFetcher thBaseAmazonProductIdentifierFetcher)
    {
        return thBaseAmazonProductIdentifierFetcher;
    }

    @Provides THAmazonProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseAmazonProductIdentifierFetcherHolder thBaseAmazonProductIdentifierFetcherHolder)
    {
        return thBaseAmazonProductIdentifierFetcherHolder;
    }

    @Provides THAmazonInventoryFetcher provideInventoryFetcher(THBaseAmazonInventoryFetcher thBaseIABInventoryFetcher)
    {
        return thBaseIABInventoryFetcher;
    }

    @Provides THAmazonInventoryFetcherHolder provideInventoryFetcherHolder(THBaseAmazonInventoryFetcherHolder thBaseAmazonInventoryFetcherHolder)
    {
        return thBaseAmazonInventoryFetcherHolder;
    }

    @Provides THAmazonPurchaseFetcher providePurchaseFetcher(THBaseAmazonPurchaseFetcher thBaseAmazonPurchaseFetcher)
    {
        return thBaseAmazonPurchaseFetcher;
    }

    @Provides THAmazonPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseAmazonPurchaseFetcherHolder thBaseAmazonPurchaseFetcherHolder)
    {
        return thBaseAmazonPurchaseFetcherHolder;
    }

    @Provides THAmazonPurchaser providePurchaser(THBaseAmazonPurchaser thBaseAmazonPurchaser)
    {
        return thBaseAmazonPurchaser;
    }

    @Provides THAmazonPurchaserHolder providePurchaserHolder(THBaseAmazonPurchaserHolder thBaseAmazonPurchaserHolder)
    {
        return thBaseAmazonPurchaserHolder;
    }

    @Provides THAmazonPurchaseReporter providePurchaseReporter(THBaseAmazonPurchaseReporter thBaseAmazonPurchaseReporter)
    {
        return thBaseAmazonPurchaseReporter;
    }

    @Provides THAmazonPurchaseReporterHolder providePurchaseReporterHolder(THBaseAmazonPurchaseReporterHolder thBaseAmazonPurchaseReporterHolder)
    {
        return thBaseAmazonPurchaseReporterHolder;
    }

    @Provides THAmazonPurchaseConsumer providePurchaseConsumer(THBaseAmazonPurchaseConsumer thBaseAmazonPurchaseConsumer)
    {
        return thBaseAmazonPurchaseConsumer;
    }

    @Provides THAmazonPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseAmazonPurchaseConsumerHolder thBaseAmazonPurchaseConsumerHolder)
    {
        return thBaseAmazonPurchaseConsumerHolder;
    }
    //</editor-fold>

    @Provides SecurityAlertKnowledge provideSecurityAlertKnowledge(THAmazonSecurityAlertKnowledge thAmazonSecurityAlertKnowledge)
    {
        return thAmazonSecurityAlertKnowledge;
    }

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THAmazonLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THAmazonLogicHolder provideTHIABLogicHolder(THAmazonLogicHolderFull thAmazonLogicHolderFull)
    {
        return thAmazonLogicHolderFull;
    }

    @Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    {
        return THAmazonRequestFull.builder();
    }

    @Provides @Singleton @ProcessingPurchase StringSetPreference provideProcessingPurchasePreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringSetPreference(sharedPreferences, PREF_PROCESSING_PURCHASES, new HashSet<String>());
    }
}
