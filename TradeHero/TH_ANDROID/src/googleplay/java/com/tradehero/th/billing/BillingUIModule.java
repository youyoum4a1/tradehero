package com.androidth.general.billing;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BillingInteractorRx;
import com.androidth.general.common.billing.BillingLogicHolderRx;
import com.androidth.general.common.billing.googleplay.BillingServiceBinderObservable;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.billing.googleplay.THBaseIABInteractorRx;
import com.androidth.general.billing.googleplay.THBaseIABLogicHolderRx;
import com.androidth.general.billing.googleplay.THIABAlertDialogRxUtil;
import com.androidth.general.billing.googleplay.THIABInteractorRx;
import com.androidth.general.billing.googleplay.THIABLogicHolderRx;
import com.androidth.general.billing.googleplay.consumer.THBaseIABPurchaseConsumerHolderRx;
import com.androidth.general.billing.googleplay.consumer.THIABPurchaseConsumerHolderRx;
import com.androidth.general.billing.googleplay.identifier.THBaseIABProductIdentifierFetcherHolderRx;
import com.androidth.general.billing.googleplay.identifier.THIABProductIdentifierFetcherHolderRx;
import com.androidth.general.billing.googleplay.inventory.THBaseIABInventoryFetcherHolderRx;
import com.androidth.general.billing.googleplay.inventory.THIABInventoryFetcherHolderRx;
import com.androidth.general.billing.googleplay.purchase.THBaseIABPurchaserHolderRx;
import com.androidth.general.billing.googleplay.purchase.THIABPurchaserHolderRx;
import com.androidth.general.billing.googleplay.purchasefetch.THBaseIABPurchaseFetcherHolderRx;
import com.androidth.general.billing.googleplay.purchasefetch.THIABPurchaseFetcherHolderRx;
import com.androidth.general.billing.googleplay.report.THBaseIABPurchaseReporterHolderRx;
import com.androidth.general.billing.googleplay.report.THIABPurchaseReporterHolderRx;
import com.androidth.general.billing.googleplay.tester.THBaseIABBillingAvailableTesterHolderRx;
import com.androidth.general.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
import dagger.Module;
import dagger.Provides;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;

@Module(
        complete = false,
        library = true,
        overrides = true
)
public class BillingUIModule
{
    @Provides @Singleton Observable<IBinder> provideBillingServiceBinderObservable(@NonNull Provider<Activity> activityProvider)
    {
        THToast.show("providing binder observable");
        return BillingServiceBinderObservable.getServiceBinder(
                activityProvider.get(),
                BillingServiceBinderObservable.getBillingBindIntent(),
                0,
                Context.BIND_AUTO_CREATE)
                .cache(1);
    }

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
