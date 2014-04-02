package com.tradehero.th.network.service;

import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.persistence.social.HeroKey;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Repurposes requests
 * Created by xavier on 12/12/13.
 */
@Singleton public class FollowerServiceWrapper
{
    private final FollowerService followerService;

    @Inject public FollowerServiceWrapper(FollowerService followerService)
    {
        super();
        this.followerService = followerService;
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


    public void getFollowersSummary(HeroKey followerKey, Callback<FollowerSummaryDTO> callback)
    {
        //basicCheck(followerId);
        switch (followerKey.heroType)
        {
            case PREMIUM:
                //TODO use real data
                this.followerService.getFollowersSummary(followerKey.userBaseKey.key, callback);
                break;
            case FREE:
                //TODO use real data
                this.followerService.getFollowersSummary(followerKey.userBaseKey.key, callback);
                break;
            case ALL:
                this.followerService.getFollowersSummary(followerKey.userBaseKey.key, callback);
                break;
            default:
                break;
        }

    }

    public FollowerSummaryDTO getFollowersSummary(HeroKey followerKey)
    {
        //basicCheck(followerId);
        switch (followerKey.heroType)
        {
            case PREMIUM:
                //TODO use real data
                return this.followerService.getFollowersSummary(followerKey.userBaseKey.key);
            case FREE:
                //TODO use real data
                return  this.followerService.getFollowersSummary(followerKey.userBaseKey.key);
            case ALL:
                return this.followerService.getFollowersSummary(followerKey.userBaseKey.key);
            default:
                break;

        }
        return null;


    }
    //</editor-fold>
}
