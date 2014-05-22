package com.tradehero.th.api.leaderboard;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class LeaderboardDTO implements DTO
{
    public static final String INCLUDE_FOF = "INCLUDE_FOF";

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

    //<editor-fold desc="Constructors">
    public LeaderboardDTO()
    {
        super();
    }

    public LeaderboardDTO(int id, String name, List<LeaderboardUserDTO> users, int userIsAtPositionZeroBased, Date markUtc)
    {
        this.id = id;
        this.name = name;
        this.users = users;
        this.userIsAtPositionZeroBased = userIsAtPositionZeroBased;
        this.markUtc = markUtc;
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
        return avgStdDevPositionRoiInPeriod;
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

}