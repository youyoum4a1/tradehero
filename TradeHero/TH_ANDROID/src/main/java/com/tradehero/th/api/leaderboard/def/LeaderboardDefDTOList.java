package com.tradehero.th.api.leaderboard.def;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;

public class LeaderboardDefDTOList extends BaseArrayList<LeaderboardDefDTO>
    implements DTO, ContainerDTO<LeaderboardDefDTO, LeaderboardDefDTOList>
{
    //<editor-fold desc="Contructors">
    public LeaderboardDefDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull
    public LeaderboardDefDTOList keepForCountryCode(@NonNull String countryCode)
    {
        Predicate<LeaderboardDefDTO> leaderboardDefDTOPredicate = new LeaderboardDefDTOCountryCodeContainPredicate(countryCode);
        LeaderboardDefDTOList kept = new LeaderboardDefDTOList();
        for (LeaderboardDefDTO leaderboardDefDTO : this)
        {
            if (leaderboardDefDTOPredicate.apply(leaderboardDefDTO))
            {
                kept.add(leaderboardDefDTO);
            }
        }
        return kept;
    }

    @NonNull public LeaderboardDefKeyList createKeys()
    {
        LeaderboardDefKeyList list = new LeaderboardDefKeyList();
        for (LeaderboardDefDTO leaderboardDefDTO : this)
        {
            list.add(leaderboardDefDTO.getLeaderboardDefKey());
        }
        return list;
    }

    @Override public LeaderboardDefDTOList getList()
    {
        return this;
    }
}
