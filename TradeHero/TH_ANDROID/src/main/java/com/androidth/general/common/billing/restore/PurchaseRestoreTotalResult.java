package com.androidth.general.common.billing.restore;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.androidth.general.common.billing.BaseResult;
import com.androidth.general.common.billing.OrderId;
import com.androidth.general.common.billing.ProductIdentifier;
import com.androidth.general.common.billing.ProductPurchase;
import com.androidth.general.common.utils.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

public class PurchaseRestoreTotalResult<
        ProductIdentifierType extends ProductIdentifier,
        OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseResult
{
    @NonNull public final List<PurchaseRestoreResultWithError<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType>> restoredList;

    //<editor-fold desc="Constructors">
    public PurchaseRestoreTotalResult(int requestCode)
    {
        super(requestCode);
        this.restoredList = new ArrayList<>();
    }
    //</editor-fold>

    public void add(@NonNull PurchaseRestoreResultWithError<
            ProductIdentifierType,
            OrderIdType,
            ProductPurchaseType> restored)
    {
        this.restoredList.add(restored);
    }

    public int getCount()
    {
        return restoredList.size();
    }

    public int getSucceededCount()
    {
        return CollectionUtils.count(restoredList,
                new Predicate<PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>()
                {
                    @Override public boolean apply(
                            PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType> item)
                    {
                        return item.throwable == null;
                    }
                });
    }

    public int getFailedCount()
    {
        return CollectionUtils.count(restoredList,
                new Predicate<PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType>>()
                {
                    @Override public boolean apply(
                            PurchaseRestoreResultWithError<ProductIdentifierType, OrderIdType, ProductPurchaseType> item)
                    {
                        return item.throwable != null;
                    }
                });
    }
}
