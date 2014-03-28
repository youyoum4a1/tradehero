package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THInventoryFetcherHolder;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABInventoryFetcherHolder
        extends
        IABInventoryFetcherHolder<
                IABSKU,
                THIABProductDetail,
                IABException>,
        THInventoryFetcherHolder<
                IABSKU,
                THIABProductDetail,
                IABException>
{
}
