package com.tradehero.th.billing;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THBillingLogicHolder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.billing.samsung.ForSamsungBillingMode;
import com.tradehero.th.billing.samsung.ProcessingPurchase;
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
import com.tradehero.th.billing.samsung.THSamsungConstants;
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
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCache;
import com.tradehero.th.billing.samsung.request.BaseTHUISamsungRequest;
import com.tradehero.th.billing.samsung.request.THSamsungRequestFull;
import com.tradehero.th.persistence.billing.samsung.SamsungSKUListCache;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCache;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
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
public class BillingModule
{
    public static final String PREF_PROCESSING_PURCHASES = "SAMSUNG_PROCESSING_PURCHASES";

    @Provides @ForSamsungBillingMode
    int provideSamsungBillingMode()
    {
        return THSamsungConstants.PURCHASE_MODE;
    }

    //<editor-fold desc="Actors and Action Holders">
    @Provides
    THSamsungBillingAvailableTester provideBillingAvailableTest(THBaseSamsungBillingAvailableTester thBaseSamsungBillingAvailableTester)
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

    @Provides
    THSamsungInventoryFetcher provideInventoryFetcher(THBaseSamsungInventoryFetcher thBaseSamsungInventoryFetcher)
    {
        return thBaseSamsungInventoryFetcher;
    }

    @Provides
    THSamsungInventoryFetcherHolder provideInventoryFetcherHolder(THBaseSamsungInventoryFetcherHolder thBaseSamsungInventoryFetcherHolder)
    {
        return thBaseSamsungInventoryFetcherHolder;
    }

    @Provides
    THSamsungPurchaseFetcher providePurchaseFetcher(THBaseSamsungPurchaseFetcher thBaseSamsungPurchaseFetcher)
    {
        return thBaseSamsungPurchaseFetcher;
    }

    @Provides
    THSamsungPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseSamsungPurchaseFetcherHolder thBaseSamsungPurchaseFetcherHolder)
    {
        return thBaseSamsungPurchaseFetcherHolder;
    }

    @Provides
    THSamsungPurchaser providePurchaser(THBaseSamsungPurchaser thBaseSamsungPurchaser)
    {
        return thBaseSamsungPurchaser;
    }

    @Provides
    THSamsungPurchaserHolder providePurchaserHolder(THBaseSamsungPurchaserHolder thBaseSamsungPurchaserHolder)
    {
        return thBaseSamsungPurchaserHolder;
    }

    @Provides
    THSamsungPurchaseReporter providePurchaseReporter(THBaseSamsungPurchaseReporter thBaseSamsungPurchaseReporter)
    {
        return thBaseSamsungPurchaseReporter;
    }

    @Provides
    THSamsungPurchaseReporterHolder providePurchaseReporterHolder(THBaseSamsungPurchaseReporterHolder thBaseSamsungPurchaseReporterHolder)
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

    @Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    {
        return THSamsungRequestFull.builder();
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestTestAvailableBuilder()
    {
        return BaseTHUISamsungRequest.builder();
    }

    @Provides @Singleton @ProcessingPurchase
    StringSetPreference provideProcessingPurchasePreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringSetPreference(sharedPreferences, PREF_PROCESSING_PURCHASES, new HashSet<String>());
    }
}
