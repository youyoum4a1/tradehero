package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.th.billing.amazon.THAmazonAlertDialogRxUtil;
import com.tradehero.th.billing.amazon.THAmazonLogicHolderRx;
import com.tradehero.th.billing.amazon.THBaseAmazonLogicHolderRx;
import com.tradehero.th.billing.amazon.consume.THAmazonPurchaseConsumerHolderRx;
import com.tradehero.th.billing.amazon.consume.THBaseAmazonPurchaseConsumerHolderRx;
import com.tradehero.th.billing.amazon.identifier.THAmazonProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.amazon.identifier.THBaseAmazonProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.amazon.inventory.THAmazonInventoryFetcherHolderRx;
import com.tradehero.th.billing.amazon.inventory.THBaseAmazonInventoryFetcherHolderRx;
import com.tradehero.th.billing.amazon.purchase.THAmazonPurchaserHolderRx;
import com.tradehero.th.billing.amazon.purchase.THBaseAmazonPurchaserHolderRx;
import com.tradehero.th.billing.amazon.purchasefetch.THAmazonPurchaseFetcherHolderRx;
import com.tradehero.th.billing.amazon.purchasefetch.THBaseAmazonPurchaseFetcherHolderRx;
import com.tradehero.th.billing.amazon.report.THAmazonPurchaseReporterHolderRx;
import com.tradehero.th.billing.amazon.report.THBaseAmazonPurchaseReporterHolderRx;
import com.tradehero.th.billing.amazon.tester.THAmazonBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.amazon.tester.THBaseAmazonBillingAvailableTesterHolderRx;
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

    @Provides @Singleton BillingInteractorRx provideBillingInteractorRx(THBillingInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }
}
