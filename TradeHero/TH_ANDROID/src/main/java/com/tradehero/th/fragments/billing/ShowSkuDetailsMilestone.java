package com.tradehero.th.fragments.billing;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.milestone.BaseMilestoneGroup;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.googleplay.THIABActorInventoryFetcher;
import com.tradehero.th.billing.googleplay.THInventoryFetchMilestone;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;

/** Created with IntelliJ IDEA. User: xavier Date: 11/22/13 Time: 11:08 AM To change this template use File | Settings | File Templates. */
public class ShowSkuDetailsMilestone extends BaseMilestoneGroup
{
    public static final String TAG = ShowSkuDetailsMilestone.class.getSimpleName();

    public final UserBaseKey userBaseKey;

    /**
     * If param userBaseKey is null, the PortfolioCompactListRetrievedMilestone is not added.
     * @param context
     * @param userBaseKey
     */
    public ShowSkuDetailsMilestone(final Context context, THIABActorInventoryFetcher actorInventoryFetcher, IABSKUListType iabskuListType, final UserBaseKey userBaseKey)
    {
        super();

        this.userBaseKey = userBaseKey;
        add(new THInventoryFetchMilestone(context, actorInventoryFetcher, iabskuListType));
        if (userBaseKey != null)
        {
            add(new PortfolioCompactListRetrievedMilestone(userBaseKey));
        }
    }
}
