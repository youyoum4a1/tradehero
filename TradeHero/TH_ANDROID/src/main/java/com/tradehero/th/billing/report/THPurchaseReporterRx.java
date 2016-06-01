package com.ayondo.academy.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeActor;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductPurchase;
import rx.Observable;

public interface THPurchaseReporterRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
    extends RequestCodeActor
{
    @NonNull Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get();
}
