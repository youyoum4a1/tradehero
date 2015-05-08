package com.tradehero.th.fragments.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.widget.UserLevelProgressBar;
import timber.log.Timber;

public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    @InjectView(R.id.portfolio_title) @Optional protected TextView portfolioTitle;
    @InjectView(R.id.txt_total_wealth) @Optional protected TextView totalWealth;
    @InjectView(R.id.txt_additional_cash) @Optional protected TextView additionalCash;
    @InjectView(R.id.txt_cash_on_hand) @Optional protected TextView cashOnHand;
    @InjectView(R.id.user_profile_achievement_count) @Optional protected TextView achievementCount;
    @InjectView(R.id.user_level_progress_bar) @Optional protected UserLevelProgressBar userLevelProgressBar;
    @InjectView(R.id.user_statistic_view) @Optional protected UserStatisticView userStatisticView;

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

        // Total Wealth
        if (totalWealth != null)
        {
            if (userProfileDTO.portfolio != null)
            {
                THSignedMoney.builder(userProfileDTO.portfolio.totalValue)
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build()
                        .into(totalWealth);
            }
            else
            {
                totalWealth.setText(R.string.na);
            }
        }

        // Additional Cash
        if (additionalCash != null)
        {
            if (userProfileDTO.portfolio != null)
            {
                THSignedMoney.builder(userProfileDTO.portfolio.getTotalExtraCash())
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build()
                        .into(additionalCash);
            }
            else
            {
                additionalCash.setText(R.string.na);
            }
        }

        // Cash On Hand
        if (cashOnHand != null)
        {
            if (userProfileDTO.portfolio != null)
            {
                THSignedMoney.builder(userProfileDTO.portfolio.cashBalanceRefCcy)
                        .currency(userProfileDTO.portfolio.getNiceCurrency())
                        .build()
                        .into(cashOnHand);
            }
            else
            {
                cashOnHand.setText(R.string.na);
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
    @OnClick(R.id.user_profile_achievement_count_wrapper) @Optional
    @Override protected void notifyAchievementsClicked()
    {
        super.notifyAchievementsClicked();
    }
}
