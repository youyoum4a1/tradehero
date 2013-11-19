package com.tradehero.th.persistence.billing;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.billing.googleplay.THSKUDetailsTuner;
import com.tradehero.th.persistence.position.PositionCompactIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 1:05 PM To change this template use File | Settings | File Templates. */
@Singleton public class SKUDetailCache extends StraightDTOCache<IABSKU, THSKUDetails>
{
    private static final int DEFAULT_MAX_SIZE = 200;

    private THSKUDetailsTuner detailsTuner;

    //<editor-fold desc="Constructors">
    @Inject public SKUDetailCache()
    {
        super(DEFAULT_MAX_SIZE);
        detailsTuner = new THSKUDetailsTuner();
    }
    //</editor-fold>

    @Override protected THSKUDetails fetch(IABSKU key)
    {
        throw new IllegalStateException("You should not fetch THSKUDetails individually");
    }

    @Override public THSKUDetails put(IABSKU key, THSKUDetails value)
    {
        // Save the correspondence between integer id and compound key.
        detailsTuner.fineTune(value);
        return super.put(key, value);
    }

    public List<THSKUDetails> put(List<THSKUDetails> values)
    {
        if (values == null)
        {
            return null;
        }

        List<THSKUDetails> previousValues = new ArrayList<>();

        for (THSKUDetails skuDetails: values)
        {
            previousValues.add(put(skuDetails.getProductIdentifier(), skuDetails));
        }

        return previousValues;
    }

    public List<THSKUDetails> get(List<IABSKU> keys)
    {
        if (keys == null)
        {
            return null;
        }

        List<THSKUDetails> skuDetails = new ArrayList<>();

        for (IABSKU key: keys)
        {
            skuDetails.add(get(key));
        }

        return skuDetails;
    }
}
