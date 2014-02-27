package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by xavier on 2/11/14.
 */
abstract public class ProductPurchaseCache<
        ProductIdentifierType extends ProductIdentifier,
            OrderIdType extends OrderId,
        ProductPurchaseType extends ProductPurchase<ProductIdentifierType, OrderIdType>>
        extends StraightDTOCache<ProductIdentifierType, ProductPurchaseType>
{
    protected final ArrayList<ProductIdentifierType> keys;

    public ProductPurchaseCache(int maxSize)
    {
        super(maxSize);
        keys = new ArrayList<>();
    }

    @Override public ProductPurchaseType put(ProductIdentifierType key, ProductPurchaseType value)
    {
        ProductPurchaseType previous = super.put(key, value);
        keys.add(key);
        return previous;
    }

    @Override public void invalidate(ProductIdentifierType key)
    {
        super.invalidate(key);
        keys.remove(key);
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
        keys.clear();
    }

    public void put(Map<ProductIdentifierType, ProductPurchaseType> values)
    {
        if (values != null)
        {
            for (Map.Entry<ProductIdentifierType, ProductPurchaseType> entry : values.entrySet())
            {
                if (entry != null && entry.getKey() != null)
                {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public ArrayList<ProductIdentifierType> getKeys()
    {
        return new ArrayList<>(keys);
    }
}
