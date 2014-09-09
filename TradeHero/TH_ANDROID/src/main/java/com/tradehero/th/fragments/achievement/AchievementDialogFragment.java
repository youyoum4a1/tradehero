package com.tradehero.th.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class AchievementDialogFragment extends AbstractAchievementDialogFragment
{
    private static final String PROPERTY_XP_EARNED = "xpEarned";
    private static final String PROPERTY_DOLLARS_EARNED = "dollarsEarned";
    private static final String PROPERTY_BTN_COLOR = "btnColor";

    @InjectView(R.id.btn_achievement_share) Button btnShare;

    @InjectView(R.id.achievement_progress_indicator) AchievementProgressIndicator achievementProgressIndicator;

    @InjectView(R.id.user_level_progress_xp_earned) TextView xpEarned;
    @InjectView(R.id.achievement_virtual_dollar_earned) TextView dollarEarned;

    @Inject CurrentUserId currentUserId;
    @Inject AchievementCategoryCache achievementCategoryCache;

    private DTOCacheNew.Listener<AchievementCategoryId, AchievementCategoryDTO> mCategoryCacheListener;

    private ValueAnimator mAnim;
    private ObjectAnimator btnColorAnimation;

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
        displayXpEarned(0);
        startAnimation();
    }

    private void displayXpEarned(int xp)
    {
        xpEarned.setText(getString(R.string.achievement_xp_earned_format, xp));
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

    protected void onCreatePropertyValuesHolder(List<PropertyValuesHolder> propertyValuesHolders)
    {
        PropertyValuesHolder dollar =
                PropertyValuesHolder.ofFloat(PROPERTY_DOLLARS_EARNED, 0f, (float) userAchievementDTO.achievementDef.virtualDollars);
        propertyValuesHolders.add(dollar);
        PropertyValuesHolder xp = PropertyValuesHolder.ofInt(PROPERTY_XP_EARNED, 0, userAchievementDTO.xpEarned);
        propertyValuesHolders.add(xp);
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
        if (mAnim != null)
        {
            mAnim.cancel();
            mAnim.removeAllUpdateListeners();
            mAnim.removeAllListeners();
            mAnim = null;
        }
        if (btnColorAnimation != null)
        {
            btnColorAnimation.cancel();
            btnColorAnimation.removeAllListeners();
            btnColorAnimation.removeAllUpdateListeners();
            btnColorAnimation = null;
        }
        super.onDestroyView();
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

    private void startAnimation()
    {
        List<PropertyValuesHolder> propertyValuesHolders = new ArrayList<>();
        this.onCreatePropertyValuesHolder(propertyValuesHolders);

        PropertyValuesHolder[] array = new PropertyValuesHolder[propertyValuesHolders.size()];

        mAnim = ValueAnimator.ofPropertyValuesHolder(propertyValuesHolders.toArray(array));

        mAnim.setStartDelay(getResources().getInteger(R.integer.achievement_animation_start_delay));
        mAnim.setDuration(getResources().getInteger(R.integer.achievement_earned_duration));
        mAnim.setInterpolator(new AccelerateInterpolator());

        mAnim.addListener(createAnimatorListenerAdapter());
        mAnim.addUpdateListener(createEarnedAnimatorUpdateListener());

        mAnim.start();
    }

    private void displayDollarsEarned(float dollars)
    {
        dollarEarned.setText(
                THSignedMoney.builder(dollars).currency("TH$").signTypePlusMinusAlways().withSign().relevantDigitCount(1).build().toString());
    }

    private void setShareButtonColor()
    {
        List<PropertyValuesHolder> propertyValuesHolders = graphicUtil.wiggleWiggle(1f);

        PropertyValuesHolder pvhColor = PropertyValuesHolder.ofObject(PROPERTY_BTN_COLOR,
                new ArgbEvaluator(),
                getResources().getColor(R.color.tradehero_blue),
                Color.WHITE,
                mCurrentColor);

        propertyValuesHolders.add(pvhColor);

        PropertyValuesHolder[] array = new PropertyValuesHolder[propertyValuesHolders.size()];

        btnColorAnimation = ObjectAnimator.ofPropertyValuesHolder(btnShare, propertyValuesHolders.toArray(array));
        btnColorAnimation.setDuration(getResources().getInteger(R.integer.achievement_share_button_animation_duration));
        btnColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                int color = (Integer) valueAnimator.getAnimatedValue(PROPERTY_BTN_COLOR);
                StateListDrawable drawable = graphicUtil.createStateListDrawable(getActivity(), color);
                int textColor = graphicUtil.getContrastingColor(color);
                graphicUtil.setBackground(btnShare, drawable);
                btnShare.setTextColor(textColor);
            }
        });
        btnColorAnimation.setStartDelay(getResources().getInteger(R.integer.achievement_share_button_animation_delay));
        btnColorAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        btnColorAnimation.start();
    }

    protected ValueAnimator.AnimatorUpdateListener createEarnedAnimatorUpdateListener()
    {
        return new AchievementValueAnimatorUpdateListener();
    }

    protected AnimatorListenerAdapter createAnimatorListenerAdapter()
    {
        return new AnimatorListenerAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                setShareButtonColor();
            }
        };
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

    protected class AchievementValueAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
    {
        @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
        {
            float value = (Float) valueAnimator.getAnimatedValue(PROPERTY_DOLLARS_EARNED);
            displayDollarsEarned(value);
            int xp = (Integer) valueAnimator.getAnimatedValue(PROPERTY_XP_EARNED);
            displayXpEarned(xp);
        }
    }
}
