package com.tradehero.common.billing;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;

import java.util.ArrayList;
import java.util.List;
import android.support.annotation.NonNull;

abstract public class ProductPurchaseCache<
        ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends StraightDTOCacheNew<OrderIdType, ProductPurchaseType>
{
    //<editor-fold desc="Constructors">
    public ProductPurchaseCache(int maxSize,
            @NonNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize, dtoCacheUtil);
    }
    //</editor-fold>

    public void put(List<ProductPurchaseType> values)
    {
        if (values != null)
        {
            for (ProductPurchaseType purchase : values)
            {
                if (purchase != null && purchase.getOrderId() != null)
                {
                    put(purchase.getOrderId(), purchase);
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
            value = get(key);
            if (value != null)
            {
                values.add(value);
            }
        }
        return values;
    }
}
