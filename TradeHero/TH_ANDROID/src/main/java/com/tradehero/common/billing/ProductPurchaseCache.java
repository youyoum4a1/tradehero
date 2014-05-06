package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.ArrayList;
import java.util.List;


abstract public class ProductPurchaseCache<
        ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends StraightDTOCache<OrderIdType, ProductPurchaseType>
{
    protected final ArrayList<OrderIdType> keys;

    public ProductPurchaseCache(int maxSize)
    {
        super(maxSize);
        keys = new ArrayList<>();
    }

    @Override public ProductPurchaseType put(OrderIdType key, ProductPurchaseType value)
    {
        ProductPurchaseType previous = super.put(key, value);
        keys.add(key);
        return previous;
    }

    @Override public void invalidate(OrderIdType key)
    {
        super.invalidate(key);
        keys.remove(key);
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
        keys.clear();
    }

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
        return new ArrayList<>(snapshot().values());
    }
}
