package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.billing.googleplay.SKUFetcher;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:27 PM To change this template use File | Settings | File Templates. */
@Singleton public class IABSKUListCache extends StraightDTOCache<IABSKUListType, IABSKUList>
{
    public static final String TAG = IABSKUListCache.class.getSimpleName();
    public static final int MAX_SIZE = 5;

    private SKUFetcher skuFetcher;

    @Inject public IABSKUListCache()
    {
        super(MAX_SIZE);
        skuFetcher = new SKUFetcher();
    }

    @Override protected IABSKUList fetch(IABSKUListType key) throws Throwable
    {
        return reProcess(key, skuFetcher.fetchSkusSync());
    }

    private IABSKUList reProcess(IABSKUListType key, Map<String, List<IABSKU>> values)
    {
        IABSKUList returnable = null;
        for (Map.Entry<String, List<IABSKU>> entry : values.entrySet())
        {
            IABSKUListType newKey = new IABSKUListType(entry.getKey());
            IABSKUList newValue = new IABSKUList(entry.getValue());
            put (newKey, newValue);
            if (key.equals(newKey))
            {
                returnable = newValue;
            }
        }
        return returnable;
    }
}
