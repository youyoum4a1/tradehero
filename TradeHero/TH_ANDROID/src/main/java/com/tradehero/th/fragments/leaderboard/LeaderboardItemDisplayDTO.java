package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;

public abstract class LeaderboardItemDisplayDTO implements DTO
{
    public static class Factory
    {
        private final Resources resources;
        private final CurrentUserId currentUserId;
        private UserProfileDTO currentUserProfileDTO;

        public Factory(Resources resources, CurrentUserId currentUserId, UserProfileDTO currentUserProfileDTO)
        {
            this.resources = resources;
            this.currentUserId = currentUserId;
            this.currentUserProfileDTO = currentUserProfileDTO;
        }

        @NonNull LeaderboardItemDisplayDTO create(@NonNull LeaderboardUserDTO leaderboardUserDTO)
        {
            return new LeaderboardMarkedUserItemDisplayDto(resources, currentUserId, leaderboardUserDTO, currentUserProfileDTO);
        }

        @NonNull LeaderboardItemDisplayDTO create(@NonNull UserFriendsDTO userFriendsDTO)
        {
            return new FriendLeaderboardItemDisplayDTO.Social(userFriendsDTO);
        }

        @NonNull CompetitionLeaderboardItemDisplayDT create(@NonNull LeaderboardUserDTO leaderboardUserDTO, ProviderDTO providerDTO,
                CompetitionLeaderboardDTO competitionLeaderboardDTO)
        {
            return new CompetitionLeaderboardItemDisplayDT(resources, currentUserId, leaderboardUserDTO, currentUserProfileDTO, providerDTO,
                    competitionLeaderboardDTO);
        }
    }

    public int ranking = -1;
    String lbmuRanking;

    public void setRanking(int ranking)
    {
        this.ranking = ranking;
        lbmuRanking = String.valueOf(ranking);
    }

    public static class DTOList<T extends LeaderboardItemDisplayDTO> extends BaseArrayList<T> implements
            com.tradehero.common.persistence.DTO,
            ContainerDTO<T, DTOList<T>>
    {
        @NonNull public final LeaderboardDTO leaderboardDTO;

        protected DTOList(@NonNull LeaderboardDTO leaderboardDTO)
        {
            this.leaderboardDTO = leaderboardDTO;
        }

        @Override public DTOList<T> getList()
        {
            return this;
        }
    }
}
