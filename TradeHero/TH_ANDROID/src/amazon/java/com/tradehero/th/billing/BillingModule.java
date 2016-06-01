package com.ayondo.academy.billing;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.billing.ProductDetailCacheRx;
import com.tradehero.common.billing.ProductIdentifierListCacheRx;
import com.tradehero.common.billing.ProductPurchaseCacheRx;
import com.tradehero.common.billing.amazon.AmazonPurchaseCacheRx;
import com.tradehero.common.billing.amazon.exception.AmazonExceptionFactory;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.ayondo.academy.billing.amazon.ProcessingPurchase;
import com.ayondo.academy.billing.amazon.exception.THAmazonExceptionFactory;
import com.ayondo.academy.persistence.billing.AmazonSKUListCacheRx;
import com.ayondo.academy.persistence.billing.THAmazonProductDetailCacheRx;
import com.ayondo.academy.persistence.billing.THAmazonPurchaseCacheRx;
import dagger.Module;
import dagger.Provides;
import java.util.HashSet;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
    public static final String PREF_PROCESSING_PURCHASES = "AMAZON_PROCESSING_PURCHASES";

    //<editor-fold desc="Caches">
    @Provides @Singleton ProductIdentifierListCacheRx provideProductIdentifierListCacheRx(AmazonSKUListCacheRx amazonSkuListCache)
    {
        return amazonSkuListCache;
    }

    @Provides @Singleton ProductDetailCacheRx provideProductDetailCacheRx(THAmazonProductDetailCacheRx productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCacheRx provideProductPurchaseCacheRx(AmazonPurchaseCacheRx purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton AmazonPurchaseCacheRx provideIABPurchaseCacheRx(THAmazonPurchaseCacheRx purchaseCache)
    {
        return purchaseCache;
    }
    //</editor-fold>

    @Provides BillingExceptionFactory provideBillingExceptionFactory(AmazonExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides AmazonExceptionFactory provideAmazonExceptionFactory(THAmazonExceptionFactory exceptionFactory)
    {
        return exceptionFactory;
    }

    @Provides @Singleton @ProcessingPurchase StringSetPreference provideProcessingPurchasePreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringSetPreference(sharedPreferences, PREF_PROCESSING_PURCHASES, new HashSet<String>());
    }
}
