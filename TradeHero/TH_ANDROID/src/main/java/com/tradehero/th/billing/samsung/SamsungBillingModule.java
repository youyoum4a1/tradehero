package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.request.BillingRequest;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.request.UISamsungRequest;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCache;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.billing.samsung.request.THUISamsungRequest;
import com.tradehero.th.persistence.billing.samsung.SamsungSKUListCache;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THSamsungAlertDialogUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    //<editor-fold desc="Caches">
    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(SamsungSKUListCache samsungSKUListCache)
    {
        return samsungSKUListCache;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THSamsungProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(SamsungPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton SamsungPurchaseCache provideSamsungPurchaseCache(THSamsungPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }
    //</editor-fold>

    @Provides BillingExceptionFactory provideBillingExceptionFactory(SamsungExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides SamsungExceptionFactory provideSamsungExceptionFactory(THSamsungExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

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

    @Provides BillingRequest provideBillingRequest(THBillingRequest request)
    {
        return request;
    }

    @Provides THBillingRequest provideTHBillingRequest(THSamsungRequestFull request)
    {
        return request;
    }

    @Provides UIBillingRequest provideUIBillingRequest(THUIBillingRequest request)
    {
        return request;
    }

    @Provides UISamsungRequest provideUISamsungBillingRequest(THUISamsungRequest request)
    {
        return request;
    }

    @Provides THUIBillingRequest provideTHUIBillingRequest(THUISamsungRequest request)
    {
        return request;
    }
}
