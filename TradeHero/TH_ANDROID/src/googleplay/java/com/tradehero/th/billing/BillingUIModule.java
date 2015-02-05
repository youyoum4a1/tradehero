package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.th.billing.googleplay.THBaseIABInteractorRx;
import com.tradehero.th.billing.googleplay.THBaseIABLogicHolderRx;
import com.tradehero.th.billing.googleplay.THIABAlertDialogRxUtil;
import com.tradehero.th.billing.googleplay.THIABInteractorRx;
import com.tradehero.th.billing.googleplay.THIABLogicHolderRx;
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
import com.tradehero.th.billing.googleplay.tester.THBaseIABBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
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

    @Provides @Singleton BillingLogicHolderRx provideBillingActorRx(THBillingLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides @Singleton THBillingLogicHolderRx provideTHBillingActorRx(THIABLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides @Singleton THIABLogicHolderRx provideTHIABLogicHolderRx(THBaseIABLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides THBillingAlertDialogRxUtil provideBillingAlertDialogRxUtil(THIABAlertDialogRxUtil THIABAlertDialogUtil)
    {
        return THIABAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractorRx provideBillingInteractorRx(THBillingInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton THBillingInteractorRx provideTHBillingInteractorRx(THIABInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton THIABInteractorRx provideTHIABInteractorRx(THBaseIABInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }
}
