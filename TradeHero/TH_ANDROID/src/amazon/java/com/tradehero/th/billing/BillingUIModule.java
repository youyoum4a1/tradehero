package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.th.billing.amazon.AmazonAlertDialogUtil;
import com.tradehero.th.billing.amazon.THAmazonAlertDialogRxUtil;
import com.tradehero.th.billing.amazon.THAmazonAlertDialogUtil;
import com.tradehero.th.billing.amazon.THAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THAmazonInteractor;
import com.tradehero.th.billing.amazon.THAmazonInteractorRx;
import com.tradehero.th.billing.amazon.THAmazonInventoryFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonLogicHolder;
import com.tradehero.th.billing.amazon.THAmazonLogicHolderFull;
import com.tradehero.th.billing.amazon.THAmazonLogicHolderRx;
import com.tradehero.th.billing.amazon.THAmazonProductIdentifierFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseConsumerHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseFetcherHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaseReporterHolder;
import com.tradehero.th.billing.amazon.THAmazonPurchaserHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonBillingAvailableTesterHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonInteractor;
import com.tradehero.th.billing.amazon.THBaseAmazonInteractorRx;
import com.tradehero.th.billing.amazon.THBaseAmazonInventoryFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonLogicHolderRx;
import com.tradehero.th.billing.amazon.THBaseAmazonProductIdentifierFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseConsumerHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseFetcherHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaseReporterHolder;
import com.tradehero.th.billing.amazon.THBaseAmazonPurchaserHolder;
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
import com.tradehero.th.billing.amazon.request.BaseTHUIAmazonRequest;
import com.tradehero.th.billing.amazon.tester.THAmazonBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.amazon.tester.THBaseAmazonBillingAvailableTesterHolderRx;
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
    @Provides
    THAmazonBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseAmazonBillingAvailableTesterHolder thBaseAmazonBillingAvailableTesterHolder)
    {
        return thBaseAmazonBillingAvailableTesterHolder;
    }

    @Provides
    THAmazonProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseAmazonProductIdentifierFetcherHolder thBaseAmazonProductIdentifierFetcherHolder)
    {
        return thBaseAmazonProductIdentifierFetcherHolder;
    }

    @Provides THAmazonInventoryFetcherHolder provideInventoryFetcherHolder(THBaseAmazonInventoryFetcherHolder thBaseAmazonInventoryFetcherHolder)
    {
        return thBaseAmazonInventoryFetcherHolder;
    }

    @Provides THAmazonPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseAmazonPurchaseFetcherHolder thBaseAmazonPurchaseFetcherHolder)
    {
        return thBaseAmazonPurchaseFetcherHolder;
    }

    @Provides THAmazonPurchaserHolder providePurchaserHolder(THBaseAmazonPurchaserHolder thBaseAmazonPurchaserHolder)
    {
        return thBaseAmazonPurchaserHolder;
    }

    @Provides THAmazonPurchaseReporterHolder providePurchaseReporterHolder(THBaseAmazonPurchaseReporterHolder thBaseAmazonPurchaseReporterHolder)
    {
        return thBaseAmazonPurchaseReporterHolder;
    }

    @Provides THAmazonPurchaseConsumerHolder providePurchaseConsumerHolder(THBaseAmazonPurchaseConsumerHolder thBaseAmazonPurchaseConsumerHolder)
    {
        return thBaseAmazonPurchaseConsumerHolder;
    }
    //</editor-fold>

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

    @Provides @Singleton THBillingInteractorRx provideTHBillingInteractorRx(THAmazonInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton THAmazonInteractorRx provideTHIABInteractorRx(THBaseAmazonInteractorRx billingInteractorRx)
    {
        return billingInteractorRx;
    }

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THAmazonLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THAmazonLogicHolder provideTHIABLogicHolder(THAmazonLogicHolderFull thAmazonLogicHolderFull)
    {
        return thAmazonLogicHolderFull;
    }

    @Provides THBillingAlertDialogUtil provideBillingAlertDialogUtil(THAmazonAlertDialogUtil thAmazonAlertDialogUtil)
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

    @Provides AmazonAlertDialogUtil provideAmazonAlertDialogUtil(THAmazonAlertDialogUtil dialogUtil)
    {
        return dialogUtil;
    }
}
