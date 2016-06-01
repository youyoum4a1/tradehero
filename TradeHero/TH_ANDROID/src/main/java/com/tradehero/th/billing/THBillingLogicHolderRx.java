package com.ayondo.academy.billing;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.BillingLogicHolderRx;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListKey;
import com.ayondo.academy.billing.inventory.THProductDetailDomainInformerRx;
import com.ayondo.academy.billing.report.PurchaseReportResult;
import rx.Observable;

public interface THBillingLogicHolderRx<
        ProductIdentifierListKeyType extends ProductIdentifierListKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends BaseProductIdentifierList<ProductIdentifierType>,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THPurchaseOrderType extends THPurchaseOrder<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends
        BillingLogicHolderRx<
                ProductIdentifierListKeyType,
                ProductIdentifierType,
                ProductIdentifierListType,
                THProductDetailType,
                THPurchaseOrderType,
                THOrderIdType,
                THProductPurchaseType>,
        THProductDetailDomainInformerRx<
                ProductIdentifierType,
                THProductDetailType>
{
    @NonNull Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>>
    reportAndClear(int requestCode,
            @NonNull THProductPurchaseType purchase);

    @NonNull Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>>
    report(int requestCode,
            @NonNull THProductPurchaseType purchase);

    @NonNull Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>>
    reportAndClear(int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail);

    @NonNull Observable<PurchaseReportResult<
            ProductIdentifierType,
            THOrderIdType,
            THProductPurchaseType>>
    report(int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail);
}
