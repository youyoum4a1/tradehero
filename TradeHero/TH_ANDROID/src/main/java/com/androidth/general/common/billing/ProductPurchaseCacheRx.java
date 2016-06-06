package com.androidth.general.common.billing;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import java.util.ArrayList;
import java.util.List;

abstract public class ProductPurchaseCacheRx<
        ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends BaseDTOCacheRx<OrderIdType, ProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    public ProductPurchaseCacheRx(int maxSize,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(List<ProductPurchaseType> values)
    {
        if (values != null)
        {
            for (ProductPurchaseType purchase : values)
            {
                if (purchase != null && purchase.getOrderId() != null)
                {
                    onNext(purchase.getOrderId(), purchase);
                }
            }
        }
    }

    public ArrayList<ProductPurchaseType> getValues()
    {
        ArrayList<ProductPurchaseType> values = new ArrayList<>();
        ProductPurchaseType value;
        for (OrderIdType key : new ArrayList<>(snapshot().keySet()))
        {
            value = getCachedValue(key);
            if (value != null)
            {
                values.add(value);
            }
        }
        return values;
    }
}
