package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
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
    @NonNull private final Context context;

    //<editor-fold desc="Constructors">
    @Inject public LeaderboardDefDTOFactory(
            @NonNull Context context)
    {
        this.context = context;
    }
    //</editor-fold>

    @NonNull public LeaderboardDefDTOList call(@NonNull LeaderboardDefDTOList leaderboardDefDTOs)
    {
        leaderboardDefDTOs.add(new InviteFriendsLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new HeroLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FollowerLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new FriendLeaderboardDefDTO(context));
        leaderboardDefDTOs.add(new ExchangeContainerLeaderboardDefDTO(context));
        return leaderboardDefDTOs;
    }

    @NonNull public HashMap<LeaderboardDefListKey, LeaderboardDefDTOList> file(
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
