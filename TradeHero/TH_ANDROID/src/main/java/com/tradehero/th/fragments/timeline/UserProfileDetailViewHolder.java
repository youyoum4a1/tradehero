package com.tradehero.th.fragments.timeline;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THApp;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.widget.UserLevelProgressBar;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class UserProfileDetailViewHolder extends UserProfileCompactViewHolder
{
    @InjectView(R.id.profile_screen_user_detail_top) @Optional protected View profileTop;
    @InjectView(R.id.txt_total_wealth) @Optional protected TextView totalWealth;
    @InjectView(R.id.txt_additional_cash) @Optional protected TextView additionalCash;
    @InjectView(R.id.txt_cash_on_hand) @Optional protected TextView cashOnHand;
    @InjectView(R.id.user_profile_achievement_count) @Optional protected TextView achievementCount;
    @InjectView(R.id.user_level_progress_bar) @Optional protected UserLevelProgressBar userLevelProgressBar;

    @InjectView(R.id.user_statistic_view) @Optional protected UserStatisticView userStatisticView;

    @Inject LevelDefListCacheRx levelDefListCache;

    protected Runnable displayTopViewBackgroundRunnable;
    private Subscription levelDefDTOListSubscription;
    private UserStatisticView.DTO userStatisticsDto;

    public UserProfileDetailViewHolder(View view)
    {
        super(view);
    }

    @Override public void initViews(View view)
    {
        super.initViews(view);
        LevelDefListId levelDefListId = new LevelDefListId();
        if (!view.isInEditMode())
        {
            levelDefDTOListSubscription = levelDefListCache.getOne(levelDefListId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new LevelDefListCacheObserver());
        }
    }

    @Override public void detachViews()
    {
        if (profileTop != null)
        {
            profileTop.removeCallbacks(displayTopViewBackgroundRunnable);
        }
        levelDefDTOListSubscription.unsubscribe();
        levelDefDTOListSubscription = null;
        super.detachViews();
    }

    @Override public void display(final UserProfileDTO dto)
    {
        super.display(dto);
        displayTotalWealth();
        displayAdditionalCash();
        displayCashOnHand();
        displayAchievementCount();
        displayLevelProgress();
        displayUserStatic();
    }

    protected void displayUserStatic()
    {
        initUserStaticView();
        if (userStatisticsDto != null)
        {
            userStatisticView.display(userStatisticsDto);
        }
    }

    protected void displayTotalWealth()
    {
        if (totalWealth != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
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
    }

    protected void displayAdditionalCash()
    {
        if (additionalCash != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
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
    }

    protected void displayCashOnHand()
    {
        if (cashOnHand != null)
        {
            if (userProfileDTO != null && userProfileDTO.portfolio != null)
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
    }

    protected void displayAchievementCount()
    {
        if (achievementCount != null)
        {
            if (userProfileDTO != null)
            {
                achievementCount.setText(String.valueOf(userProfileDTO.achievementCount));
            }
            else
            {
                achievementCount.setText(R.string.na);
            }
        }
    }

    protected void setLevelDef(LevelDefDTOList levelDefDTOList)
    {
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefDTOList);
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

    public void setVisibility(int visibility)
    {
        if (visibility == View.VISIBLE
                && displayTopViewBackgroundRunnable != null
                && profileTop != null)
        {
            profileTop.post(displayTopViewBackgroundRunnable);
        }
    }

    protected class LevelDefListCacheObserver implements Observer<Pair<LevelDefListId, LevelDefDTOList>>
    {
        @Override public void onNext(Pair<LevelDefListId, LevelDefDTOList> pair)
        {
            setLevelDef(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (userLevelProgressBar != null)
            {
                userLevelProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    @SuppressWarnings("EmptyMethod")
    @OnClick(R.id.user_profile_achievement_count_wrapper) @Optional
    @Override protected void notifyDefaultPortfolioClicked()
    {
        super.notifyDefaultPortfolioClicked();
    }

    public void initUserStaticView()
    {
        if (userStatisticsDto != null) return;
        if (userProfileDTO != null)
        {
            LeaderboardUserDTO leaderboardUserDTO;
            try
            {
                leaderboardUserDTO = userProfileDTO.mostSkilledLbmu.getList().get(0);
                if (leaderboardUserDTO != null)
                {
                    userStatisticsDto = new UserStatisticView.DTO(THApp.context().getResources(), leaderboardUserDTO, userProfileDTO.mostSkilledLbmu);
                }
            }
            catch (Exception e)
            {
                Timber.e("initUserStaticView error:" + e.toString());
            }
        }
    }
}
