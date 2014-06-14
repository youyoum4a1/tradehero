package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.HasExpiration;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LeaderboardDTO implements DTO, HasExpiration
{
    public static final String INCLUDE_FOF = "INCLUDE_FOF";
    public static final int DEFAULT_LIFE_EXPECTANCY_SECONDS = 300;

    public int id;
    public String name;
    public List<LeaderboardUserDTO> users;
    public int userIsAtPositionZeroBased;
    public Date markUtc;

    public int minPositionCount;
    @JsonProperty("max_sharpeRatioInPeriod_vsSP500")
    public double maxSharpeRatioInPeriodVsSP500;
    @JsonProperty("max_stddev_positionRoiInPeriod")
    public double maxStdDevPositionRoiInPeriod;
    @JsonProperty("avg_stddev_positionRoiInPeriod")
    public double avgStdDevPositionRoiInPeriod;

    @NotNull public Date expirationDate;

    //<editor-fold desc="Constructors">
    public LeaderboardDTO()
    {
        super();
        setExpirationDateSecondsInFuture(DEFAULT_LIFE_EXPECTANCY_SECONDS);
    }

    public LeaderboardDTO(int id, String name, List<LeaderboardUserDTO> users, int userIsAtPositionZeroBased, Date markUtc,
            int minPositionCount, double maxSharpeRatioInPeriodVsSP500,
            double maxStdDevPositionRoiInPeriod, double avgStdDevPositionRoiInPeriod,
            @NotNull Date expirationDate)
    {
        this.id = id;
        this.name = name;
        this.users = users;
        this.userIsAtPositionZeroBased = userIsAtPositionZeroBased;
        this.markUtc = markUtc;
        this.minPositionCount = minPositionCount;
        this.maxSharpeRatioInPeriodVsSP500 = maxSharpeRatioInPeriodVsSP500;
        this.maxStdDevPositionRoiInPeriod = maxStdDevPositionRoiInPeriod;
        this.avgStdDevPositionRoiInPeriod = avgStdDevPositionRoiInPeriod;
        this.expirationDate = expirationDate;
    }

    protected void setExpirationDateSecondsInFuture(int seconds)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        this.expirationDate = calendar.getTime();
    }
    //</editor-fold>

    @JsonIgnore
    public LeaderboardKey getLeaderboardKey()
    {
        return new LeaderboardKey(id);
    }

    @Override
    public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Failed to json";
        }
    }

    @JsonIgnore
    public Double getAvgVolatility()
    {
        return (double) avgStdDevPositionRoiInPeriod;
    }

    @JsonIgnore
    public Double getAvgConsistency()
    {
        Double v = getAvgVolatility();
        if (v != null && v != 0)
        {
            return 1/v;
        }
        return (double)2;
    }

    @Override public long getExpiresInSeconds()
    {
        return Math.max(
                0,
                expirationDate.getTime() - Calendar.getInstance().getTime().getTime());
    }
}