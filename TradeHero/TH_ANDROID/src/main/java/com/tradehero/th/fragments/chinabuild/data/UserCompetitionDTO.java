package com.tradehero.th.fragments.chinabuild.data;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.th.utils.DateUtils;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by huhaiping on 14-9-10.
 */
public class UserCompetitionDTO implements DTO, Serializable
{

    private static final long serialVersionUID = 1L;
    public int id;
    public int leaderboardId;
    public String name;
    public String description;
    public Date startDateUtc;
    public Date endDateUtc;
    public boolean isPrivate;
    public int hostUserId;
    public String hostUserName;
    public String iconUrl;
    public String bannerUrl;
    public String detailUrl;
    public Boolean isEnrolled;
    public int durationDays;
    public boolean isOngoing;
    public int userCount;
    public ExchangeDTOList exchanges;
    public boolean isOfficial;
    public int rankRise;
    public double roi;

    public String getRankRise()
    {
        if (rankRise > 0)
        {
            return " + " + rankRise;
        }
        else
        {
            return "" + rankRise;
        }
    }

    public String getUserCounter()
    {
        return String.valueOf(userCount) + "人";
    }

    public String getDisplayDatePeriod()
    {
        return DateUtils.getDisplayableDate(Application.context().getResources(), startDateUtc, endDateUtc);
    }

    //返回 交易所 缩写
    public String getDisplayExchangeShort()
    {
        if (exchanges != null)
        {
            int size = exchanges.size();
            int[] exchangeList = new int[size];
            for (int i = 0; i < exchangeList.length; i++)
            {
                exchangeList[i] = exchanges.get(i).id;
            }
            return CompetitionUtils.getExchangeShortName(exchangeList);
        }
        return "";
    }
}
