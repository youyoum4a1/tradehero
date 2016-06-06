package com.androidth.general.billing.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductPurchase;

public class PurchaseReportResult<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
    extends BaseResult
{
    @NonNull public final THProductPurchaseType reportedPurchase;
    @NonNull public final UserProfileDTO updatedUserProfile;

    //<editor-fold desc="Constructors">
    public PurchaseReportResult(
            int requestCode,
            @NonNull THProductPurchaseType reportedPurchase,
            @NonNull UserProfileDTO updatedUserProfile)
    {
        super(requestCode);
        this.reportedPurchase = reportedPurchase;
        this.updatedUserProfile = updatedUserProfile;
    }
    //</editor-fold>
}
