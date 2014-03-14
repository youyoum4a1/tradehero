package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.billing.THBillingRequest;
import com.tradehero.th.persistence.billing.ProductDetailCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by xavier on 2/11/14.
 */
@Module(
        injects = {
        },
        staticInjections = {
        },
        complete = false,
        library = true
)
public class THIABModule
{
    public static final String TAG = THIABModule.class.getSimpleName();

    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
    {
        return THIABAlertDialogUtil;
    }

    @Provides @Singleton ProductDetailCache provideProductDetailCache(THIABProductDetailCache productDetailCache)
    {
        return productDetailCache;
    }

    @Provides @Singleton BillingLogicHolder provideBillingActor(THIABLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THIABLogicHolder provideTHIABLogicHolder(THIABLogicHolderFull thiabLogicHolderFull)
    {
        return thiabLogicHolderFull;
    }

    @Provides THBillingInteractor provideTHBillingInteractor(THIABUserInteractor thiabUserInteractor)
    {
        return thiabUserInteractor;
    }

    @Provides THIABUserInteractor provideTHIABUserInteractor()
    {
        return new THIABUserInteractor();
    }

    @Provides THBillingRequest.THBuilder provideTHBillingRequestBuilder(THIABBillingRequestFull.THIABBuilder builder)
    {
        return builder;
    }
}
