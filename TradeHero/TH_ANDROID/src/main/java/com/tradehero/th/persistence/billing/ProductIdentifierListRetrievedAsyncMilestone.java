package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:40 PM To change this template use File | Settings | File Templates. */
abstract public class ProductIdentifierListRetrievedAsyncMilestone<
        ProductIdentifierListKey extends DTOKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends ArrayList<ProductIdentifierType> & DTO,
        ProductIdentifierListCacheType extends DTOCache<ProductIdentifierListKey, ProductIdentifierListType>>
        extends DTORetrievedAsyncMilestone<ProductIdentifierListKey, ProductIdentifierListType, ProductIdentifierListCacheType>
{
    public static final String TAG = ProductIdentifierListRetrievedAsyncMilestone.class.getSimpleName();

    public ProductIdentifierListRetrievedAsyncMilestone(ProductIdentifierListKey key)
    {
        super(key);
        DaggerUtils.inject(this);
    }

    @Override public void launch()
    {
        launchOwn();
    }
}
