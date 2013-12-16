package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
public class FollowerServiceWrapper
{
    public static final String TAG = FollowerServiceWrapper.class.getSimpleName();

    @Inject FollowerService followerService;

    public FollowerServiceWrapper()
    {
        super();
        DaggerUtils.inject(this);
    }

    private void basicCheck(FollowerId followerId)
    {
        if (followerId == null)
        {
            throw new NullPointerException("followerId cannot be null");
        }
        if (followerId.followerId == null)
        {
            throw new NullPointerException("followerId.followerId cannot be null");
        }
        if (followerId.followedId == null)
        {
            throw new NullPointerException("followerId.followedId cannot be null");
        }
    }

    //<editor-fold desc="Get Follower Subscription Detail">
    public UserFollowerDTO getFollowerSubscriptionDetail(FollowerId followerId)
            throws RetrofitError
    {
        basicCheck(followerId);
        return this.followerService.getFollowerSubscriptionDetail(followerId.followedId, followerId.followerId);
    }

    public void getFollowerSubscriptionDetail(FollowerId followerId, Callback<UserFollowerDTO> callback)
    {
        basicCheck(followerId);
        this.followerService.getFollowerSubscriptionDetail(followerId.followedId, followerId.followerId, callback);
    }
    //</editor-fold>
}
