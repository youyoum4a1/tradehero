package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.samsung.THBaseSamsungBillingAvailableTester;
import com.tradehero.th.billing.samsung.THBaseSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungInventoryFetcher;
import com.tradehero.th.billing.samsung.THBaseSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungProductIdentifierFetcher;
import com.tradehero.th.billing.samsung.THBaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseFetcher;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseReporter;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaser;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.THSamsungAlertDialogUtil;
import com.tradehero.th.billing.samsung.THSamsungBillingAvailableTester;
import com.tradehero.th.billing.samsung.THSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THSamsungBillingInteractor;
import com.tradehero.th.billing.samsung.THSamsungInteractor;
import com.tradehero.th.billing.samsung.THSamsungInventoryFetcher;
import com.tradehero.th.billing.samsung.THSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolderFull;
import com.tradehero.th.billing.samsung.THSamsungProductIdentifierFetcher;
import com.tradehero.th.billing.samsung.THSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseFetcher;
import com.tradehero.th.billing.samsung.THSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseReporter;
import com.tradehero.th.billing.samsung.THSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaser;
import com.tradehero.th.billing.samsung.THSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.request.BaseTHUISamsungRequest;
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
    @Provides THSamsungBillingAvailableTester provideBillingAvailableTest(THBaseSamsungBillingAvailableTester thBaseSamsungBillingAvailableTester)
    {
        return thBaseSamsungBillingAvailableTester;
    }

    @Provides
    THSamsungBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseSamsungBillingAvailableTesterHolder thBaseSamsungBillingAvailableTesterHolder)
    {
        return thBaseSamsungBillingAvailableTesterHolder;
    }

    @Provides
    THSamsungProductIdentifierFetcher provideProductIdentifierFetcher(THBaseSamsungProductIdentifierFetcher thBaseSamsungProductIdentifierFetcher)
    {
        return thBaseSamsungProductIdentifierFetcher;
    }

    @Provides
    THSamsungProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseSamsungProductIdentifierFetcherHolder thBaseSamsungProductIdentifierFetcherHolder)
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

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THSamsungLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THSamsungLogicHolder provideTHSamsungLogicHolder(THSamsungLogicHolderFull thSamsungLogicHolderFull)
    {
        return thSamsungLogicHolderFull;
    }

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THSamsungAlertDialogUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THSamsungInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides @Singleton THSamsungInteractor provideTHSamsungInteractor(THSamsungBillingInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
    {
        return BaseTHUISamsungRequest.builder();
    }
}
