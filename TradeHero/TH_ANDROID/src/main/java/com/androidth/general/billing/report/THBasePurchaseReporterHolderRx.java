package com.androidth.general.billing.report;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.BaseRequestCodeHolder;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.billing.THOrderId;
import com.androidth.general.billing.THProductDetail;
import com.androidth.general.billing.THProductPurchase;
import rx.Observable;

abstract public class THBasePurchaseReporterHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BaseRequestCodeHolder<THPurchaseReporterRx<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>>
        implements THPurchaseReporterHolderRx<
        ProductIdentifierType,
        THProductDetailType,
        THOrderIdType,
        THProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    public THBasePurchaseReporterHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override public Observable<PurchaseReportResult<ProductIdentifierType, THOrderIdType, THProductPurchaseType>> get(
            int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail)
    {
        THPurchaseReporterRx<
                ProductIdentifierType, THOrderIdType, THProductPurchaseType> reporter = actors.get(requestCode);
        if (reporter == null)
        {
            try
            {
                reporter = createReporter(requestCode, purchase, productDetail);
            } catch (Exception e)
            {
                return Observable.error(e);
            }
            actors.put(requestCode, reporter);
        }
        return reporter.get();
    }

    @NonNull abstract protected THPurchaseReporterRx<ProductIdentifierType, THOrderIdType, THProductPurchaseType> createReporter(
            int requestCode,
            @NonNull THProductPurchaseType purchase,
            @NonNull THProductDetailType productDetail);
}
