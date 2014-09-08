package com.tradehero.th.api.leaderboard.def;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;

public class InviteFriendsLeaderboardDefDTO extends ConnectedLeaderboardDefDTO
{
    //<editor-fold desc="Constructors">
    public InviteFriendsLeaderboardDefDTO(Context context)
    {
        super();
        id = LeaderboardDefKeyKnowledge.INVITE_FRIENDS_ID;
        name = context.getString(R.string.leaderboard_community_invite_friends);
        bannerResId = R.drawable.invite_friends_banner;
    }
    //</editor-fold>
}
