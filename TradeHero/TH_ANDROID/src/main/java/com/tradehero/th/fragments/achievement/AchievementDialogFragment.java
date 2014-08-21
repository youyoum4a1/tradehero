package com.tradehero.th.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.widget.UserLevelProgressBar;

public class AchievementDialogFragment extends AbstractAchievementDialogFragment
{
    private static final String PROPERTY_XP_EARNED = "xpEarned";
    private static final String PROPERTY_DOLLARS_EARNED = "dollarsEarned";

    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @InjectView(R.id.btn_achievement_share) Button btnShare;

    @InjectView(R.id.user_level_progress_level_up) TextView levelUp;
    @InjectView(R.id.user_level_progress_xp_earned) TextView xpEarned;
    @InjectView(R.id.user_level_progress_virtual_dollar_earned) TextView dollarEarned;

    @InjectView(R.id.achievement_xp_dollar_earned_container) ViewGroup xpDollarEarnedContainer;

    private float mXpEarnedOriginalY = -1;
    private long mMsLevelUpDelay;

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
        mMsLevelUpDelay = getResources().getInteger(R.integer.achievement_level_up_end_start_offset) - getResources().getInteger(
                R.integer.achievement_level_up_start_duration);
    }

    @Override protected void initView()
    {
        super.initView();
        displayXPDollarsEarned();
        initProgressBar();
    }

    private void displayXPDollarsEarned()
    {
        displayDollarsEarned(0f);
        displayXpEarned(0);
    }

    private void displayDollarsEarned(float dollars)
    {
        dollarEarned.setText(getString(R.string.achievement_virtual_dollars_earned_format, dollars));
    }

    private void displayXpEarned(int xp)
    {
        xpEarned.setText(getString(R.string.achievement_xp_earned_format, xp));
    }

    private void initProgressBar()
    {
        userLevelProgressBar.startsWith(userAchievementDTO.getBaseExp());
        userLevelProgressBar.setStartDelayOnLevelUp(mMsLevelUpDelay);
        userLevelProgressBar.setUserLevelProgressBarListener(createUserLevelProgressBarListener());
    }

    private void startAnimation()
    {
        PropertyValuesHolder dollar = PropertyValuesHolder.ofFloat(PROPERTY_DOLLARS_EARNED, 0f, (float) userAchievementDTO.achievementDef.virtualDollars);
        PropertyValuesHolder xp = PropertyValuesHolder.ofInt(PROPERTY_XP_EARNED, 0, userAchievementDTO.xpEarned);

        ValueAnimator anim = ValueAnimator.ofPropertyValuesHolder(xp, dollar);

        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                userLevelProgressBar.increment(400);
            }
        });

        anim.setDuration(getResources().getInteger(R.integer.achievement_earned_duration));
        anim.setInterpolator(new AccelerateInterpolator());

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                float value = (Float) valueAnimator.getAnimatedValue(PROPERTY_DOLLARS_EARNED);
                displayDollarsEarned(value);

                int xp = (Integer) valueAnimator.getAnimatedValue(PROPERTY_XP_EARNED);
                displayXpEarned(xp);
            }
        });

        anim.start();
    }

    @Override public void onDestroyView()
    {
        userLevelProgressBar.setUserLevelProgressBarListener(null);
        super.onDestroyView();
    }

    @OnClick(R.id.btn_achievement_share)
    public void onShareClicked()
    {
        startAnimation();
    }

    private void playLevelUpAnimation()
    {
        if (levelUp != null)
        {
            levelUp.setVisibility(View.VISIBLE);

            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.achievement_level_up);
            a.setAnimationListener(createLevelUpAnimationListener());
            levelUp.startAnimation(a);
        }
    }

    private LevelUpAnimationListener createLevelUpAnimationListener()
    {
        return new LevelUpAnimationListener();
    }

    private UserLevelProgressBar.UserLevelProgressBarListener createUserLevelProgressBarListener()
    {
        return new AchievementUserLevelProgressBarListener();
    }

    private class LevelUpAnimationListener implements Animation.AnimationListener
    {

        @Override public void onAnimationStart(Animation animation)
        {

        }

        @Override public void onAnimationEnd(Animation animation)
        {
            if (levelUp != null)
            {
                levelUp.setVisibility(View.GONE);
            }
        }

        @Override public void onAnimationRepeat(Animation animation)
        {

        }
    }

    protected class AchievementUserLevelProgressBarListener implements UserLevelProgressBar.UserLevelProgressBarListener
    {
        @Override public void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel)
        {
            playLevelUpAnimation();
        }
    }
}
