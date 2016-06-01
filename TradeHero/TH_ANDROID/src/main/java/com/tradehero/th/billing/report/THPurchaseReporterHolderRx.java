package com.ayondo.academy.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.RequestCodeHolder;
import com.ayondo.academy.billing.THOrderId;
import com.ayondo.academy.billing.THProductDetail;
import com.ayondo.academy.billing.THProductPurchase;
import rx.Observable;

public interface THPurchaseReporterHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends RequestCodeHolder
{
    @NonNull Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>>
    get(int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail);
}
