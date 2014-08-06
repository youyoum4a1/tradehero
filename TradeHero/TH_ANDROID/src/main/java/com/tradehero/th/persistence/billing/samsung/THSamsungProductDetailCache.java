package com.tradehero.th.persistence.billing.samsung;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.samsung.THSamsungProductDetail;
import com.tradehero.th.billing.samsung.THSamsungProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class THSamsungProductDetailCache extends ProductDetailCache<SamsungSKU, THSamsungProductDetail, THSamsungProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    @Inject SamsungSKUListCache samsungSKUListCache;

    //<editor-fold desc="Constructors">
    @Inject public THSamsungProductDetailCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected void createDetailsTuner()
    {
        detailsTuner = new THSamsungProductDetailTuner();
    }

    @Override protected THSamsungProductDetail fetch(SamsungSKU key)
    {
        throw new IllegalStateException("You should not fetch THSamsungProductDetail individually");
    }

    @Override public THSamsungProductDetail put(SamsungSKU key, THSamsungProductDetail value)
    {
        samsungSKUListCache.add(value);
        return super.put(key, value);
    }
}
