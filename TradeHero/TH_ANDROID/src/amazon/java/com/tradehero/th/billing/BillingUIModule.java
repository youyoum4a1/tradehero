package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.th.billing.amazon.THAmazonAlertDialogUtil;
import com.tradehero.th.billing.amazon.THAmazonInteractor;
import com.tradehero.th.billing.amazon.THBaseAmazonInteractor;
import com.tradehero.th.billing.amazon.request.BaseTHUIAmazonRequest;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
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
    @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THAmazonAlertDialogUtil thAmazonAlertDialogUtil)
    {
        return thAmazonAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THAmazonInteractor thAmazonInteractor)
    {
        return thAmazonInteractor;
    }

    @Provides @Singleton THAmazonInteractor provideTHIABInteractor(THBaseAmazonInteractor thBaseAmazonInteractor)
    {
        return thBaseAmazonInteractor;
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
    {
        return BaseTHUIAmazonRequest.builder();
    }
}
