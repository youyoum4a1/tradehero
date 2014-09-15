package com.tradehero.th.billing;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.ProductPurchaseCache;
import com.tradehero.common.billing.amazon.AmazonPurchaseCache;
import com.tradehero.common.billing.amazon.exception.AmazonExceptionFactory;
import com.tradehero.common.billing.exception.BillingExceptionFactory;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.th.billing.amazon.ProcessingPurchase;
import com.tradehero.th.billing.amazon.THAmazonSecurityAlertKnowledge;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import com.tradehero.th.billing.amazon.request.THAmazonRequestFull;
import com.tradehero.th.billing.request.THBillingRequest;
import com.tradehero.th.persistence.billing.AmazonSKUListCache;
import com.tradehero.th.persistence.billing.THAmazonProductDetailCache;
import com.tradehero.th.persistence.billing.THAmazonPurchaseCache;
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
    @Provides @Singleton ProductIdentifierListCache provideProductIdentifierListCache(AmazonSKUListCache amazonSkuListCache)
    {
        return amazonSkuListCache;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THAmazonProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton ProductPurchaseCache provideProductPurchaseCache(AmazonPurchaseCache purchaseCache)
    {
        return purchaseCache;
    }

    @Provides @Singleton AmazonPurchaseCache provideIABPurchaseCache(THAmazonPurchaseCache purchaseCache)
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

    @Provides SecurityAlertKnowledge provideSecurityAlertKnowledge(THAmazonSecurityAlertKnowledge thAmazonSecurityAlertKnowledge)
    {
        return thAmazonSecurityAlertKnowledge;
    }

    @Provides THBillingRequest.Builder provideTHBillingRequestBuilder()
    {
        return THAmazonRequestFull.builder();
    }

    @Provides @Singleton @ProcessingPurchase StringSetPreference provideProcessingPurchasePreference(@ForApp SharedPreferences sharedPreferences)
    {
        return new StringSetPreference(sharedPreferences, PREF_PROCESSING_PURCHASES, new HashSet<String>());
    }
}
