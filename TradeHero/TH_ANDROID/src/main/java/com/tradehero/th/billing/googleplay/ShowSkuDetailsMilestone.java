package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.billing.ShowProductDetailsMilestone;

/** Created with IntelliJ IDEA. User: xavier Date: 11/22/13 Time: 11:08 AM To change this template use File | Settings | File Templates. */
public class ShowSkuDetailsMilestone extends ShowProductDetailsMilestone
{
    public static final String TAG = ShowSkuDetailsMilestone.class.getSimpleName();

    /**
     * @param userBaseKey
     */
    public ShowSkuDetailsMilestone(IABSKUListType iabskuListType, final UserBaseKey userBaseKey)
    {
        super(userBaseKey);
        add(new THInventoryFetchMilestone(iabskuListType));
    }
}
