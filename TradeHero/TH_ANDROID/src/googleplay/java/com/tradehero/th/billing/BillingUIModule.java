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
import com.tradehero.th.billing.googleplay.consumer.THBaseIABPurchaseConsumerHolderRx;
import com.tradehero.th.billing.googleplay.consumer.THIABPurchaseConsumerHolderRx;
import com.tradehero.th.billing.googleplay.identifier.THBaseIABProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.googleplay.identifier.THIABProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.googleplay.inventory.THBaseIABInventoryFetcherHolderRx;
import com.tradehero.th.billing.googleplay.inventory.THIABInventoryFetcherHolderRx;
import com.tradehero.th.billing.googleplay.purchase.THBaseIABPurchaserHolderRx;
import com.tradehero.th.billing.googleplay.purchase.THIABPurchaserHolderRx;
import com.tradehero.th.billing.googleplay.purchasefetch.THBaseIABPurchaseFetcherHolderRx;
import com.tradehero.th.billing.googleplay.purchasefetch.THIABPurchaseFetcherHolderRx;
import com.tradehero.th.billing.googleplay.report.THBaseIABPurchaseReporterHolderRx;
import com.tradehero.th.billing.googleplay.report.THIABPurchaseReporterHolderRx;
import com.tradehero.th.billing.googleplay.request.BaseTHUIIABRequest;
import com.tradehero.th.billing.googleplay.tester.THBaseIABBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
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
    //<editor-fold desc="Action Holders">
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

    //<editor-fold desc="Action Holders Rx">
    @Provides THIABBillingAvailableTesterHolderRx provideBillingAvailableTesterHolderRx(
            THBaseIABBillingAvailableTesterHolderRx thBaseIABBillingAvailableTesterHolder)
    {
        return thBaseIABBillingAvailableTesterHolder;
    }

    @Provides THIABProductIdentifierFetcherHolderRx provideProductIdentifierFetcherHolderRx(
            THBaseIABProductIdentifierFetcherHolderRx thBaseIABProductIdentifierFetcherHolder)
    {
        return thBaseIABProductIdentifierFetcherHolder;
    }

    @Provides THIABInventoryFetcherHolderRx provideInventoryFetcherHolderRx(THBaseIABInventoryFetcherHolderRx thBaseIABInventoryFetcherHolder)
    {
        return thBaseIABInventoryFetcherHolder;
    }

    @Provides THIABPurchaseFetcherHolderRx providePurchaseFetcherHolderRx(THBaseIABPurchaseFetcherHolderRx thBaseIABPurchaseFetcherHolder)
    {
        return thBaseIABPurchaseFetcherHolder;
    }

    @Provides THIABPurchaserHolderRx providePurchaserHolderRx(THBaseIABPurchaserHolderRx thBaseIABPurchaserHolder)
    {
        return thBaseIABPurchaserHolder;
    }

    @Provides THIABPurchaseReporterHolderRx providePurchaseReporterHolderRx(THBaseIABPurchaseReporterHolderRx thBaseIABPurchaseReporterHolder)
    {
        return thBaseIABPurchaseReporterHolder;
    }

    @Provides THIABPurchaseConsumerHolderRx providePurchaseConsumerHolderRx(THBaseIABPurchaseConsumerHolderRx thBaseIABPurchaseConsumerHolder)
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

    @Provides THBillingAlertDialogUtil provideBillingAlertDialogUtil(THIABAlertDialogUtil THIABAlertDialogUtil)
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
