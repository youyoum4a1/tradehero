package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.samsung.THSamsungAlertDialogUtil;
import com.tradehero.th.billing.samsung.THSamsungBillingInteractor;
import com.tradehero.th.billing.samsung.THSamsungInteractor;
import com.tradehero.th.billing.samsung.request.BaseTHUISamsungRequest;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingUIModule
{
    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THSamsungAlertDialogUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THSamsungInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides @Singleton THSamsungInteractor provideTHSamsungInteractor(THSamsungBillingInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
    {
        return BaseTHUISamsungRequest.builder();
    }
}
