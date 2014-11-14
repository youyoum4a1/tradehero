package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.amazon.AmazonAlertDialogUtil;
import com.tradehero.th.billing.amazon.THAmazonAlertDialogUtil;
import com.tradehero.th.billing.amazon.THAmazonBillingAvailableTester;
import com.tradehero.th.billing.amazon.THAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THAmazonInteractor;
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
import com.tradehero.th.billing.amazon.THBaseAmazonBillingAvailableTester;
import com.tradehero.th.billing.amazon.THBaseAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonInteractor;
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
import com.tradehero.th.billing.amazon.request.BaseTHUIAmazonRequest;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingUIModule
{
    //<editor-fold desc="Actors and Action Holders">
    @Provides THAmazonBillingAvailableTester provideBillingAvailableTest(THBaseAmazonBillingAvailableTester thBaseAmazonBillingAvailableTester)
    {
        return thBaseAmazonBillingAvailableTester;
    }

    @Provides
    THAmazonBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseAmazonBillingAvailableTesterHolder thBaseAmazonBillingAvailableTesterHolder)
    {
        return thBaseAmazonBillingAvailableTesterHolder;
    }

    @Provides
    THAmazonProductIdentifierFetcher provideProductIdentifierFetcher(THBaseAmazonProductIdentifierFetcher thBaseAmazonProductIdentifierFetcher)
    {
        return thBaseAmazonProductIdentifierFetcher;
    }

    @Provides
    THAmazonProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseAmazonProductIdentifierFetcherHolder thBaseAmazonProductIdentifierFetcherHolder)
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

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THAmazonAlertDialogUtil thAmazonAlertDialogUtil)
    {
        return thAmazonAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THAmazonInteractor thAmazonInteractor)
    {
        return thAmazonInteractor;
    }

    @Provides @Singleton THAmazonInteractor provideTHIABInteractor(THBaseAmazonInteractor thBaseAmazonInteractor)
    {
        return thBaseAmazonInteractor;
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
    {
        return BaseTHUIAmazonRequest.builder();
    }

    @Provides AmazonAlertDialogUtil provideAmazonAlertDialogUtil(THAmazonAlertDialogUtil dialogUtil)
    {
        return dialogUtil;
    }
}
