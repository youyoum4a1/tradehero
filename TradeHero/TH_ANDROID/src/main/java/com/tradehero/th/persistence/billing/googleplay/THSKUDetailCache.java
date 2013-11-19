package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.THSKUDetailsTuner;
import com.tradehero.th.persistence.billing.ProductDetailCache;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class THSKUDetailCache extends ProductDetailCache<IABSKU, THSKUDetails, THSKUDetailsTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THSKUDetailCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected void createDetailsTuner()
    {
        detailsTuner = new THSKUDetailsTuner();
    }

    @Override protected THSKUDetails fetch(IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THSKUDetails individually");
    }
}
