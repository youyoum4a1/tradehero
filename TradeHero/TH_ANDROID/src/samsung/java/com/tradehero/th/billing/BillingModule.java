bpackage com.androidth.general.billing;

import com.androidth.general.common.billing.ProductDetailCacheRx;
import com.androidth.general.common.billing.ProductIdentifierListCacheRx;
import com.androidth.general.common.billing.ProductPurchaseCacheRx;
import com.androidth.general.common.billing.exception.BillingExceptionFactory;
import com.androidth.general.common.billing.samsung.SamsungBillingMode;
import com.androidth.general.common.billing.samsung.exception.SamsungExceptionFactory;
import com.androidth.general.common.billing.samsung.persistence.SamsungPurchaseCacheRx;
import com.androidth.general.billing.samsung.THSamsungConstants;
import com.androidth.general.billing.samsung.exception.THSamsungExceptionFactory;
import com.androidth.general.billing.samsung.persistence.THSamsungPurchaseCacheRx;
import com.androidth.general.persistence.billing.samsung.SamsungSKUListCacheRx;
import com.androidth.general.persistence.billing.samsung.THSamsungProductDetailCacheRx;
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
