package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.googleplay.Constants;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.SKUFetcher;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolderExtended
    extends THIABLogicHolder
    implements SKUFetcher.OnSKUFetchedListener<IABSKU>
{
    public static final String TAG = THIABLogicHolderExtended.class.getSimpleName();

    protected Exception latestSkuFetcherException;

    @Inject Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;

    private DTOCache.Listener<UserBaseKey, OwnedPortfolioIdList> portfolioCompactListCacheListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioIdList> portfolioCompactListFetchTask;

    public THIABLogicHolderExtended(Activity activity)
    {
        super(activity);
        DaggerUtils.inject(this);
    }

    //<editor-fold desc="THIABActor">
    @Override public List<THIABProductDetails> getDetailsOfDomain(String domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get().get(getAllSkus()), THIABProductDetails.getPredicateIsOfCertainDomain(domain));
    }
    //</editor-fold>

    //<editor-fold desc="THIABSKUFetcher.OnSKUFetchedListener">
    @Override public void onFetchSKUsFailed(int requestCode, Exception exception)
    {
        latestSkuFetcherException = exception;
    }

    @Override public void onFetchedSKUs(int requestCode, Map<String, List<IABSKU>> availableSkus)
    {
        List<IABSKU> mixedIABSKUs = availableSkus.get(Constants.ITEM_TYPE_INAPP);
        if (availableSkus.containsKey(Constants.ITEM_TYPE_SUBS))
        {
            mixedIABSKUs.addAll(availableSkus.get(Constants.ITEM_TYPE_SUBS));
        }
        // TODO to cache perhaps
    }
    //</editor-fold>

    private void launchOwnPortfolioSequence()
    {
        if (portfolioCompactListCacheListener == null)
        {
            portfolioCompactListCacheListener = new DTOCache.Listener<UserBaseKey, OwnedPortfolioIdList>()
            {
                @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value)
                {
                    THLog.d(TAG, "Received the list of portfolios for user " + key);
                    // TODO launch fetch purchase somewhere
                }

                @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                {
                    THLog.e(TAG, "There was an error fetching your portfolio list", error);
                }
            };
        }
        if (portfolioCompactListFetchTask != null)
        {
            portfolioCompactListFetchTask.forgetListener(true);
        }
        portfolioCompactListFetchTask = portfolioCompactListCache.get().getOrFetch(currentUserBaseKeyHolder.get().getCurrentUserBaseKey(), portfolioCompactListCacheListener);
        portfolioCompactListFetchTask.execute();
    }
}
