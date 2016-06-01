package com.ayondo.academy.billing;

import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.ayondo.academy.billing.amazon.AmazonAlertDialogRxUtil;
import com.ayondo.academy.billing.amazon.THAmazonAlertDialogRxUtil;
import com.ayondo.academy.billing.amazon.THAmazonInteractorRx;
import com.ayondo.academy.billing.amazon.THAmazonLogicHolderRx;
import com.ayondo.academy.billing.amazon.THBaseAmazonInteractorRx;
import com.ayondo.academy.billing.amazon.THBaseAmazonLogicHolderRx;
import com.ayondo.academy.billing.amazon.consume.THAmazonPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.amazon.consume.THBaseAmazonPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.amazon.identifier.THAmazonProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.amazon.identifier.THBaseAmazonProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.amazon.inventory.THAmazonInventoryFetcherHolderRx;
import com.ayondo.academy.billing.amazon.inventory.THBaseAmazonInventoryFetcherHolderRx;
import com.ayondo.academy.billing.amazon.purchase.THAmazonPurchaserHolderRx;
import com.ayondo.academy.billing.amazon.purchase.THBaseAmazonPurchaserHolderRx;
import com.ayondo.academy.billing.amazon.purchasefetch.THAmazonPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.amazon.purchasefetch.THBaseAmazonPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.amazon.report.THAmazonPurchaseReporterHolderRx;
import com.ayondo.academy.billing.amazon.report.THBaseAmazonPurchaseReporterHolderRx;
import com.ayondo.academy.billing.amazon.tester.THAmazonBillingAvailableTesterHolderRx;
import com.ayondo.academy.billing.amazon.tester.THBaseAmazonBillingAvailableTesterHolderRx;
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
    //<editor-fold desc="Actors and Action Holders Rx">
    @Provides THAmazonBillingAvailableTesterHolderRx provideBillingAvailableTesterHolderRx(THBaseAmazonBillingAvailableTesterHolderRx thBaseAmazonBillingAvailableTesterHolder)
    {
        return thBaseAmazonBillingAvailableTesterHolder;
    }

    @Provides THAmazonProductIdentifierFetcherHolderRx provideProductIdentifierFetcherHolderRx(THBaseAmazonProductIdentifierFetcherHolderRx thBaseAmazonProductIdentifierFetcherHolder)
    {
        return thBaseAmazonProductIdentifierFetcherHolder;
    }

    @Provides THAmazonInventoryFetcherHolderRx provideInventoryFetcherHolderRx(THBaseAmazonInventoryFetcherHolderRx thBaseAmazonInventoryFetcherHolder)
    {
        return thBaseAmazonInventoryFetcherHolder;
    }

    @Provides THAmazonPurchaseFetcherHolderRx providePurchaseFetcherHolderRx(THBaseAmazonPurchaseFetcherHolderRx thBaseAmazonPurchaseFetcherHolder)
    {
        return thBaseAmazonPurchaseFetcherHolder;
    }

    @Provides THAmazonPurchaserHolderRx providePurchaserHolderRx(THBaseAmazonPurchaserHolderRx thBaseAmazonPurchaserHolder)
    {
        return thBaseAmazonPurchaserHolder;
    }

    @Provides THAmazonPurchaseReporterHolderRx providePurchaseReporterHolderRx(THBaseAmazonPurchaseReporterHolderRx thBaseAmazonPurchaseReporterHolder)
    {
        return thBaseAmazonPurchaseReporterHolder;
    }

    @Provides THAmazonPurchaseConsumerHolderRx providePurchaseConsumerHolderRx(THBaseAmazonPurchaseConsumerHolderRx thBaseAmazonPurchaseConsumerHolder)
    {
        return thBaseAmazonPurchaseConsumerHolder;
    }
    //</editor-fold>

    @Provides @Singleton BillingLogicHolderRx provideBillingActorRx(THBillingLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides @Singleton THBillingLogicHolderRx provideTHBillingActorRx(THAmazonLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides @Singleton THAmazonLogicHolderRx provideTHIABLogicHolderRx(THBaseAmazonLogicHolderRx logicHolderRx)
    {
        return logicHolderRx;
    }

    @Provides THBillingAlertDialogRxUtil provideBillingAlertDialogRxUtil(THAmazonAlertDialogRxUtil billingDialogRxUtil)
    {
        return billingDialogRxUtil;
    }

    @Provides AmazonAlertDialogRxUtil provideAmazonAlertDialogRxUtil(THAmazonAlertDialogRxUtil billingDialogRxUtil)
    {
        return billingDialogRxUtil;
    }

    @Provides @Singleton BillingInteractorRx provideBillingInteractorRx(THBillingInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton THBillingInteractorRx provideBillingInteractorRx(THAmazonInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton THAmazonInteractorRx provideBillingInteractorRx(THBaseAmazonInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }
}
