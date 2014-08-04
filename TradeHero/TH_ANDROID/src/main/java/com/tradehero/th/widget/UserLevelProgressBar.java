package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.models.level.LevelDTO;
import com.tradehero.th.models.level.LevelUtil;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayDeque;
import java.util.Queue;
import javax.inject.Inject;

public class UserLevelProgressBar extends RelativeLayout
{
    @InjectView(R.id.user_level_progress_next) protected TextView nextLevelLabel;
    @InjectView(R.id.user_level_progress_bar_indicator) protected TextView xpIndicatorLabel;
    @InjectView(R.id.user_level_progress_current) protected TextView currentLevelLabel;

    @InjectView(R.id.user_level_progress_bar) protected ProgressBar xpProgressBar;

    @Inject LevelUtil levelUtil;

    private int mCurrentXP = -1;
    private LevelDTO currentLevelDTO;

    private UserLevelProgressBarListener userLevelProgressBarListener;

    public UserLevelProgressBar(Context context)
    {
        super(context);
    }

    public UserLevelProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UserLevelProgressBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    public void setXpIndicator(int current)
    {
        if (current < 0)
        {
            throw new RuntimeException("Current XP must not be negative");
        }

        this.mCurrentXP = current;
        determineLevel();
    }

    private void determineLevel()
    {
        currentLevelDTO = levelUtil.getCurrentLevel(mCurrentXP);
        xpProgressBar.setMax(getMaxProgress(currentLevelDTO));
        xpProgressBar.setProgress(getCurrentXpProgress());
    }

    public void increment(int xpGained)
    {
        if (currentLevelDTO == null)
        {
            throw new RuntimeException("Must call setXPIndicator before calling increment!");
        }

        Queue<UserLevelProgressBarAnimationDefinition> repeatAnimationQueue = getRepeatAnimationQueue(xpGained);

        ProgressBarAnimation animation = new ProgressBarAnimation(xpProgressBar, repeatAnimationQueue);
        xpProgressBar.startAnimation(animation);
    }

    private int getMaxProgress(LevelDTO levelDTO)
    {
        return levelDTO.getMaxXp() - levelDTO.getBaseXp();
    }

    private int getCurrentXpProgress()
    {
        return xpToProgress(mCurrentXP, currentLevelDTO);
    }

    private int xpToProgress(int xp, LevelDTO levelDTO)
    {
        return xp - levelDTO.getBaseXp();
    }

    private Queue<UserLevelProgressBarAnimationDefinition> getRepeatAnimationQueue(int xpGained)
    {
        ArrayDeque<UserLevelProgressBarAnimationDefinition> repeatAnimationAnimation = new ArrayDeque<>();

        int targetXp = mCurrentXP + xpGained;
        LevelDTO levelDTO = currentLevelDTO;

        UserLevelProgressBarAnimationDefinition a0 =
                new UserLevelProgressBarAnimationDefinition(getCurrentXpProgress(), xpToProgress(targetXp, currentLevelDTO), getMaxProgress(currentLevelDTO), currentLevelDTO);
        repeatAnimationAnimation.add(a0);

        while (targetXp >= levelDTO.getMaxXp())
        {
            targetXp -= levelDTO.getMaxXp();
            levelDTO = levelUtil.getNextLevel(levelDTO.getCurrentLevel());
            UserLevelProgressBarAnimationDefinition
                    a = new UserLevelProgressBarAnimationDefinition(0, xpToProgress(targetXp, levelDTO), getMaxProgress(levelDTO), levelDTO);
            repeatAnimationAnimation.add(a);
        }

        return repeatAnimationAnimation;
    }

    public void setUserLevelProgressBarListener(UserLevelProgressBarListener userLevelProgressBarListener)
    {
        this.userLevelProgressBarListener = userLevelProgressBarListener;
    }

    protected class ProgressBarAnimation extends Animation
    {
        private ProgressBar progressBar;
        private UserLevelProgressBarAnimationDefinition animationDefinition;

        public ProgressBarAnimation(ProgressBar progressBar, final Queue<UserLevelProgressBarAnimationDefinition> animationQueue)
        {
            super();
            setInterpolator(new AccelerateInterpolator());
            this.progressBar = progressBar;
            this.animationDefinition = animationQueue.poll();
            this.setRepeatCount(animationQueue.size());

            this.setAnimationListener(new AnimationListener()
            {
                @Override public void onAnimationStart(Animation animation)
                {
                    xpProgressBar.setProgress(animationDefinition.startFrom);
                }

                @Override public void onAnimationEnd(Animation animation)
                {
                }

                @Override public void onAnimationRepeat(Animation animation)
                {
                    animationDefinition = animationQueue.poll();
                }
            });
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            super.applyTransformation(interpolatedTime, t);
            int value = (int) (animationDefinition.startFrom + (animationDefinition.finishAt - animationDefinition.startFrom) * interpolatedTime);

            progressBar.setSecondaryProgress(value);
            if(userLevelProgressBarListener != null)
            {
                //userLevelProgressBarListener.onLevelUp();
            }
        }
    }

    public interface UserLevelProgressBarListener
    {
        void onLevelUp(LevelDTO fromLevel, LevelDTO toLevel);
    }

    protected class UserLevelProgressBarAnimationDefinition
    {
        int startFrom;
        int finishAt;
        int maxValue;
        LevelDTO levelDTO;

        public UserLevelProgressBarAnimationDefinition(int startFrom, int finishAt, int maxValue, LevelDTO levelDTO)
        {
            this.startFrom = startFrom;
            this.finishAt = finishAt;
            this.maxValue = maxValue;
            this.levelDTO = levelDTO;

            if (finishAt >= maxValue)
            {
                this.finishAt = maxValue;
            }
        }
    }
}
