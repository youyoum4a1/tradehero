package com.tradehero.th.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeActor;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductPurchase;
import rx.Observable;

public interface THPurchaseReporterRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
    extends RequestCodeActor
{
    @NonNull Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get();
}
