package com.tradehero.th.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.StringUtils;

import static com.tradehero.th.utils.Constants.MAX_OWN_LEADER_RANKING;

public class LeaderboardMarkedUserItemDisplayDto extends LeaderboardItemDisplayDTO implements com.tradehero.common.persistence.DTO,
        ExpandableItem
{
    @NonNull final CurrentUserId currentUserId;
    @Nullable final LeaderboardUserDTO leaderboardUserDTO;
    @Nullable final UserStatisticView.DTO userStatisticsDto;
    final String lbmuDisplayName;
    @NonNull final CharSequence lbmuRoi;
    final int lbmuPositionColor;
    final String lbmuFoF;
    String lbmuDisplayPicture;
    @ViewVisibilityValue final int lbmuFoFVisibility;
    @ViewVisibilityValue public int lbmuFollowUserVisibility;
    @ViewVisibilityValue public int lbmuFollowingUserVisibility;
    private String maxOwnLeaderRanking;
    private boolean expanded;
    private boolean isMyOwnRanking;

    /**
     * This constructor can only be used for Header view (Current User Ranking) to show that we are loading required current user information
     */
    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        //This constructor can only be used for Header view (Current User Ranking)
        this.currentUserId = currentUserId;
        this.leaderboardUserDTO = null;
        this.userStatisticsDto = null;
        this.lbmuDisplayName = resources.getString(R.string.loading_required_information);
        this.lbmuRoi = "";
        this.lbmuPositionColor = resources.getColor(R.color.text_primary);
        this.lbmuFoF = "";
        this.lbmuRanking = "-";
        this.lbmuFoFVisibility = View.GONE;
        this.isMyOwnRanking = true;
    }

    /**
     * This constructor can only be used for Header view (Current User Ranking) to show that the user is no ranked
     */
    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserId = currentUserId;
        this.leaderboardUserDTO = null;
        this.userStatisticsDto = null;
        this.lbmuDisplayName = currentUserProfileDTO.displayName;
        this.lbmuRoi = resources.getString(R.string.leaderboard_not_ranked);
        this.lbmuPositionColor = resources.getColor(R.color.text_primary);
        this.lbmuFoF = "";
        this.lbmuRanking = "-";
        this.lbmuFoFVisibility = View.GONE;
        this.isMyOwnRanking = true;
        this.lbmuDisplayPicture = currentUserProfileDTO.picture;
    }

    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO)
    {
        this.ranking = leaderboardItem.ordinalPosition + 1;
        this.lbmuRanking = String.valueOf(leaderboardItem.ordinalPosition + 1);

        this.currentUserId = currentUserId;
        this.leaderboardUserDTO = leaderboardItem;
        this.userStatisticsDto = new UserStatisticView.DTO(resources, leaderboardUserDTO, currentUserProfileDTO.mostSkilledLbmu);
        this.lbmuDisplayName = leaderboardItem.displayName;
        this.lbmuRoi = THSignedPercentage
                .builder(leaderboardItem.roiInPeriod * 100)
                .signTypePlusMinusAlways()
                .relevantDigitCount(3)
                .withDefaultColor()
                .build().createSpanned();

        maxOwnLeaderRanking = resources.getString(R.string.leaderboard_max_ranked_position, MAX_OWN_LEADER_RANKING);

        this.lbmuPositionColor = currentUserId.get() == leaderboardItem.id ?
                resources.getColor(R.color.light_green_normal) :
                resources.getColor(R.color.text_primary);
        this.lbmuFoF = leaderboardItem.friendOfMarkupString;// **
        this.lbmuFoFVisibility = leaderboardItem.isIncludeFoF() != null
                && leaderboardItem.isIncludeFoF()
                && !StringUtils.isNullOrEmptyOrSpaces(leaderboardItem.friendOfMarkupString)
                ? View.VISIBLE
                : View.GONE;

        lbmuFollowUserVisibility = createLbmuFollowUserVisibility(currentUserProfileDTO, leaderboardItem.getBaseKey());
        lbmuFollowingUserVisibility = createLbmuFollowingUserVisibility(currentUserProfileDTO, leaderboardItem.getBaseKey());
        this.lbmuDisplayPicture = leaderboardItem.picture;
        // To use when server correctly sets the relationship in LeaderboardUserDTO
        //lbmuFollowUserVisibility = createLbmuFollowUserVisibility(leaderboardItem);
        //lbmuFollowingUserVisibility = createLbmuFollowingUserVisibility(leaderboardItem);
    }

    @ViewVisibilityValue protected int createLbmuFollowUserVisibility(
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull UserBaseKey heroId)
    {
        if (heroId.key.equals(currentUserProfileDTO.id))
        {
            // you can't follow yourself
            return View.GONE;
        }
        else
        {
            return !currentUserProfileDTO.isFollowingUser(heroId)
                    ? View.VISIBLE
                    : View.GONE;
        }
    }

    @ViewVisibilityValue protected int createLbmuFollowingUserVisibility(
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull UserBaseKey heroId)
    {
        return currentUserProfileDTO.isFollowingUser(heroId)
                ? View.VISIBLE
                : View.GONE;
    }

    public void followChanged(
            @NonNull UserProfileDTO currentUserProfileDTO,
            @NonNull UserBaseKey heroId)
    {
        this.lbmuFollowUserVisibility = createLbmuFollowUserVisibility(currentUserProfileDTO, heroId);
        this.lbmuFollowingUserVisibility = createLbmuFollowingUserVisibility(currentUserProfileDTO, heroId);
    }

    @ViewVisibilityValue protected int createLbmuFollowUserVisibility(
            @NonNull LeaderboardUserDTO leaderboardItem)
    {
        if (leaderboardItem.relationship == null)
        {
            // you can't follow yourself
            return View.GONE;
        }
        else
        {
            return !leaderboardItem.relationship.isMyHero
                    ? View.VISIBLE
                    : View.GONE;
        }
    }

    @ViewVisibilityValue protected int createLbmuFollowingUserVisibility(
            @NonNull LeaderboardUserDTO leaderboardItem)
    {
        return leaderboardItem.relationship.isMyHero
                ? View.VISIBLE
                : View.GONE;
    }

    @Override public boolean isExpanded()
    {
        return expanded;
    }

    @Override public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }

    public void setIsMyOwnRanking(boolean isMyOwnRanking)
    {
        this.isMyOwnRanking = isMyOwnRanking;
    }

    public boolean isMyOwnRanking()
    {
        return isMyOwnRanking;
    }

    public static class Requisite
    {
        @Nullable final LeaderboardUserDTO currentLeaderboardUserDTO;
        @NonNull final UserProfileDTO currentUserProfileDTO;

        public Requisite(
                @NonNull Pair<LeaderboardKey, LeaderboardDTO> currentLeaderboardPair,
                @NonNull Pair<UserBaseKey, UserProfileDTO> currentUserProfilePair)
        {
            this(currentLeaderboardPair.second, currentUserProfilePair.second);
        }

        public Requisite(
                @NonNull LeaderboardDTO currentLeaderboardDTO,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            this(currentLeaderboardDTO.users == null || currentLeaderboardDTO.users.size() == 0
                            ? null
                            : currentLeaderboardDTO.users.get(0),
                    currentUserProfileDTO);
        }

        public Requisite(
                @Nullable LeaderboardUserDTO currentLeaderboardUserDTO,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            this.currentLeaderboardUserDTO = currentLeaderboardUserDTO;
            this.currentUserProfileDTO = currentUserProfileDTO;
        }
    }
}