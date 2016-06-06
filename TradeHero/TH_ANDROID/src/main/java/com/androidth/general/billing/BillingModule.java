package com.androidth.general.billing;
//
//import android.content.SharedPreferences;
//import com.androidth.general.common.annotation.ForApp;
//import com.androidth.general.common.billing.ProductDetailCacheRx;
//import com.androidth.general.common.billing.ProductIdentifierListCacheRx;
//import com.androidth.general.common.billing.ProductPurchaseCacheRx;
//import com.androidth.general.common.billing.amazon.AmazonPurchaseCacheRx;
//import com.androidth.general.common.billing.amazon.exception.AmazonExceptionFactory;
//import com.androidth.general.common.billing.exception.BillingExceptionFactory;
//import com.androidth.general.common.persistence.prefs.StringSetPreference;
//import com.androidth.general.billing.amazon.ProcessingPurchase;
//import com.androidth.general.billing.amazon.exception.THAmazonExceptionFactory;
//import com.tradehero.th.persistence.billing.AmazonSKUListCacheRx;
//import com.tradehero.th.persistence.billing.THAmazonProductDetailCacheRx;
//import com.tradehero.th.persistence.billing.THAmazonPurchaseCacheRx;
import dagger.Module;
//import dagger.Provides;
//import java.util.HashSet;
//import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingModule
{
//    public static final String PREF_PROCESSING_PURCHASES = "AMAZON_PROCESSING_PURCHASES";
//
//    //<editor-fold desc="Caches">
//    @Provides @Singleton ProductIdentifierListCacheRx provideProductIdentifierListCacheRx(AmazonSKUListCacheRx amazonSkuListCache)
//    {
//        return amazonSkuListCache;
//    }
//
//    @Provides @Singleton ProductDetailCacheRx provideProductDetailCacheRx(THAmazonProductDetailCacheRx productDetailCache)
//    {
//        return productDetailCache;
//    }
//
//    @Provides @Singleton ProductPurchaseCacheRx provideProductPurchaseCacheRx(AmazonPurchaseCacheRx purchaseCache)
//    {
//        return purchaseCache;
//    }
//
//    @Provides @Singleton AmazonPurchaseCacheRx provideIABPurchaseCacheRx(THAmazonPurchaseCacheRx purchaseCache)
//    {
//        return purchaseCache;
//    }
//    //</editor-fold>
//
//    @Provides BillingExceptionFactory provideBillingExceptionFactory(AmazonExceptionFactory exceptionFactory)
//    {
//        return exceptionFactory;
//    }
//
//    @Provides AmazonExceptionFactory provideAmazonExceptionFactory(THAmazonExceptionFactory exceptionFactory)
//    {
//        return exceptionFactory;
//    }
//
//    @Provides @Singleton @ProcessingPurchase StringSetPreference provideProcessingPurchasePreference(@ForApp SharedPreferences sharedPreferences)
//    {
//        return new StringSetPreference(sharedPreferences, PREF_PROCESSING_PURCHASES, new HashSet<String>());
//    }
}
