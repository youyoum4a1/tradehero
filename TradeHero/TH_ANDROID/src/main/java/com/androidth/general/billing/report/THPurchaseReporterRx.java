package com.androidth.general.billing.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.RequestCodeActor;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductPurchase;
import rx.Observable;

public interface THPurchaseReporterRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
    extends RequestCodeActor
{
    @NonNull Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get();
}
