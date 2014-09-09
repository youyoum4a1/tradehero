package com.tradehero.th.fragments.achievement;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.key.AchievementCategoryId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.achievement.AchievementCategoryCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AchievementDialogFragment extends AbstractAchievementDialogFragment
{
    private static final String PROPERTY_DOLLARS_EARNED = "dollarsEarned";

    @InjectView(R.id.achievement_progress_indicator) AchievementProgressIndicator achievementProgressIndicator;

    @InjectView(R.id.achievement_virtual_dollar_earned) TextView dollarEarned;

    @Inject CurrentUserId currentUserId;
    @Inject AchievementCategoryCache achievementCategoryCache;

    private DTOCacheNew.Listener<AchievementCategoryId, AchievementCategoryDTO> mCategoryCacheListener;

    protected AchievementDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.achievement_dialog_fragment, container, false);
    }

    @Override protected void init()
    {
        super.init();
        mCategoryCacheListener = new CategoryCacheListener();
    }

    @Override protected void initView()
    {
        super.initView();
        displayDollarsEarned(0f);
    }

    @Override public void onStart()
    {
        super.onStart();
        attachCategoryCacheListener();
    }

    private void animateCurrentProgress()
    {
        getView().post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        achievementProgressIndicator.animateCurrentLevel();
                    }
                }
        );
    }

    @Override protected void onCreatePropertyValuesHolder(List<PropertyValuesHolder> propertyValuesHolders)
    {
        super.onCreatePropertyValuesHolder(propertyValuesHolders);
        PropertyValuesHolder dollar =
                PropertyValuesHolder.ofFloat(PROPERTY_DOLLARS_EARNED, 0f, (float) userAchievementDTO.achievementDef.virtualDollars);
        propertyValuesHolders.add(dollar);
    }

    @Override public void onStop()
    {
        detachCategoryCacheListener();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        mCategoryCacheListener = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override protected ValueAnimator.AnimatorUpdateListener createEarnedAnimatorUpdateListener()
    {
        return new AchievementValueAnimatorUpdateListener();
    }

    private void attachCategoryCacheListener()
    {
        AchievementCategoryId achievementCategoryId =
                new AchievementCategoryId(currentUserId.toUserBaseKey(), userAchievementDTO.achievementDef.categoryId);
        achievementCategoryCache.register(achievementCategoryId,
                mCategoryCacheListener);
        achievementCategoryCache.getOrFetchAsync(achievementCategoryId);
    }

    private void detachCategoryCacheListener()
    {
        achievementCategoryCache.unregister(mCategoryCacheListener);
    }

    @OnClick(R.id.btn_achievement_share)
    public void onShareClicked()
    {

    }

    private void displayDollarsEarned(float dollars)
    {
        dollarEarned.setText(
                THSignedMoney.builder(dollars).currency("TH$").signTypePlusMinusAlways().withSign().relevantDigitCount(1).build().toString());
    }

    private class CategoryCacheListener implements DTOCacheNew.Listener<AchievementCategoryId, AchievementCategoryDTO>
    {

        @Override public void onDTOReceived(@NotNull AchievementCategoryId key, @NotNull AchievementCategoryDTO value)
        {
            achievementProgressIndicator.setAchievementDef(value.achievementDefs, userAchievementDTO.achievementDef.achievementLevel);
            animateCurrentProgress();
        }

        @Override public void onErrorThrown(@NotNull AchievementCategoryId key, @NotNull Throwable error)
        {

        }
    }

    protected class AchievementValueAnimatorUpdateListener extends AbstractAchievementValueAnimatorUpdateListener
    {
        @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
        {
            super.onAnimationUpdate(valueAnimator);
            float value = (Float) valueAnimator.getAnimatedValue(PROPERTY_DOLLARS_EARNED);
            displayDollarsEarned(value);
        }
    }
}
