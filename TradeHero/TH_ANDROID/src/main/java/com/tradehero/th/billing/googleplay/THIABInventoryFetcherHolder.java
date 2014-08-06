package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THInventoryFetcherHolder;

interface THIABInventoryFetcherHolder
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
