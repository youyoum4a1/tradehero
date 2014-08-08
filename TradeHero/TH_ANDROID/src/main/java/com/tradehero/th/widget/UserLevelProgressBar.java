package com.tradehero.th.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.models.level.LevelDefUtil;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserLevelProgressBar extends RelativeLayout
{
    private static final long MS_PER_XP = 20;
    @InjectView(R.id.user_level_progress_next) protected TextView nextLevelLabel;
    @InjectView(R.id.user_level_progress_bar_indicator) protected TextView xpIndicatorLabel;
    @InjectView(R.id.user_level_progress_current) protected TextView currentLevelLabel;

    @InjectView(R.id.user_level_main_progress_bar) protected ProgressBar xpProgressBar;

    @Inject LevelDefUtil levelDefUtil;

    private int mCurrentXP = -1;
    private LevelDefDTO currentLevelDTO;
    private String xpFormat;

    private UserLevelProgressBarListener userLevelProgressBarListener;
    private AnimatorSet mAnimatorSet;
    private long mMsDelay;

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

        xpFormat = getContext().getString(R.string.user_level_xp_indicator_format);
    }

    public void startsWith(int current)
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
        currentLevelDTO = levelDefUtil.getCurrentLevel(mCurrentXP);
        xpProgressBar.setMax(getMaxProgress(currentLevelDTO));
        xpProgressBar.setProgress(currentXpToProgress());
        updateDisplay();
        updateXPIndicator(mCurrentXP);
    }

    private void updateDisplay()
    {
        currentLevelLabel.setText(String.valueOf(currentLevelDTO.level));
        nextLevelLabel.setText(String.valueOf(currentLevelDTO.level + 1));
    }

    public void setStartDelayOnLevelUp(long msDelay)
    {
        if (msDelay > 0)
        {
            this.mMsDelay = msDelay;
        }
    }

    public void increment(int xpGained)
    {
        if (currentLevelDTO == null)
        {
            throw new RuntimeException("Must call startsWith before calling increment!");
        }

        List<Animator> animators = getAnimatorQueue(xpGained);
        if (mAnimatorSet != null)
        {
            mAnimatorSet.end();
        }
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setInterpolator(new LinearInterpolator());
        mAnimatorSet.playSequentially(animators);
        mAnimatorSet.start();
    }

    private int getMaxProgress(LevelDefDTO levelDTO)
    {
        return levelDTO.xpTo - levelDTO.xpFrom;
    }

    private int currentXpToProgress()
    {
        return mCurrentXP - currentLevelDTO.xpFrom;
    }

    private int gainedXpToEndProgress(int currentXp, int xpGained, LevelDefDTO levelDTO)
    {
        if (currentXp + xpGained >= levelDTO.xpTo)
        {
            return getMaxProgress(levelDTO);
        }
        return xpGained;
    }

    private ValueAnimator.AnimatorUpdateListener createUpdateListener()
    {
        return new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                Integer xp = currentLevelDTO.xpFrom + (Integer) valueAnimator.getAnimatedValue("secondaryProgress");
                updateXPIndicator(xp);
            }
        };
    }

    private void updateXPIndicator(int xp)
    {
        mCurrentXP = xp;
        xpIndicatorLabel.setText(String.format(xpFormat, xp, currentLevelDTO.xpTo));
    }

    private List<Animator> getAnimatorQueue(int xpGained)
    {
        List<Animator> aList = new ArrayList<>();
        LevelDefDTO levelDefDTO = currentLevelDTO;

        PropertyValuesHolder p0Progress = PropertyValuesHolder.ofInt("progress", currentXpToProgress(), currentXpToProgress());
        PropertyValuesHolder p0SecondaryProgress = PropertyValuesHolder.ofInt("secondaryProgress", currentXpToProgress(),
                gainedXpToEndProgress(mCurrentXP, xpGained, currentLevelDTO));
        PropertyValuesHolder p0Max = PropertyValuesHolder.ofInt("max", getMaxProgress(currentLevelDTO), getMaxProgress(currentLevelDTO));

        ValueAnimator v0 =
                ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, p0Progress, p0SecondaryProgress, p0Max);
        v0.addUpdateListener(createUpdateListener());
        v0.setDuration(getAnimationDuration(currentXpToProgress(), gainedXpToEndProgress(mCurrentXP, xpGained, currentLevelDTO)));
        v0.addListener(createAnimationAdapter());
        aList.add(v0);

        int targetXp = mCurrentXP + xpGained;
        while (willLevelUp(targetXp, levelDefDTO))
        {
            LevelDefDTO nextLevelDTO = levelDefUtil.getNextLevelDTO(levelDefDTO.level);

            int xpGainedN = targetXp - nextLevelDTO.xpFrom;

            int startProgress = 0;

            PropertyValuesHolder pNa = PropertyValuesHolder.ofInt("progress", startProgress, startProgress);
            PropertyValuesHolder pNb = PropertyValuesHolder.ofInt("secondaryProgress", startProgress,
                    gainedXpToEndProgress(nextLevelDTO.xpFrom, xpGainedN, nextLevelDTO));
            PropertyValuesHolder pNc = PropertyValuesHolder.ofInt("max", getMaxProgress(nextLevelDTO), getMaxProgress(nextLevelDTO));

            ValueAnimator vN = ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, pNa, pNb, pNc);
            vN.addUpdateListener(createUpdateListener());
            vN.setDuration(getAnimationDuration(startProgress, gainedXpToEndProgress(nextLevelDTO.xpFrom, xpGainedN, nextLevelDTO)));
            vN.setStartDelay(mMsDelay);
            vN.addListener(createAnimationAdapter());

            aList.add(vN);

            levelDefDTO = nextLevelDTO;
        }

        return aList;
    }

    private boolean willLevelUp(int targetXp, LevelDefDTO levelDefDTO)
    {
        if (targetXp > levelDefDTO.xpTo)
        {
            return true;
        }
        return false;
    }

    private AnimatorListenerAdapter createAnimationAdapter()
    {
        return new AnimatorListenerAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                super.onAnimationStart(animation);
                if (xpProgressBar.getSecondaryProgress() >= xpProgressBar.getMax())
                {
                    UserLevelProgressBarListener userLevelProgressBarListenerCopy = userLevelProgressBarListener;
                    LevelDefDTO nextLevel = levelDefUtil.getNextLevelDTO(currentLevelDTO.level);
                    if (userLevelProgressBarListenerCopy != null)
                    {
                        userLevelProgressBarListenerCopy.onLevelUp(currentLevelDTO, nextLevel);
                    }
                    currentLevelDTO = nextLevel;
                    updateDisplay();
                }
            }
        };
    }

    private long getAnimationDuration(long startProgress, long endProgress)
    {
        long diff = (endProgress - startProgress) * MS_PER_XP;
        if (diff < 0)
        {
            diff = 0;
        }
        return diff;
    }

    public void setUserLevelProgressBarListener(UserLevelProgressBarListener userLevelProgressBarListener)
    {
        this.userLevelProgressBarListener = userLevelProgressBarListener;
    }

    public interface UserLevelProgressBarListener
    {
        void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel);
    }
}
