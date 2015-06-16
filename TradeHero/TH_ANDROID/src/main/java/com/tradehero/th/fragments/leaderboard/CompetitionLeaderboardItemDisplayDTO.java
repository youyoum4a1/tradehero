package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;

class CompetitionLeaderboardItemDisplayDto extends LeaderboardMarkedUserItemDisplayDto
{
    public ProviderDTO providerDTO;

    public CompetitionLeaderboardItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources, currentUserId);
    }

    public CompetitionLeaderboardItemDisplayDto(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO, ProviderDTO providerDTO)
    {
        super(resources, currentUserId, currentUserProfileDTO);
        this.providerDTO = providerDTO;
    }

    public CompetitionLeaderboardItemDisplayDto(@NonNull Resources resources, @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull ProviderDTO providerDTO)
    {
        super(resources, currentUserId, leaderboardItem, currentUserProfileDTO);
        this.providerDTO = providerDTO;
    }
}
