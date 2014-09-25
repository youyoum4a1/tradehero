package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.persistence.billing.googleplay.IABPurchaseCache;
import com.tradehero.th.billing.googleplay.THIABSecurityAlertKnowledge;
import com.tradehero.th.billing.googleplay.exception.THIABExceptionFactory;
import com.tradehero.th.billing.googleplay.request.THIABBillingRequestFull;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCache;
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
    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(IABSKUListCache iabskuListCache)
    {
        return iabskuListCache;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THIABProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(IABPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton IABPurchaseCache provideIABPurchaseCache(THIABPurchaseCache purchaseCache)
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

    @Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    {
        return THIABBillingRequestFull.builder();
    }
}
