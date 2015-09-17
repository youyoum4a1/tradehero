package com.tradehero.chinabuild.data;

import java.util.Date;

/**
 * Created by huhaiping on 14-8-27.
 */
public class UserTrendingDTO
{
    public String name;
    public int userId;
    public String pictureUrl;
    public double winRatio;
    public int followerCount;
    public double totalWealth;
    public double perfRoi;
    public int tradeCount;
    public String exchange;
    public String securityName;
    public String symbol;
    public int watchCount;
    public int topWatchUserId;
    public String topWatchUserName;

    //for buyWhat
    public String userName;
    public double monthlyRoi;
    public double price;
    public Date dateTimeUtc;
    public double percent;
}
