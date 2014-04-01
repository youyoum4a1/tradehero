package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
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

/**
 * Created by xavier on 2/11/14.
 */
@Module(
        injects = {
                THBaseSamsungBillingAvailableTester.class,
                THBaseSamsungBillingAvailableTesterHolder.class,
                THBaseSamsungProductIdentifierFetcher.class,
                THBaseSamsungProductIdentifierFetcherHolder.class,
                THBaseSamsungInventoryFetcher.class,
                THBaseSamsungInventoryFetcherHolder.class,
                THBaseSamsungPurchaser.class,
                THBaseSamsungPurchaserHolder.class,
                THBaseSamsungPurchaseReporter.class,
                THBaseSamsungPurchaseReporterHolder.class,
                THBaseSamsungPurchaseFetcher.class,
                THBaseSamsungPurchaseFetcherHolder.class,
                THSamsungBillingInteractor.class,
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class THSamsungModule
{
    public static final String TAG = THSamsungModule.class.getSimpleName();

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THSamsungAlertDialogUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(SamsungSKUListCache samsungSkuListCache)
    {
        return samsungSkuListCache;
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

    @Provides @Singleton THSamsungLogicHolder provideTHSamsungLogicHolder(THSamsungLogicHolderFull thiabLogicHolderFull)
    {
        return thiabLogicHolderFull;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides THBillingInteractor provideTHBillingInteractor(THSamsungBillingInteractor thiabInteractor)
    {
        return thiabInteractor;
    }

    @Provides THBillingRequest provideBillingRequest(THSamsungRequestFull request)
    {
        return request;
    }

    @Provides THUIBillingRequest provideUIBillingRequest(THUISamsungRequest request)
    {
        return request;
    }
}
