package com.tradehero.th.api.users;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
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

    public List<UserLeaderboardRanking> leaderboardRankings;
            // *** DELETE ME -- once all clients are on >= 1.2.3
    public List<UserLeaderboardRanking> rank;
            // // user's top-traders ranking across all LBs

    public boolean firstFollowAllTime;

    public boolean useTHPrice;

    public int unreadCount;
    public int alertCount;

    public int tradesSharedCount_FB;

    public List<UserAlertPlanDTO> userAlertPlans;
    public List<ProviderDTO> enrolledProviders;

    public boolean isFollowingUser(int userId)
    {
        return this.heroIds.contains(userId);
    }
}