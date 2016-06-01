package com.ayondo.academy.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.ProductIdentifier;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductPurchase;

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
