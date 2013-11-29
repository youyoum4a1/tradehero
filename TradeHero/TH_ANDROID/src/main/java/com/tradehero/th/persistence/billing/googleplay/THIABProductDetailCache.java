package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THSKUDetailTuner;
import com.tradehero.th.persistence.billing.ProductDetailCache;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class THIABProductDetailCache extends ProductDetailCache<IABSKU, THIABProductDetail, THSKUDetailTuner>
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
        detailsTuner = new THSKUDetailTuner();
    }

    @Override protected THIABProductDetail fetch(IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THIABProductDetail individually");
    }

    @Override public THIABProductDetail put(IABSKU key, THIABProductDetail value)
    {
        return super.put(key, value);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override public List<THIABProductDetail> put(List<THIABProductDetail> values)
    {
        return super.put(values);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
