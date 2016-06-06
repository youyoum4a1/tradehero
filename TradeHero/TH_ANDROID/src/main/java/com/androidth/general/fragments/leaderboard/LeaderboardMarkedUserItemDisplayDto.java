package com.androidth.general.fragments.leaderboard;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.R;
import com.androidth.general.adapters.ExpandableItem;
import com.androidth.general.api.leaderboard.LeaderboardDTO;
import com.androidth.general.api.leaderboard.LeaderboardUserDTO;
import com.androidth.general.api.leaderboard.key.LeaderboardKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.timeline.UserStatisticView;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.utils.DateUtils;
import com.androidth.general.utils.StringUtils;

public class LeaderboardMarkedUserItemDisplayDto extends LeaderboardItemDisplayDTO implements com.androidth.general.common.persistence.DTO,
        ExpandableItem
{
    @NonNull final CurrentUserId currentUserId;
    @Nullable final LeaderboardUserDTO leaderboardUserDTO;
    @Nullable final UserStatisticView.DTO userStatisticsDto;
    final String lbmuDisplayName;
    @NonNull final CharSequence lbmuRoi;
    final int lbmuPositionColor;
    final String lbmuFoF;
    @Nullable public final String lbmuRoiPeriod;
    public int lbmuRoiPeriodVisibility;
    String lbmuDisplayPicture;
    @ViewVisibilityValue final int lbmuFoFVisibility;
    private boolean isFollowing;
    private boolean expanded;
    private boolean isMyOwnRanking;

    /**
     * This constructor can only be used for Header view (Current User Ranking) to show that we are loading required current user information
     */
    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId)
    {
        super(resources);
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
        this.lbmuRoiPeriod = null;
        this.lbmuRoiPeriodVisibility = View.GONE;
        this.isFollowing = false;
    }

    /**
     * This constructor can only be used for Header view (Current User Ranking) to show that the user is no ranked
     */
    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileDTO currentUserProfileDTO)
    {
        super(resources);
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
        this.lbmuRoiPeriod = null;
        this.lbmuRoiPeriodVisibility = View.GONE;
        this.isFollowing = false;
    }

    public LeaderboardMarkedUserItemDisplayDto(@NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull LeaderboardUserDTO leaderboardItem,
            @NonNull UserProfileDTO currentUserProfileDTO)
    {
        super(resources);
        /**
         * This might not be the correct ranking position; e.g. When the leaderboard is filtered, ordinalPosition is always 0,
         */
        setRanking(leaderboardItem.ordinalPosition + 1);

        this.currentUserId = currentUserId;
        this.leaderboardUserDTO = leaderboardItem;
        this.userStatisticsDto = new UserStatisticView.DTO(resources, leaderboardUserDTO, currentUserProfileDTO.mostSkilledLbmu);
        this.lbmuDisplayName = leaderboardItem.displayName;

        this.lbmuRoiPeriod = DateUtils.getDisplayableDate(resources, leaderboardItem.periodStartUtc, leaderboardItem.periodEndUtc);
        this.lbmuRoiPeriodVisibility = View.GONE;

        this.lbmuRoi = THSignedPercentage
                .builder(leaderboardItem.roiInPeriod * 100)
                .signTypePlusMinusAlways()
                .relevantDigitCount(3)
                .withDefaultColor()
                .build().createSpanned();

        this.lbmuPositionColor = currentUserId.get() == leaderboardItem.id ?
                resources.getColor(R.color.light_green_normal) :
                resources.getColor(R.color.text_primary);
        this.lbmuFoF = leaderboardItem.friendOfMarkupString;
        this.lbmuFoFVisibility = leaderboardItem.isIncludeFoF() != null
                && leaderboardItem.isIncludeFoF()
                && !StringUtils.isNullOrEmptyOrSpaces(leaderboardItem.friendOfMarkupString)
                ? View.VISIBLE
                : View.GONE;

        this.isFollowing = currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey());
        this.lbmuDisplayPicture = leaderboardItem.picture;
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

    public void setIsFollowing(boolean isFollowing)
    {
        this.isFollowing = isFollowing;
    }

    public boolean isFollowing()
    {
        return isFollowing;
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