package com.tradehero.th.billing;

import com.tradehero.common.milestone.BaseMilestoneGroup;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;

/**
 * Created by xavier on 2/25/14.
 */
public class THBillingInitialMilestone extends BaseMilestoneGroup
{
    public static final String TAG = THBillingInitialMilestone.class.getSimpleName();

    public final UserBaseKey userBaseKey;

    /**
     * @param userBaseKey
     */
    public THBillingInitialMilestone(final UserBaseKey userBaseKey)
    {
        super();
        this.userBaseKey = userBaseKey;
        add(new PortfolioCompactListRetrievedMilestone(userBaseKey));
        add(new UserProfileRetrievedMilestone(userBaseKey));
    }
}
