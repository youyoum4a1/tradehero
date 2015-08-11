package com.tradehero.th.fragments.achievement;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.AchievementCategoryId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.achievement.AchievementCategoryCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.AttributesEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AchievementDialogFragment extends AbstractAchievementDialogFragment
{
    private static final String PROPERTY_DOLLARS_EARNED = "dollarsEarned";

    @Bind(R.id.achievement_progress_indicator) AchievementProgressIndicator achievementProgressIndicator;
    @Bind(R.id.achievement_virtual_dollar_earned) TextView dollarEarned;

    @Inject CurrentUserId currentUserId;
    @Inject AchievementCategoryCacheRx achievementCategoryCache;
    @Inject Analytics analytics;

    //<editor-fold desc="Constructors">
    public AchievementDialogFragment()
    {
        super();
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.achievement_dialog_fragment, container, false);
    }

    @Override protected void init()
    {
        super.init();
        if (userAchievementDTO != null)
        {
            reportAnalytics(userAchievementDTO);
        }
    }

    @Override protected void initView()
    {
        super.initView();
        displayDollarsEarned(0f);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchCategoryCache();
    }

    @Override protected void onCreatePropertyValuesHolder(List<PropertyValuesHolder> propertyValuesHolders)
    {
        super.onCreatePropertyValuesHolder(propertyValuesHolders);
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            PropertyValuesHolder dollar =
                    PropertyValuesHolder.ofFloat(PROPERTY_DOLLARS_EARNED, 0f, (float) userAchievementDTOCopy.achievementDef.virtualDollars);
            propertyValuesHolders.add(dollar);
        }
    }

    @Override protected void handleBadgeSuccess()
    {
        super.handleBadgeSuccess();
        achievementProgressIndicator.delayedColorUpdate(mCurrentColor);
    }

    @Override @NonNull protected ValueAnimator.AnimatorUpdateListener createEarnedAnimatorUpdateListener()
    {
        return new AchievementValueAnimatorUpdateListener();
    }

    private void fetchCategoryCache()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            AchievementCategoryId achievementCategoryId = new AchievementCategoryId(
                    currentUserId.toUserBaseKey(),
                    userAchievementDTO.achievementDef.categoryId);
            AppObservable.bindSupportFragment(
                    this,
                    achievementCategoryCache.get(achievementCategoryId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Pair<AchievementCategoryId, AchievementCategoryDTO>>()
                            {
                                @Override public void call(
                                        Pair<AchievementCategoryId, AchievementCategoryDTO> pair)
                                {
                                    AchievementDialogFragment.this.onReceivedAchievementCategory(pair);
                                }
                            },
                            new EmptyAction1<Throwable>());
        }
    }

    public void onReceivedAchievementCategory(Pair<AchievementCategoryId, AchievementCategoryDTO> pair)
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            achievementProgressIndicator.setAchievementDef(pair.second.achievementDefs, userAchievementDTO.achievementDef.achievementLevel);
            achievementProgressIndicator.animateCurrentLevel();
        }
    }

    private void displayDollarsEarned(float dollars)
    {
        THSignedMoney.builder(dollars)
                .currency(SecurityUtils.DEFAULT_VIRTUAL_CASH_BONUS_CURRENCY_DISPLAY)
                .signTypePlusMinusAlways()
                .withSign()
                .relevantDigitCount(1)
                .build()
                .into(dollarEarned);
    }

    private void reportAnalytics(@NonNull UserAchievementDTO userAchievementDTOCopy)
    {
        Map<String, String> collections = new HashMap<>();
        collections.put(AnalyticsConstants.Trigger, AnalyticsConstants.Clicked);
        collections.put(AnalyticsConstants.Type, userAchievementDTOCopy.achievementDef.thName);
        collections.put(AnalyticsConstants.Level, String.valueOf(userAchievementDTOCopy.achievementDef.achievementLevel));
        analytics.fireEvent(new AttributesEvent(AnalyticsConstants.AchievementNotificationScreen, collections));
    }

    protected class AchievementValueAnimatorUpdateListener extends AbstractAchievementValueAnimatorUpdateListener
    {
        @Override public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator)
        {
            super.onAnimationUpdate(valueAnimator);
            Float value = (Float) valueAnimator.getAnimatedValue(PROPERTY_DOLLARS_EARNED);
            if (value != null)
            {
                displayDollarsEarned(value);
            }
        }
    }
}
