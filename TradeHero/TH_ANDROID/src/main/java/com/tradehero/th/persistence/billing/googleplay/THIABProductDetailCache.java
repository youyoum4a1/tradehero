package com.tradehero.th.persistence.billing.googleplay;

import com.tradehero.common.billing.ProductDetailCache;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
import com.tradehero.th.billing.googleplay.THIABProductDetailTuner;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class THIABProductDetailCache extends ProductDetailCache<IABSKU, THIABProductDetail, THIABProductDetailTuner>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    //<editor-fold desc="Constructors">
    @Inject public THIABProductDetailCache(@NotNull THIABProductDetailTuner thiabProductDetailTuner)
    {
        super(DEFAULT_MAX_SIZE, thiabProductDetailTuner);
    }
    //</editor-fold>

    @Override @NotNull public THIABProductDetail fetch(@NotNull IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THIABProductDetail individually");
    }
}
