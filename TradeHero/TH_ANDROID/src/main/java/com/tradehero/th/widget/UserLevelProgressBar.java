package com.tradehero.th.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.common.annotations.VisibleForTesting;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class UserLevelProgressBar extends RelativeLayout
{
    private static final long MS_PER_XP = 5;

    private static final String PROPERTY_MAX = "max";
    private static final String PROPERTY_PROGRESS = "progress";
    private static final String PROPERTY_SECONDARY_PROGRESS = "secondaryProgress";

    @InjectView(R.id.user_level_progress_next) protected ImageView nextLevelLabel;
    @InjectView(R.id.user_level_progress_current) protected ImageView currentLevelLabel;

    @InjectView(R.id.user_level_main_progress_bar) protected ProgressBar xpProgressBar;

    @Inject Picasso picasso;

    private int mCurrentXP = -1;
    private UserLevelProgressBarListener userLevelProgressBarListener;

    private AnimatorSet mAnimatorSet;
    private long mMsDelay;
    private LevelDefDTO mCurrentLevelDTO;
    private LevelDefDTO mMaxLevelDTO;
    private LevelDefDTOList mLevelDefDTOList;

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

    public LevelDefDTOList getLevelDefDTOList()
    {
        return mLevelDefDTOList;
    }

    public void setLevelDefDTOList(@NotNull LevelDefDTOList mLevelDefDTOList)
    {
        this.mLevelDefDTOList = mLevelDefDTOList;
        mMaxLevelDTO = mLevelDefDTOList.getMaxLevelDTO();
    }

    public void startsWith(int current)
    {
        if (mLevelDefDTOList == null)
        {
            throw new RuntimeException("Must call setLevelDefDTOList before calling starts");
        }

        if (current < 0)
        {
            throw new RuntimeException("Current XP must not be negative");
        }

        this.mCurrentXP = current;
        determineLevel();
    }

    private void determineLevel()
    {
        if (mLevelDefDTOList != null)
        {
            mCurrentLevelDTO = mLevelDefDTOList.findCurrentLevel(mCurrentXP);
            xpProgressBar.setMax(getMaxProgress(mCurrentLevelDTO));
            xpProgressBar.setProgress(currentXpToProgress());
            xpProgressBar.setSecondaryProgress(currentXpToProgress());
            updateLevelDisplay();
            if (mCurrentLevelDTO.equals(mMaxLevelDTO))
            {
                setBarProgressAtMax();
            }
        }
        else
        {
            //TODO
        }
    }

    private void setBarProgressAtMax()
    {
        xpProgressBar.setMax(getMaxProgress(mCurrentLevelDTO));
    }

    private void updateLevelDisplay()
    {
        if (mCurrentLevelDTO != null)
        {
            picasso.load(mCurrentLevelDTO.badge).into(currentLevelLabel);
            if (mCurrentLevelDTO.equals(mMaxLevelDTO))
            {
                hideNextLevel();
            }
            else if (mLevelDefDTOList != null)
            {
                LevelDefDTO nextLevel = mLevelDefDTOList.getNextLevelDTO(mCurrentLevelDTO.level);
                if (nextLevel != null)
                {
                    picasso.load(nextLevel.badge).into(nextLevelLabel);
                }
                else
                {
                    hideNextLevel();
                }
            }
        }
    }

    private void hideNextLevel()
    {
        nextLevelLabel.setVisibility(View.GONE);
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
        if (mCurrentLevelDTO == null)
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
        mAnimatorSet.setStartDelay(1000l);
        mAnimatorSet.start();
    }

    private int getMaxProgress(LevelDefDTO levelDTO)
    {
        return levelDTO.xpTo - levelDTO.xpFrom;
    }

    private int currentXpToProgress()
    {
        return mCurrentXP - mCurrentLevelDTO.xpFrom;
    }

    private int gainedXpToEndProgress(int currentXp, int xpGained, LevelDefDTO levelDTO)
    {
        if (currentXp + xpGained >= levelDTO.xpTo)
        {
            return getMaxProgress(levelDTO);
        }
        return currentXp + xpGained - levelDTO.xpFrom;
    }

    private ValueAnimator.AnimatorUpdateListener createUpdateListener()
    {
        return new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                Integer xp = mCurrentLevelDTO.xpFrom + (Integer) valueAnimator.getAnimatedValue(PROPERTY_SECONDARY_PROGRESS);
                setCurrentXp(xp);
            }
        };
    }

    private void setCurrentXp(int xp)
    {
        mCurrentXP = xp;
    }

    @VisibleForTesting
    public List<Animator> getAnimatorQueue(int xpGained)
    {
        List<Animator> aList = new ArrayList<>();

        int currentXPProgress = currentXpToProgress();

        PropertyValuesHolder p0Progress = PropertyValuesHolder.ofInt(PROPERTY_PROGRESS, currentXPProgress, currentXPProgress);
        PropertyValuesHolder p0SecondaryProgress = PropertyValuesHolder.ofInt(PROPERTY_SECONDARY_PROGRESS, currentXPProgress,
                gainedXpToEndProgress(mCurrentXP, xpGained, mCurrentLevelDTO));
        PropertyValuesHolder p0Max = PropertyValuesHolder.ofInt(PROPERTY_MAX, getMaxProgress(mCurrentLevelDTO), getMaxProgress(mCurrentLevelDTO));

        ValueAnimator v0 =
                ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, p0Progress, p0SecondaryProgress, p0Max);
        v0.addUpdateListener(createUpdateListener());
        v0.setDuration(getAnimationDuration(currentXPProgress, gainedXpToEndProgress(mCurrentXP, xpGained, mCurrentLevelDTO)));
        aList.add(v0);

        int targetXp = mCurrentXP + xpGained;
        LevelDefDTO levelDefDTO = mCurrentLevelDTO;
        if (willLevelUp(targetXp, levelDefDTO))
        {
            v0.addListener(createAnimationAdapter());
        }

        while (willLevelUp(targetXp, levelDefDTO))
        {
            LevelDefDTO nextLevelDTO = mLevelDefDTOList.getNextLevelDTO(levelDefDTO.level);
            if (mLevelDefDTOList.isMaxLevel(nextLevelDTO) || nextLevelDTO == null)
            {
                break;
            }

            int xpGainedN = targetXp - nextLevelDTO.xpFrom;
            int startProgress = 0;

            PropertyValuesHolder pNa = PropertyValuesHolder.ofInt(PROPERTY_PROGRESS, startProgress, startProgress);
            PropertyValuesHolder pNb = PropertyValuesHolder.ofInt(PROPERTY_SECONDARY_PROGRESS, startProgress,
                    gainedXpToEndProgress(nextLevelDTO.xpFrom, xpGainedN, nextLevelDTO));
            PropertyValuesHolder pNc = PropertyValuesHolder.ofInt(PROPERTY_MAX, getMaxProgress(nextLevelDTO), getMaxProgress(nextLevelDTO));

            ValueAnimator vN = ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, pNa, pNb, pNc);
            vN.addUpdateListener(createUpdateListener());
            vN.setDuration(getAnimationDuration(startProgress, gainedXpToEndProgress(nextLevelDTO.xpFrom, xpGainedN, nextLevelDTO)));
            vN.setStartDelay(mMsDelay);
            if (willLevelUp(targetXp, nextLevelDTO))
            {
                vN.addListener(createAnimationAdapter());
            }

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

    private Animator.AnimatorListener createAnimationAdapter()
    {
        return new Animator.AnimatorListener()
        {

            @Override public void onAnimationStart(Animator animator)
            {

            }

            @Override public void onAnimationEnd(Animator animator)
            {
                if (xpProgressBar.getSecondaryProgress() >= xpProgressBar.getMax())
                {
                    UserLevelProgressBarListener userLevelProgressBarListenerCopy = userLevelProgressBarListener;
                    LevelDefDTO nextLevel = mLevelDefDTOList.getNextLevelDTO(mCurrentLevelDTO.level);
                    if (userLevelProgressBarListenerCopy != null)
                    {
                        userLevelProgressBarListenerCopy.onLevelUp(mCurrentLevelDTO, nextLevel);
                    }
                    mCurrentLevelDTO = nextLevel;
                    updateLevelDisplay();
                }
            }

            @Override public void onAnimationCancel(Animator animator)
            {

            }

            @Override public void onAnimationRepeat(Animator animator)
            {

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

    public String getCurrentLevel()
    {
        return String.valueOf(mCurrentLevelDTO.level);
    }

    public String getNextLevel()
    {
        return String.valueOf(mLevelDefDTOList.getNextLevelDTO(mCurrentLevelDTO.level).level);
    }

    public interface UserLevelProgressBarListener
    {
        void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel);
    }
}
