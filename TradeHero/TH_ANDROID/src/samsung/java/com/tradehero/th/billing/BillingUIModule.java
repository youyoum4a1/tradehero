package com.ayondo.academy.billing;

import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.ayondo.academy.billing.samsung.THBaseSamsungInteractorRx;
import com.ayondo.academy.billing.samsung.THBaseSamsungLogicHolderRx;
import com.ayondo.academy.billing.samsung.THSamsungAlertDialogRxUtil;
import com.ayondo.academy.billing.samsung.THSamsungInteractorRx;
import com.ayondo.academy.billing.samsung.THSamsungLogicHolderRx;
import com.ayondo.academy.billing.samsung.identifier.THBaseSamsungProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.samsung.identifier.THSamsungProductIdentifierFetcherHolderRx;
import com.ayondo.academy.billing.samsung.inventory.THBaseSamsungInventoryFetcherHolderRx;
import com.ayondo.academy.billing.samsung.inventory.THSamsungInventoryFetcherHolderRx;
import com.ayondo.academy.billing.samsung.purchase.THBaseSamsungPurchaserHolderRx;
import com.ayondo.academy.billing.samsung.purchase.THSamsungPurchaserHolderRx;
import com.ayondo.academy.billing.samsung.purchasefetch.THBaseSamsungPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.samsung.purchasefetch.THSamsungPurchaseFetcherHolderRx;
import com.ayondo.academy.billing.samsung.report.THBaseSamsungPurchaseReporterHolderRx;
import com.ayondo.academy.billing.samsung.report.THSamsungPurchaseReporterHolderRx;
import com.ayondo.academy.billing.samsung.tester.THBaseSamsungBillingAvailableTesterHolderRx;
import com.ayondo.academy.billing.samsung.tester.THSamsungBillingAvailableTesterHolderRx;
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
