package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonConstants;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THAmazonSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THAmazonStoreProductDetailView;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseAmazonInteractorRx
        extends
        THBaseBillingInteractorRx<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList,
                THAmazonProductDetail,
                THAmazonPurchaseOrder,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonLogicHolderRx,
                THAmazonStoreProductDetailView,
                THAmazonSKUDetailAdapter>
        implements THAmazonInteractorRx
{
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonInteractorRx(
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THAmazonAlertDialogRxUtil thAmazonAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull THAmazonLogicHolderRx billingActor,
            @NonNull UserProfileDTOUtil userProfileDTOUtil)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thAmazonAlertDialogUtil,
                billingRequisitePreparer);
        this.userProfileDTOUtil = userProfileDTOUtil;
    }
    //</editor-fold>

    @Override public String getName()
    {
        return AmazonConstants.NAME;
    }
}
