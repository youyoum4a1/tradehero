package com.ayondo.academy.billing;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.common.billing.googleplay.BillingServiceBinderObservable;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.billing.googleplay.THBaseIABInteractorRx;
import com.ayondo.academy.billing.googleplay.THBaseIABLogicHolderRx;
import com.ayondo.academy.billing.googleplay.THIABAlertDialogRxUtil;
import com.ayondo.academy.billing.googleplay.THIABInteractorRx;
import com.ayondo.academy.billing.googleplay.THIABLogicHolderRx;
import com.ayondo.academy.billing.googleplay.consumer.THBaseIABPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.googleplay.consumer.THIABPurchaseConsumerHolderRx;
import com.ayondo.academy.billing.googleplay.identifier.THBaseIABProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.identifier.THIABProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.inventory.THBaseIABInventoryFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.inventory.THIABInventoryFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.purchase.THBaseIABPurchaserHolderRx;
import com.ayondo.academy.billing.googleplay.purchase.THIABPurchaserHolderRx;
import com.ayondo.academy.billing.googleplay.purchasefetch.THBaseIABPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.purchasefetch.THIABPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.googleplay.report.THBaseIABPurchaseReporterHolderRx;
import com.ayondo.academy.billing.googleplay.report.THIABPurchaseReporterHolderRx;
import com.ayondo.academy.billing.googleplay.tester.THBaseIABBillingAvailableTesterHolderRx;
import com.ayondo.academy.billing.googleplay.tester.THIABBillingAvailableTesterHolderRx;
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
