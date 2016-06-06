package com.androidth.general.billing;

import com.androidth.general.common.billing.BillingInteractorRx;
import com.androidth.general.common.billing.BillingLogicHolderRx;
import com.androidth.general.billing.samsung.THBaseSamsungInteractorRx;
import com.androidth.general.billing.samsung.THBaseSamsungLogicHolderRx;
import com.androidth.general.billing.samsung.THSamsungAlertDialogRxUtil;
import com.androidth.general.billing.samsung.THSamsungInteractorRx;
import com.androidth.general.billing.samsung.THSamsungLogicHolderRx;
import com.androidth.general.billing.samsung.identifier.THBaseSamsungProductIdentifierFetcherHolderRx;
import com.androidth.general.billing.samsung.identifier.THSamsungProductIdentifierFetcherHolderRx;
import com.androidth.general.billing.samsung.inventory.THBaseSamsungInventoryFetcherHolderRx;
import com.androidth.general.billing.samsung.inventory.THSamsungInventoryFetcherHolderRx;
import com.androidth.general.billing.samsung.purchase.THBaseSamsungPurchaserHolderRx;
import com.androidth.general.billing.samsung.purchase.THSamsungPurchaserHolderRx;
import com.androidth.general.billing.samsung.purchasefetch.THBaseSamsungPurchaseFetcherHolderRx;
import com.androidth.general.billing.samsung.purchasefetch.THSamsungPurchaseFetcherHolderRx;
import com.androidth.general.billing.samsung.report.THBaseSamsungPurchaseReporterHolderRx;
import com.androidth.general.billing.samsung.report.THSamsungPurchaseReporterHolderRx;
import com.androidth.general.billing.samsung.tester.THBaseSamsungBillingAvailableTesterHolderRx;
import com.androidth.general.billing.samsung.tester.THSamsungBillingAvailableTesterHolderRx;
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
    @Provides THSamsungBillingAvailableTesterHolderRx provideBillingAvailableTesterHolderRx(
            THBaseSamsungBillingAvailableTesterHolderRx thBaseSamsungBillingAvailableTesterHolder)
    {
        return thBaseSamsungBillingAvailableTesterHolder;
    }

    @Provides THSamsungProductIdentifierFetcherHolderRx provideProductIdentifierFetcherHolder(
            THBaseSamsungProductIdentifierFetcherHolderRx thBaseSamsungProductIdentifierFetcherHolder)
    {
        return thBaseSamsungProductIdentifierFetcherHolder;
    }

    @Provides THSamsungInventoryFetcherHolderRx provideInventoryFetcherHolderRx(
            THBaseSamsungInventoryFetcherHolderRx thBaseSamsungInventoryFetcherHolder)
    {
        return thBaseSamsungInventoryFetcherHolder;
    }

    @Provides THSamsungPurchaseFetcherHolderRx providePurchaseFetcherHolderRx(THBaseSamsungPurchaseFetcherHolderRx thBaseSamsungPurchaseFetcherHolder)
    {
        return thBaseSamsungPurchaseFetcherHolder;
    }

    @Provides THSamsungPurchaserHolderRx providePurchaserHolderRx(THBaseSamsungPurchaserHolderRx thBaseSamsungPurchaserHolder)
    {
        return thBaseSamsungPurchaserHolder;
    }

    @Provides THSamsungPurchaseReporterHolderRx providePurchaseReporterHolderRx(
            THBaseSamsungPurchaseReporterHolderRx thBaseSamsungPurchaseReporterHolder)
    {
        return thBaseSamsungPurchaseReporterHolder;
    }
    //</editor-fold>

    @Provides @Singleton BillingLogicHolderRx provideBillingActor(THBillingLogicHolderRx logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolderRx provideTHBillingActor(THSamsungLogicHolderRx logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THSamsungLogicHolderRx provideTHSamsungLogicHolder(THBaseSamsungLogicHolderRx thSamsungLogicHolderFull)
    {
        return thSamsungLogicHolderFull;
    }

    @Provides THBillingAlertDialogRxUtil provideBillingAlertDialogUtilRx(THSamsungAlertDialogRxUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractorRx provideBillingInteractorRx(THBillingInteractorRx billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractorRx provideTHBillingInteractorRx(THSamsungInteractorRx thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides @Singleton THSamsungInteractorRx provideTHSamsungInteractorRx(THBaseSamsungInteractorRx thSamsungInteractor)
    {
        return thSamsungInteractor;
    }
}
