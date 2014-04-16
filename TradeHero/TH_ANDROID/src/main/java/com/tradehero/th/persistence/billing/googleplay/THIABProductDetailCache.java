package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class THIABProductDetailCache extends ProductDetailCache<IABSKU, THIABProductDetail, THIABProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABProductDetailCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected void createDetailsTuner()
    {
        detailsTuner = new THIABProductDetailTuner();
    }

    @Override protected THIABProductDetail fetch(IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THIABProductDetail individually");
    }
}
