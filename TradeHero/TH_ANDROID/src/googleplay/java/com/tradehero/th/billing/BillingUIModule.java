package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.googleplay.THBaseIABBillingAvailableTesterHolder;
import com.tradehero.th.billing.googleplay.THBaseIABInventoryFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABProductIdentifierFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseConsumerHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseReporterHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaserHolder;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABBillingAvailableTesterHolder;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABInteractor;
import com.tradehero.th.billing.googleplay.THIABInventoryFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABLogicHolderFull;
import com.tradehero.th.billing.googleplay.THIABProductIdentifierFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumerHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcherHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporterHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaserHolder;
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
    //<editor-fold desc="Actors and Action Holders">
    @Provides THIABBillingAvailableTesterHolder provideBillingAvailableTesterHolder(
            THBaseIABBillingAvailableTesterHolder thBaseIABBillingAvailableTesterHolder)
    {
        return thBaseIABBillingAvailableTesterHolder;
    }

    @Provides THIABProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(
            THBaseIABProductIdentifierFetcherHolder thBaseIABProductIdentifierFetcherHolder)
    {
        return thBaseIABProductIdentifierFetcherHolder;
    }

    @Provides THIABInventoryFetcherHolder provideInventoryFetcherHolder(THBaseIABInventoryFetcherHolder thBaseIABInventoryFetcherHolder)
    {
        return thBaseIABInventoryFetcherHolder;
    }

    @Provides THIABPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseIABPurchaseFetcherHolder thBaseIABPurchaseFetcherHolder)
    {
        return thBaseIABPurchaseFetcherHolder;
    }

    @Provides THIABPurchaserHolder providePurchaserHolder(THBaseIABPurchaserHolder thBaseIABPurchaserHolder)
    {
        return thBaseIABPurchaserHolder;
    }

    @Provides THIABPurchaseReporterHolder providePurchaseReporterHolder(THBaseIABPurchaseReporterHolder thBaseIABPurchaseReporterHolder)
    {
        return thBaseIABPurchaseReporterHolder;
    }

    @Provides THIABPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseIABPurchaseConsumerHolder thBaseIABPurchaseConsumerHolder)
    {
        return thBaseIABPurchaseConsumerHolder;
    }
    //</editor-fold>

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THIABLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THIABLogicHolder provideTHIABLogicHolder(THIABLogicHolderFull thiabLogicHolderFull)
    {
        return thiabLogicHolderFull;
    }

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
