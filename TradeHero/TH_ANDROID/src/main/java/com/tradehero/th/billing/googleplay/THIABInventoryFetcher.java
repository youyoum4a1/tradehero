package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THInventoryFetcher;

public interface THIABInventoryFetcher
        extends
        IABInventoryFetcher<
                IABSKU,
                THIABProductDetail,
                IABException>,
        THInventoryFetcher<
                IABSKU,
                THIABProductDetail,
                IABException>
{
}
