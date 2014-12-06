package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.billing.THBaseBillingInteractorRx;
import com.tradehero.th.billing.THBillingRequisitePreparer;
import com.tradehero.th.fragments.billing.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THIABStoreProductDetailView;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton public class THBaseIABInteractorRx
    extends
        THBaseBillingInteractorRx<
                        IABSKUListKey,
                        IABSKU,
                        IABSKUList,
                        THIABProductDetail,
                        THIABPurchaseOrder,
                        THIABOrderId,
                        THIABPurchase,
                        THIABLogicHolderRx,
                        THIABStoreProductDetailView,
                        THIABSKUDetailAdapter>
    implements THIABInteractorRx
{
    public static final String BUNDLE_KEY_ACTION = THBaseIABInteractorRx.class.getName() + ".action";

    @NonNull protected final THIABProductDetailCacheRx thiabProductDetailCache;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInteractorRx(
            @NonNull THIABLogicHolderRx billingActor,
            @NonNull Provider<Activity> activityProvider,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull THIABAlertDialogRxUtil thIABAlertDialogUtil,
            @NonNull THBillingRequisitePreparer billingRequisitePreparer,
            @NonNull THIABProductDetailCacheRx thiabProductDetailCache,
            @NonNull UserProfileDTOUtil userProfileDTOUtil)
    {
        super(
                billingActor,
                activityProvider,
                progressDialogUtil,
                thIABAlertDialogUtil,
                billingRequisitePreparer);
        this.thiabProductDetailCache = thiabProductDetailCache;
        this.userProfileDTOUtil = userProfileDTOUtil;
    }
    //</editor-fold>

    @Override public String getName()
    {
        return IABConstants.NAME;
    }

}
