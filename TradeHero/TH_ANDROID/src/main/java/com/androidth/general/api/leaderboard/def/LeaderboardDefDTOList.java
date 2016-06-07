package com.androidth.general.api.leaderboard.def;

import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;

public class LeaderboardDefDTOList extends BaseArrayList<LeaderboardDefDTO>
        implements DTO, ContainerDTO<LeaderboardDefDTO, LeaderboardDefDTOList>
{
    @NonNull public Pair<LeaderboardDefDTOList, LeaderboardDefDTOList> splitExchangeWithWithOut(@NonNull String countryCode)
    {
        Pair<LeaderboardDefDTOList, LeaderboardDefDTOList> split = Pair.create(new LeaderboardDefDTOList(), new LeaderboardDefDTOList());
        for (LeaderboardDefDTO leaderboardDefDTO : this)
        {
            if (leaderboardDefDTO.countryCodes != null && leaderboardDefDTO.countryCodes.contains(countryCode))
            {
                split.first.add(leaderboardDefDTO);
            }
            else if (leaderboardDefDTO.isExchangeRestricted())
            {
                split.second.add(leaderboardDefDTO);
            }
        }
        return split;
    }

    @Override public LeaderboardDefDTOList getList()
    {
        return this;
    }
}