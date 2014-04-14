package com.tradehero.th.fragments.social.follower;

import com.tradehero.th.api.social.FollowerSummaryDTO;

public interface OnFollowersLoadedListener
{
    void onFollowerLoaded(int page, FollowerSummaryDTO followerSummaryDTO);
}