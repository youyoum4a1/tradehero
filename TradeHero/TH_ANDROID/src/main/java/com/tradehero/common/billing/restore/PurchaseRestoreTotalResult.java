package com.tradehero.common.billing.restore;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.tradehero.common.billing.BaseResult;
import com.tradehero.common.billing.OrderId;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.utils.CollectionUtils;
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
