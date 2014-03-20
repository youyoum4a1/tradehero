package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.th.billing.THInventoryFetchMilestone;
import com.tradehero.th.persistence.billing.ProductIdentifierListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:50 PM To change this template use File | Settings | File Templates. */
public class THIABInventoryFetchMilestone
        extends THInventoryFetchMilestone<
        IABSKUListKey,
                    IABSKU,
                    IABSKUList,
                    IABSKUListCache,
                    THIABProductDetail,
                    THIABProductDetailCache,
                    IABException>
{
    @Inject protected THIABLogicHolder logicHolder;

    @Inject Lazy<IABSKUListCache> iabskuListCache;
    @Inject Lazy<THIABProductDetailCache> thskuDetailCache;

    public THIABInventoryFetchMilestone(IABSKUListKey iabskuListType)
    {
        super(iabskuListType);
        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        if (dependsOn != null)
        {
            dependsOn.onDestroy();
        }
        logicHolder = null;
        fetchListener = null;
        dependsOn = null;
        dependCompleteListener = null;
    }

    @Override
    protected ProductIdentifierListRetrievedAsyncMilestone<IABSKUListKey, IABSKU, IABSKUList, IABSKUListCache> createDependsOnMilestone(
            IABSKUListKey iabskuListType)
    {
        return new IABSKUListRetrievedAsyncMilestone(iabskuListType);
    }

    @Override protected IABSKUListCache getProductIdentifierListCache()
    {
        return iabskuListCache.get();
    }

    @Override protected THIABProductDetailCache getProductDetailCache()
    {
        return thskuDetailCache.get();
    }

    @Override protected List<IABSKU> getAllProductIdentifiers()
    {
        List<IABSKU> skus = iabskuListCache.get().get(IABSKUListKey.getInApp());
        if (iabskuListCache.get().get(IABSKUListKey.getSubs()) != null)
        {
            skus.addAll(iabskuListCache.get().get(IABSKUListKey.getSubs()));
        }
        return skus;
    }

    @Override protected void launchFetchProper(List<IABSKU> allSkus)
    {
        THIABLogicHolder logicHolder = this.logicHolder;
        if (logicHolder == null)
        {
            notifyFailedListener(new NullPointerException("logicHolder was null"));
        }
        else
        {
            // TODO refactor with proper calls
            int requestCode = logicHolder.getUnusedRequestCode();
            logicHolder.registerInventoryFetchedListener(requestCode, fetchListener);
            logicHolder.launchInventoryFetchSequence(requestCode, allSkus);
        }
    }
}
