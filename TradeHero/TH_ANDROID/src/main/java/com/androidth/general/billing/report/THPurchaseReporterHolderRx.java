package com.androidth.general.billing.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.RequestCodeHolder;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductDetail;
import com.androidth.general.billing.THProductPurchase;
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
