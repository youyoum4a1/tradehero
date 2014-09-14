package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABInteractor;
import com.tradehero.th.billing.googleplay.request.BaseTHUIIABRequest;
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
  @Provides BillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
  {
    return THIABAlertDialogUtil;
  }

  @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
  {
    return billingInteractor;
  }

  @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THIABInteractor thiabInteractor)
  {
    return thiabInteractor;
  }

  @Provides @Singleton THIABInteractor provideTHIABInteractor(THIABBillingInteractor thiabInteractor)
  {
    return thiabInteractor;
  }

  @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
  {
    return BaseTHUIIABRequest.builder();
  }
}
