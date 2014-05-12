package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface THIABInventoryFetcherHolder extends IABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        IABException>
{
}
