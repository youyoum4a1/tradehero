package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungConstants;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THSamsungSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THSamsungStoreProductDetailView;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseSamsungInteractorRx
        extends
        THBaseBillingInteractorRx<
                SamsungSKUListKey,
                SamsungSKU,
                SamsungSKUList,
                THSamsungProductDetail,
                THSamsungPurchaseOrder,
                THSamsungOrderId,
                THSamsungPurchase,
                THSamsungLogicHolderRx,
                THSamsungStoreProductDetailView,
                THSamsungSKUDetailAdapter>
        implements THSamsungInteractorRx
{
    public static final String BUNDLE_KEY_ACTION = THBaseSamsungInteractorRx.class.getName() + ".action";

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THSamsungAlertDialogRxUtil thSamsungAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull THSamsungLogicHolderRx billingActor)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thSamsungAlertDialogUtil,
                billingRequisitePreparer);
    }
    //</editor-fold>

    @Override public String getName()
    {
        return SamsungConstants.NAME;
    }
}
