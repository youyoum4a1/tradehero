package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductIdentifierListCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.billing.googleplay.THIABProductIdentifierFetcher;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This cache happens to populate itself fully when called once.
 * Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:27 PM To change this template use File | Settings | File Templates.
 * */
@Singleton public class IABSKUListCache extends ProductIdentifierListCache<IABSKU, IABSKUListType, IABSKUList>
{
    public static final int MAX_SIZE = 5;

    private THIABProductIdentifierFetcher skuFetcher;

    @Inject public IABSKUListCache()
    {
        super(MAX_SIZE);
        skuFetcher = new THIABProductIdentifierFetcher();
    }

    @Override public IABSKUListType getKeyForAll()
    {
        return IABSKUListType.getAll();
    }

    @Override protected IABSKUList fetch(IABSKUListType key) throws Throwable
    {
        return reProcess(key, skuFetcher.fetchProductIdentifiersSync());
    }

    private IABSKUList reProcess(IABSKUListType key, Map<String, List<IABSKU>> values)
    {
        IABSKUList returnable = null;
        for (Map.Entry<String, List<IABSKU>> entry : values.entrySet())
        {
            IABSKUListType newKey = new IABSKUListType(entry.getKey());
            IABSKUList newValue = new IABSKUList(entry.getValue());
            put (newKey, newValue);
            if (key.equals(newKey) || key.equals(getKeyForAll()))
            {
                returnable = newValue;
            }
        }
        return returnable;
    }
}
