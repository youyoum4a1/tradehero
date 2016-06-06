package com.androidth.general.billing;

import com.androidth.general.common.billing.ProductDetailCacheRx;
import com.androidth.general.common.billing.ProductIdentifierListCacheRx;
import com.androidth.general.common.billing.ProductPurchaseCacheRx;
import com.androidth.general.common.billing.exception.BillingExceptionFactory;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.persistence.billing.googleplay.IABPurchaseCacheRx;
import com.androidth.general.billing.googleplay.exception.THIABExceptionFactory;
import com.androidth.general.persistence.billing.googleplay.IABSKUListCacheRx;
import com.androidth.general.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.androidth.general.persistence.billing.googleplay.THIABPurchaseCacheRx;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        injects = {
        },
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
    //<editor-fold desc="Caches">
    @Provides @Singleton ProductIdentifierListCacheRx provideProductIdentifierListCacheRx(IABSKUListCacheRx iabskuListCache)
    {
        return iabskuListCache;
    }

    @Provides @Singleton ProductDetailCacheRx provideProductDetailCacheRx(THIABProductDetailCacheRx productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCacheRx provideProductPurchaseCacheRx(IABPurchaseCacheRx purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton IABPurchaseCacheRx provideIABPurchaseCacheRx(THIABPurchaseCacheRx purchaseCache)
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
}
