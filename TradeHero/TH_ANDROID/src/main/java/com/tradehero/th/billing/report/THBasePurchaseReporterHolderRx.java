package com.tradehero.th.billing.report;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeHolder;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.billing.THOrderId;
import com.tradehero.th.billing.THProductPurchase;
import rx.Observable;

abstract public class THBasePurchaseReporterHolderRx<
        ProductIdentifierType extends ProductIdentifier,
        THOrderIdType extends THOrderId,
        THProductPurchaseType extends THProductPurchase<ProductIdentifierType, THOrderIdType>>
        extends BaseRequestCodeHolder<THPurchaseReporterRx<
        ProductIdentifierType,
        THOrderIdType,
        THProductPurchaseType>>
        implements THPurchaseReporterHolderRx<
        ProductIdentifierType,
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
            @NonNull THProductPurchaseType purchase)
    {
        THPurchaseReporterRx<
                ProductIdentifierType, THOrderIdType, THProductPurchaseType> reporter = actors.get(requestCode);
        if (reporter == null)
        {
            try
            {
                reporter = createReporter(requestCode, purchase);
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
            @NonNull THProductPurchaseType purchase);
}
