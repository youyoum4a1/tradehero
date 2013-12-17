package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.milestone.BaseMilestoneGroup;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 12:18 PM To change this template use File | Settings | File Templates. */
public class PurchaseRestorerRequiredMilestone extends BaseMilestoneGroup
{
    public static final String TAG = PurchaseRestorerRequiredMilestone.class.getSimpleName();
    private static final int POSITION_FETCH_INVENTORY = 0;
    private static final int POSITION_FETCH_PURCHASE = 1;
    private static final int POSITION_FETCH_PORTFOLIO = 2;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    public PurchaseRestorerRequiredMilestone(Context context, THIABActorInventoryFetcher actorInventoryFetcher, THIABActorPurchaseFetcher actorPurchaseFetcher)
    {
        super();
        DaggerUtils.inject(this);
        add(new THInventoryFetchMilestone(context, actorInventoryFetcher, IABSKUListType.getInApp()));
        add(new THIABPurchaseFetchMilestone(actorPurchaseFetcher));
        add(new PortfolioCompactListRetrievedMilestone(currentUserBaseKeyHolder.getCurrentUserBaseKey()));
    }

    public Map<IABSKU, THIABPurchase> getFetchedPurchases()
    {
        return ((THIABPurchaseFetchMilestone) milestones.get(POSITION_FETCH_PURCHASE)).getFetchedPurchases();
    }
}
