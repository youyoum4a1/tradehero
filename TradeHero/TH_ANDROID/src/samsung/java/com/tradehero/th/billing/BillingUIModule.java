package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.samsung.THBaseSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungInteractorRx;
import com.tradehero.th.billing.samsung.THBaseSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungLogicHolderRx;
import com.tradehero.th.billing.samsung.THBaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.THSamsungAlertDialogRxUtil;
import com.tradehero.th.billing.samsung.THSamsungAlertDialogUtil;
import com.tradehero.th.billing.samsung.THSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THSamsungBillingInteractor;
import com.tradehero.th.billing.samsung.THSamsungInteractor;
import com.tradehero.th.billing.samsung.THSamsungInteractorRx;
import com.tradehero.th.billing.samsung.THSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolderFull;
import com.tradehero.th.billing.samsung.THSamsungLogicHolderRx;
import com.tradehero.th.billing.samsung.THSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.identifier.THBaseSamsungProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.samsung.identifier.THSamsungProductIdentifierFetcherHolderRx;
import com.tradehero.th.billing.samsung.inventory.THBaseSamsungInventoryFetcherHolderRx;
import com.tradehero.th.billing.samsung.inventory.THSamsungInventoryFetcherHolderRx;
import com.tradehero.th.billing.samsung.purchase.THBaseSamsungPurchaserHolderRx;
import com.tradehero.th.billing.samsung.purchase.THSamsungPurchaserHolderRx;
import com.tradehero.th.billing.samsung.purchasefetch.THBaseSamsungPurchaseFetcherHolderRx;
import com.tradehero.th.billing.samsung.purchasefetch.THSamsungPurchaseFetcherHolderRx;
import com.tradehero.th.billing.samsung.report.THBaseSamsungPurchaseReporterHolderRx;
import com.tradehero.th.billing.samsung.report.THSamsungPurchaseReporterHolderRx;
import com.tradehero.th.billing.samsung.request.BaseTHUISamsungRequest;
import com.tradehero.th.billing.samsung.tester.THBaseSamsungBillingAvailableTesterHolderRx;
import com.tradehero.th.billing.samsung.tester.THSamsungBillingAvailableTesterHolderRx;
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
    @Provides THSamsungBillingAvailableTesterHolder provideBillingAvailableTesterHolder(
            THBaseSamsungBillingAvailableTesterHolder thBaseSamsungBillingAvailableTesterHolder)
    {
        return thBaseSamsungBillingAvailableTesterHolder;
    }

    @Provides THSamsungProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(
            THBaseSamsungProductIdentifierFetcherHolder thBaseSamsungProductIdentifierFetcherHolder)
    {
        return thBaseSamsungProductIdentifierFetcherHolder;
    }

    @Provides THSamsungInventoryFetcherHolder provideInventoryFetcherHolder(THBaseSamsungInventoryFetcherHolder thBaseSamsungInventoryFetcherHolder)
    {
        return thBaseSamsungInventoryFetcherHolder;
    }

    @Provides THSamsungPurchaseFetcherHolder providePurchaseFetcherHolder(THBaseSamsungPurchaseFetcherHolder thBaseSamsungPurchaseFetcherHolder)
    {
        return thBaseSamsungPurchaseFetcherHolder;
    }

    @Provides THSamsungPurchaserHolder providePurchaserHolder(THBaseSamsungPurchaserHolder thBaseSamsungPurchaserHolder)
    {
        return thBaseSamsungPurchaserHolder;
    }

    @Provides THSamsungPurchaseReporterHolder providePurchaseReporterHolder(THBaseSamsungPurchaseReporterHolder thBaseSamsungPurchaseReporterHolder)
    {
        return thBaseSamsungPurchaseReporterHolder;
    }
    //</editor-fold>

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

    @Provides @Singleton BillingLogicHolder provideBillingActor(THBillingLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THBillingLogicHolder provideTHBillingActor(THSamsungLogicHolder logicHolder)
    {
        return logicHolder;
    }

    @Provides @Singleton THSamsungLogicHolder provideTHSamsungLogicHolder(THSamsungLogicHolderFull thSamsungLogicHolderFull)
    {
        return thSamsungLogicHolderFull;
    }

    @Provides THBillingAlertDialogUtil provideBillingAlertDialogUtil(THSamsungAlertDialogUtil THSamsungAlertDialogUtil)
    {
        return THSamsungAlertDialogUtil;
    }

    @Provides @Singleton BillingInteractor provideBillingInteractor(THBillingInteractor billingInteractor)
    {
        return billingInteractor;
    }

    @Provides @Singleton THBillingInteractor provideTHBillingInteractor(THSamsungInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides @Singleton THSamsungInteractor provideTHSamsungInteractor(THSamsungBillingInteractor thSamsungInteractor)
    {
        return thSamsungInteractor;
    }

    @Provides BaseTHUIBillingRequest.Builder provideTHUIBillingRequestBuilder()
    {
        return BaseTHUISamsungRequest.builder();
    }

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
