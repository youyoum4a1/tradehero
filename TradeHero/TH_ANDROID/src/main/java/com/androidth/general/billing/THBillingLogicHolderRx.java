package com.androidth.general.billing;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseProductIdentifierList;
import com.androidth.general.common.billing.BillingLogicHolderRx;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductIdentifierListKey;
import com.androidth.general.billing.inventory.THProductDetailDomainInformerRx;
import com.androidth.general.billing.report.PurchaseReportResult;
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
