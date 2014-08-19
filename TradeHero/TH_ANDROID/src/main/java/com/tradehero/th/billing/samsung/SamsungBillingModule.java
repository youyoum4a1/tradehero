package com.tradehero.th.billing.samsung;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
        },
        staticInjections = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class SamsungBillingModule
{
    @Provides @ForSamsungBillingMode int provideSamsungBillingMode()
    {
        return THSamsungConstants.PURCHASE_MODE;
    }

    //<editor-fold desc="Actors and Action Holders">
    @Provides THSamsungBillingAvailableTester provideBillingAvailableTest(THBaseSamsungBillingAvailableTester thBaseSamsungBillingAvailableTester)
    {
        return thBaseSamsungBillingAvailableTester;
    }

    @Provides THSamsungBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseSamsungBillingAvailableTesterHolder thBaseSamsungBillingAvailableTesterHolder)
    {
        return thBaseSamsungBillingAvailableTesterHolder;
    }

    @Provides THSamsungProductIdentifierFetcher provideProductIdentifierFetcher(THBaseSamsungProductIdentifierFetcher thBaseSamsungProductIdentifierFetcher)
    {
        return thBaseSamsungProductIdentifierFetcher;
    }

    @Provides THSamsungProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseSamsungProductIdentifierFetcherHolder thBaseSamsungProductIdentifierFetcherHolder)
    {
        return thBaseSamsungProductIdentifierFetcherHolder;
    }

    @Provides THSamsungInventoryFetcher provideInventoryFetcher(THBaseSamsungInventoryFetcher thBaseSamsungInventoryFetcher)
    {
        return thBaseSamsungInventoryFetcher;
    }

    @Provides THSamsungInventoryFetcherHolder provideInventoryFetcherHolder(THBaseSamsungInventoryFetcherHolder thBaseSamsungInventoryFetcherHolder)
    {
        return thBaseSamsungInventoryFetcherHolder;
    }

    @Provides THSamsungPurchaseFetcher providePurchaseFetcher(THBaseSamsungPurchaseFetcher thBaseSamsungPurchaseFetcher)
    {
        return thBaseSamsungPurchaseFetcher;
    }

    @Provides THSamsungPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseSamsungPurchaseFetcherHolder thBaseSamsungPurchaseFetcherHolder)
    {
        return thBaseSamsungPurchaseFetcherHolder;
    }

    @Provides THSamsungPurchaser providePurchaser(THBaseSamsungPurchaser thBaseSamsungPurchaser)
    {
        return thBaseSamsungPurchaser;
    }

    @Provides THSamsungPurchaserHolder providePurchaserHolder(THBaseSamsungPurchaserHolder thBaseSamsungPurchaserHolder)
    {
        return thBaseSamsungPurchaserHolder;
    }

    @Provides THSamsungPurchaseReporter providePurchaseReporter(THBaseSamsungPurchaseReporter thBaseSamsungPurchaseReporter)
    {
        return thBaseSamsungPurchaseReporter;
    }

    @Provides THSamsungPurchaseReporterHolder providePurchaseReporterHolder(THBaseSamsungPurchaseReporterHolder thBaseSamsungPurchaseReporterHolder)
    {
        return thBaseSamsungPurchaseReporterHolder;
    }
    //</editor-fold>
}
