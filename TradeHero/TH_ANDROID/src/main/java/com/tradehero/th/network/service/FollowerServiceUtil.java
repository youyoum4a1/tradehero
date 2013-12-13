package com.tradehero.th.network.service;

import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.UserFollowerDTO;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.Path;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
public class FollowerServiceUtil
{
    public static final String TAG = FollowerServiceUtil.class.getSimpleName();

    private static void basicCheck(FollowerId followerId)
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
    public static UserFollowerDTO getFollowerSubscriptionDetail(FollowerService followerService, FollowerId followerId)
            throws RetrofitError
    {
        basicCheck(followerId);
        return followerService.getFollowerSubscriptionDetail(followerId.followedId, followerId.followerId);
    }

    public static void getFollowerSubscriptionDetail(FollowerService followerService, FollowerId followerId, Callback<UserFollowerDTO> callback)
    {
        basicCheck(followerId);
        followerService.getFollowerSubscriptionDetail(followerId.followedId, followerId.followerId, callback);
    }
    //</editor-fold>
}
