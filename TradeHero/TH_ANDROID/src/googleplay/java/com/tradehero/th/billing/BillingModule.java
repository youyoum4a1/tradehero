package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCacheRx;
import com.tradehero.th.billing.googleplay.THIABSecurityAlertKnowledge;
import com.tradehero.th.billing.googleplay.exception.THIABExceptionFactory;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCacheRx;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
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

    @Provides SecurityAlertKnowledge provideSecurityAlertKnowledge(THIABSecurityAlertKnowledge thiabSecurityAlertKnowledge)
    {
        return thiabSecurityAlertKnowledge;
    }
}
