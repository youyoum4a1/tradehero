package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.milestone.BaseMilestoneGroup;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.DaggerUtils;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 11/26/13 Time: 12:18 PM To change this template use File | Settings | File Templates. */
public class PurchaseRestorerRequiredMilestone extends BaseMilestoneGroup
{
    public static final String TAG = PurchaseRestorerRequiredMilestone.class.getSimpleName();
    private static final int POSITION_FETCH_INVENTORY = 0;
    private static final int POSITION_FETCH_PURCHASE = 1;
    private static final int POSITION_FETCH_PORTFOLIO = 2;

    public PurchaseRestorerRequiredMilestone(Context context, THIABActorInventoryFetcher actorInventoryFetcher, THIABActorPurchaseFetcher actorPurchaseFetcher, UserBaseKey userBaseKey)
    {
        super();
        DaggerUtils.inject(this);
        add(new THInventoryFetchMilestone(context, actorInventoryFetcher, IABSKUListType.getInApp()));
        add(new THIABPurchaseFetchMilestone(actorPurchaseFetcher));
        add(new PortfolioCompactListRetrievedMilestone(userBaseKey));
    }

    public Map<IABSKU, BaseIABPurchase> getFetchedPurchases()
    {
        return ((THIABPurchaseFetchMilestone) milestones.get(POSITION_FETCH_PURCHASE)).getFetchedPurchases();
    }
}
