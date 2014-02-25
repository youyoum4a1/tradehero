package com.tradehero.th.api.users;

import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.UserLeaderboardRankingDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.purchase.UserCreditPlanDTO;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 7:03 PM Copyright (c) TradeHero */
public class UserProfileDTO extends UserProfileCompactDTO
{
    public String email;
    public String address;
    public String biography;
    public String location;
    public String website;

    public List<Integer> heroIds;
    public Integer followerCount;

    public Integer ccPerMonthBalance;   // recurring monthly balance (not used, old)
    public float ccBalance;       // non-recurring: CC spot level

    public PortfolioDTO portfolio;

    public String paypalEmailAddress;

    public boolean pushNotificationsEnabled;
    public boolean emailNotificationsEnabled;

    public List<UserLeaderboardRankingDTO> rank;
            // // user's top-traders ranking across all LBs

    public boolean firstFollowAllTime;

    public boolean useTHPrice;

    public int unreadCount;
    public int alertCount;

    public int tradesSharedCount_FB;

    public List<UserAlertPlanDTO> userAlertPlans;
    public List<UserCreditPlanDTO> userCreditPlans;
    public List<ProviderDTO> enrolledProviders;

    public boolean competitionAutoEnrollOnFirstLaunch;

    public boolean isFollowingUser(int userId)
    {
        return this.heroIds != null && this.heroIds.contains(userId);
    }

    public boolean isFollowingUser(UserBaseKey userBaseKey)
    {
        return userBaseKey != null && isFollowingUser(userBaseKey.key);
    }

    public boolean isFollowingUser(UserBaseDTO userBaseDTO)
    {
        return userBaseDTO != null && isFollowingUser(userBaseDTO.id);
    }

    public List<UserBaseKey> getHeroBaseKeys()
    {
        if (heroIds == null)
        {
            return null;
        }
        List<UserBaseKey> heroKeys = new ArrayList<>();
        for (Integer heroId: heroIds)
        {
            if (heroId != null)
            {
                heroKeys.add(new UserBaseKey(heroId));
            }
        }
        return heroKeys;
    }

    public int getLeaderboardRanking(int leaderboardId)
    {
        for (UserLeaderboardRankingDTO userLeaderboardRankingDTO: rank)
        {
            if (userLeaderboardRankingDTO.leaderboardId == leaderboardId)
            {
                // 1st-base ranking
                return (userLeaderboardRankingDTO.ordinalPosition + 1);
            }
        }
        return 0;
    }

    public int getUserAlertPlansAlertCount()
    {
        int count = 0;
        if (userAlertPlans != null)
        {
            for (UserAlertPlanDTO userAlertPlanDTO: userAlertPlans)
            {
                if (userAlertPlanDTO != null && userAlertPlanDTO.alertPlan != null)
                {
                    count += userAlertPlanDTO.alertPlan.numberOfAlerts;
                }
            }
        }
        return count;
    }

    @Override public String toString()
    {
        return "UserProfileDTO{" +
                "displayName='" + displayName + '\'' +
                ", id=" + id +
                ", picture='" + picture + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", memberSince=" + memberSince +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", biography='" + biography + '\'' +
                ", location='" + location + '\'' +
                ", website='" + website + '\'' +
                ", heroIds=" + heroIds +
                ", followerCount=" + followerCount +
                ", ccPerMonthBalance=" + ccPerMonthBalance +
                ", ccBalance=" + ccBalance +
                ", portfolio=" + portfolio +
                ", paypalEmailAddress='" + paypalEmailAddress + '\'' +
                ", pushNotificationsEnabled=" + pushNotificationsEnabled +
                ", emailNotificationsEnabled=" + emailNotificationsEnabled +
                ", rank=" + rank +
                ", firstFollowAllTime=" + firstFollowAllTime +
                ", useTHPrice=" + useTHPrice +
                ", unreadCount=" + unreadCount +
                ", alertCount=" + alertCount +
                ", tradesSharedCount_FB=" + tradesSharedCount_FB +
                ", userAlertPlans=" + userAlertPlans +
                ", enrolledProviders=" + enrolledProviders +
                '}';
    }
}