package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;


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
