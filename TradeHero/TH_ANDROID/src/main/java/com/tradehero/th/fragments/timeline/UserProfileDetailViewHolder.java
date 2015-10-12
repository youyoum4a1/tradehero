package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.widget.UserLevelProgressBar;
import timber.log.Timber;

public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    @Bind(R.id.portfolio_title) @Nullable protected TextView portfolioTitle;
    @Bind(R.id.user_profile_achievement_count) @Nullable protected TextView achievementCount;
    @Bind(R.id.user_level_progress_bar) @Nullable protected UserLevelProgressBar userLevelProgressBar;
    @Bind(R.id.user_statistic_view) @Nullable protected UserStatisticView userStatisticView;

    //<editor-fold desc="Constructors">
    public UserProfileDetailViewHolder(@NonNull Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override public void display(@NonNull final UserProfileDTO userProfileDTO)
    {
        super.display(userProfileDTO);

        // Portfolio Title
        if (portfolioTitle != null)
        {
            if (userProfileDTO.portfolio != null)
            {
                portfolioTitle.setText(userProfileDTO.portfolio.title);
            }
            else
            {
                portfolioTitle.setText(R.string.na);
            }
        }

        // Achievement Count
        if (achievementCount != null)
        {
            achievementCount.setText(String.valueOf(userProfileDTO.achievementCount));
        }

        // Statistics
        UserStatisticView.DTO userStatisticsDto = null;
        LeaderboardUserDTO leaderboardUserDTO;
        try
        {
            leaderboardUserDTO = userProfileDTO.mostSkilledLbmu.getList().get(0);
            if (leaderboardUserDTO != null)
            {
                userStatisticView.setVisibility(View.VISIBLE);
                userStatisticsDto = new UserStatisticView.DTO(THApp.context().getResources(), leaderboardUserDTO, userProfileDTO.mostSkilledLbmu);
            }
        } catch (Exception e)
        {
            Timber.d("initUserStaticView error:" + e.toString());
            if (userStatisticView != null)
            {
                userStatisticView.setVisibility(View.GONE);
            }
        }
        if (userStatisticView != null && userStatisticsDto != null)
        {
            userStatisticView.display(userStatisticsDto);
        }

        displayLevelProgress();
    }

    public void setLevelDef(@NonNull LevelDefDTOList levelDefDTOList)
    {
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefDTOList);
            userLevelProgressBar.setVisibility(View.VISIBLE);
        }
        displayLevelProgress();
    }

    protected void displayLevelProgress()
    {
        if (userProfileDTO != null && userLevelProgressBar != null && userLevelProgressBar.getLevelDefDTOList() != null)
        {
            userLevelProgressBar.startsWith(userProfileDTO.currentXP);
        }
    }

    @SuppressWarnings("EmptyMethod")
    @OnClick(R.id.user_profile_achievement_count_wrapper) @Nullable
    @Override protected void notifyAchievementsClicked()
    {
        super.notifyAchievementsClicked();
    }
}
