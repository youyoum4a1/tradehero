package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.androidth.general.api.social.UserFriendsDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.utils.Constants;

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
            com.androidth.general.common.persistence.DTO,
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
