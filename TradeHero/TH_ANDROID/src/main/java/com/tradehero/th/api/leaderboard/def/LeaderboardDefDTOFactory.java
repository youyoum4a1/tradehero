package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.api.leaderboard.key.ConnectedLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.DrillDownLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.ExchangeLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.MostSkilledLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.SectorLeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.TimePeriodLeaderboardDefListKey;
import java.util.HashMap;
import javax.inject.Inject;
import rx.functions.Func1;

public class LeaderboardDefDTOFactory implements Func1<LeaderboardDefDTOList, LeaderboardDefDTOList>
{
    @NonNull private final Resources resources;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(
            @NonNull Context context)
    {
        this.resources = context.getResources();
    }
    //</editor-fold>

    @NonNull public LeaderboardDefDTOList call(@NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        leaderboardDefDTOs.add(new FriendLeaderboardDefDTO(resources));
        leaderboardDefDTOs.add(new ExchangeContainerLeaderboardDefDTO(resources));
        return leaderboardDefDTOs;
    }

    @NonNull public static HashMap<LeaderboardDefListKey, LeaderboardDefDTOList> file(
            @NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        HashMap<LeaderboardDefListKey, LeaderboardDefDTOList> filed = new HashMap<>();
        filed.put(new LeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new ConnectedLeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new DrillDownLeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new SectorLeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new ExchangeLeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new TimePeriodLeaderboardDefListKey(1), new LeaderboardDefDTOList());
        filed.put(new MostSkilledLeaderboardDefListKey(1), new LeaderboardDefDTOList());

        for (LeaderboardDefDTO leaderboardDefDTO: leaderboardDefDTOs)
        {
            filed.get(new LeaderboardDefListKey(1)).add(leaderboardDefDTO);
            LeaderboardDefListKey listKey = leaderboardDefDTO.getLeaderboardDefListKey();
            filed.get(listKey).add(leaderboardDefDTO);
        }
        return filed;
    }
}
