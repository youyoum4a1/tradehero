package com.tradehero.common.billing.googleplay;

import com.tradehero.common.persistence.StraightDTOCache;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by xavier on 2/11/14.
 */
public class IABPurchaseCache<
            IABSKUType extends IABSKU,
            IABOrderIdType extends IABOrderId,
            IABPurchaseType extends IABPurchase<IABSKUType, IABOrderIdType>>
        extends StraightDTOCache<IABSKUType, IABPurchaseType>
{
    public static final String TAG = IABPurchaseCache.class.getSimpleName();

    protected final ArrayList<IABSKUType> keys;

    public IABPurchaseCache(int maxSize)
    {
        super(maxSize);
        keys = new ArrayList<>();
    }

    @Override public IABPurchaseType put(IABSKUType key, IABPurchaseType value)
    {
        IABPurchaseType previous = super.put(key, value);
        keys.add(key);
        return previous;
    }

    @Override public void invalidate(IABSKUType key)
    {
        super.invalidate(key);
        keys.remove(key);
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
        keys.clear();
    }

    @Override protected IABPurchaseType fetch(IABSKUType key) throws Throwable
    {
        throw new IllegalStateException("You cannot fetch on this cache");
    }

    public void put(Map<IABSKUType, IABPurchaseType> values)
    {
        if (values != null)
        {
            for (Map.Entry<IABSKUType, IABPurchaseType> entry : values.entrySet())
            {
                if (entry != null && entry.getKey() != null)
                {
                    put(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public ArrayList<IABSKUType> getKeys()
    {
        return new ArrayList<>(keys);
    }
}
