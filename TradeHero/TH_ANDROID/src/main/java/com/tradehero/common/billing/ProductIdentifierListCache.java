package com.tradehero.common.billing;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.billing.googleplay.THIABProductIdentifierFetcher;

/**
 * This cache happens to populate itself fully when called once.
 * Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:27 PM To change this template use File | Settings | File Templates.
 * */
abstract public class ProductIdentifierListCache<
            ProductIdentifierType extends ProductIdentifier,
            ProductIdentifierListTypeType extends ProductIdentifierListType,
            ProductIdentifierList extends BaseProductIdentifierList<ProductIdentifierType>>
        extends StraightDTOCache<ProductIdentifierListTypeType, ProductIdentifierList>
{
    public static final String TAG = ProductIdentifierListCache.class.getSimpleName();

    private THIABProductIdentifierFetcher skuFetcher;

    public ProductIdentifierListCache(int maxSize)
    {
        super(maxSize);
    }

    abstract public ProductIdentifierListTypeType getKeyForAll();
}
