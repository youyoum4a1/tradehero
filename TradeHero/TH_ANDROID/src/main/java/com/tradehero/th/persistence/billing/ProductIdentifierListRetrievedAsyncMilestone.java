package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.persistence.DTORetrievedAsyncMilestone;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;


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
