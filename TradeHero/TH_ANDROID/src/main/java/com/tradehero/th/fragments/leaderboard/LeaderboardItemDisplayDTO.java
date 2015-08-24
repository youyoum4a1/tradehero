package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.Constants;

public abstract class LeaderboardItemDisplayDTO implements DTO
{
    protected final String maxOwnLeaderRanking;

    protected LeaderboardItemDisplayDTO(Resources resources)
    {
        maxOwnLeaderRanking = resources.getString(R.string.leaderboard_max_ranked_position, THSignedNumber.builder(Constants.MAX_OWN_LEADER_RANKING).relevantDigitCount(1).with000Suffix().useShortSuffix().build().toString());
    }

    public static class Factory
    {
        public final Resources resources;
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
            return new FriendLeaderboardItemDisplayDTO.Social(resources, userFriendsDTO);
        }

        @NonNull CompetitionLeaderboardItemDisplayDTO create(@NonNull LeaderboardUserDTO leaderboardUserDTO, ProviderDTO providerDTO,
                CompetitionLeaderboardDTO competitionLeaderboardDTO)
        {
            return new CompetitionLeaderboardItemDisplayDTO(resources, currentUserId, leaderboardUserDTO, currentUserProfileDTO, providerDTO,
                    competitionLeaderboardDTO);
        }
    }

    public int ranking = -1;
    String lbmuRanking;

    public void setRanking(int ranking)
    {
        this.ranking = ranking;
        if (this.ranking > Constants.MAX_OWN_LEADER_RANKING)
        {
            lbmuRanking = maxOwnLeaderRanking;
        }
        else
        {
            lbmuRanking = String.valueOf(ranking);
        }
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
