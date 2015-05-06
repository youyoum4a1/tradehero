package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCacheRx;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCacheRx;
import com.tradehero.th.persistence.billing.samsung.SamsungSKUListCacheRx;
import com.tradehero.th.persistence.billing.samsung.THSamsungProductDetailCacheRx;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
    @Provides @SamsungBillingMode
    int provideSamsungBillingMode()
    {
        return THSamsungConstants.PURCHASE_MODE;
    }

    //<editor-fold desc="Caches">
    @Provides @Singleton ProductIdentifierListCacheRx provideProductIdentifierListCacheRx(SamsungSKUListCacheRx samsungSKUListCache)
    {
        return samsungSKUListCache;
    }

    @Provides @Singleton ProductDetailCacheRx provideProductDetailCacheRx(THSamsungProductDetailCacheRx productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCacheRx provideProductPurchaseCacheRx(SamsungPurchaseCacheRx purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton SamsungPurchaseCacheRx provideSamsungPurchaseCacheRx(THSamsungPurchaseCacheRx purchaseCache)
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
}
