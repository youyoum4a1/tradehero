package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInteractor;
import com.tradehero.common.billing.BillingLogicHolder;
import com.tradehero.th.billing.request.BaseTHUIBillingRequest;
import com.tradehero.th.billing.samsung.THBaseSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THBaseSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.THSamsungAlertDialogUtil;
import com.tradehero.th.billing.samsung.THSamsungBillingAvailableTesterHolder;
import com.tradehero.th.billing.samsung.THSamsungBillingInteractor;
import com.tradehero.th.billing.samsung.THSamsungInteractor;
import com.tradehero.th.billing.samsung.THSamsungInventoryFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolder;
import com.tradehero.th.billing.samsung.THSamsungLogicHolderFull;
import com.tradehero.th.billing.samsung.THSamsungProductIdentifierFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseFetcherHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaseReporterHolder;
import com.tradehero.th.billing.samsung.THSamsungPurchaserHolder;
import com.tradehero.th.billing.samsung.request.BaseTHUISamsungRequest;
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
    THSamsungBillingAvailableTesterHolder provideBillingAvailableTesterHolder(THBaseSamsungBillingAvailableTesterHolder thBaseSamsungBillingAvailableTesterHolder)
    {
        return thBaseSamsungBillingAvailableTesterHolder;
    }

    @Provides
    THSamsungProductIdentifierFetcherHolder provideProductIdentifierFetcherHolder(THBaseSamsungProductIdentifierFetcherHolder thBaseSamsungProductIdentifierFetcherHolder)
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
}
