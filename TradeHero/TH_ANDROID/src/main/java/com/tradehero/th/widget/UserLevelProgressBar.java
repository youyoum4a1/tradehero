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
import com.tradehero.th.models.level.LevelDTO;
import com.tradehero.th.models.level.LevelUtil;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class UserLevelProgressBar extends RelativeLayout
{
    private static final long MS_PER_XP = 20;
    @InjectView(R.id.user_level_progress_next) protected TextView nextLevelLabel;
    @InjectView(R.id.user_level_progress_bar_indicator) protected TextView xpIndicatorLabel;
    @InjectView(R.id.user_level_progress_current) protected TextView currentLevelLabel;

    @InjectView(R.id.user_level_main_progress_bar) protected ProgressBar xpProgressBar;

    @Inject LevelUtil levelUtil;

    private int mCurrentXP = -1;
    private LevelDTO currentLevelDTO;
    private String xpFormat;

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
        currentLevelDTO = levelUtil.getCurrentLevel(mCurrentXP);
        xpProgressBar.setMax(getMaxProgress(currentLevelDTO));
        xpProgressBar.setProgress(currentXpToNormalisedProgress());
        updateDisplay();
        updateXPIndicator(mCurrentXP);
    }

    private void updateDisplay()
    {
        currentLevelLabel.setText(String.valueOf(currentLevelDTO.getCurrentLevel()));
        nextLevelLabel.setText(String.valueOf(currentLevelDTO.getCurrentLevel() + 1));
    }

    public void increment(int xpGained)
    {
        if (currentLevelDTO == null)
        {
            throw new RuntimeException("Must call startsWith before calling increment!");
        }

        List<Animator> animators = getAnimatorQueue(xpGained);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.playSequentially(animators);
        animatorSet.start();
    }

    private int getMaxProgress(LevelDTO levelDTO)
    {
        return levelDTO.getMaxXp() - levelDTO.getBaseXp();
    }

    private int currentXpToNormalisedProgress()
    {
        return mCurrentXP - currentLevelDTO.getBaseXp();
    }

    private int gainedXpToNormalisedProgress(int currentXp, int xpGained, LevelDTO levelDTO)
    {
        if(currentXp + xpGained >= levelDTO.getMaxXp())
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
                Integer xp = currentLevelDTO.getBaseXp() + (Integer) valueAnimator.getAnimatedValue("secondaryProgress");
                updateXPIndicator(xp);
            }
        };
    }

    private void updateXPIndicator(int xp)
    {
        mCurrentXP = xp;
        xpIndicatorLabel.setText(String.format(xpFormat, xp, currentLevelDTO.getMaxXp()));
    }

    private List<Animator> getAnimatorQueue(int xpGained)
    {
        List<Animator> aList = new ArrayList<>();
        LevelDTO levelDTO = currentLevelDTO;

        PropertyValuesHolder p0a = PropertyValuesHolder.ofInt("progress", currentXpToNormalisedProgress(), currentXpToNormalisedProgress());
        PropertyValuesHolder p0b = PropertyValuesHolder.ofInt("secondaryProgress", currentXpToNormalisedProgress(),
                gainedXpToNormalisedProgress(mCurrentXP, xpGained, currentLevelDTO));
        PropertyValuesHolder p0c = PropertyValuesHolder.ofInt("max", getMaxProgress(currentLevelDTO), getMaxProgress(currentLevelDTO));

        Timber.d("Updating level: %d, baseExp: %d, maxExp: %d, from with Primary: %d, Secondary from: %d to: %d, MaxProgress: %d, xpGained: %d",
                currentLevelDTO.getCurrentLevel(), currentLevelDTO.getBaseXp(), currentLevelDTO.getMaxXp(), currentXpToNormalisedProgress(),
                currentXpToNormalisedProgress(), gainedXpToNormalisedProgress(mCurrentXP, xpGained, currentLevelDTO), getMaxProgress(currentLevelDTO), xpGained);

        ValueAnimator v0 =
                ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, p0a, p0b, p0c);
        v0.addUpdateListener(createUpdateListener());
        v0.setDuration(getAnimationDuration(currentXpToNormalisedProgress(), gainedXpToNormalisedProgress(mCurrentXP, xpGained, currentLevelDTO)));
        aList.add(v0);

        int targetXp = mCurrentXP + xpGained;
        while (targetXp >= levelDTO.getMaxXp())
        {
            int xpGainedN = targetXp - levelDTO.getMaxXp();

            LevelDTO nextLevelDTO = levelUtil.getNextLevelDTO(levelDTO.getCurrentLevel());

            int startProgress = 0;

            PropertyValuesHolder pNa = PropertyValuesHolder.ofInt("progress", startProgress, startProgress);
            PropertyValuesHolder pNb = PropertyValuesHolder.ofInt("secondaryProgress", startProgress,
                    gainedXpToNormalisedProgress(nextLevelDTO.getBaseXp(), xpGainedN, nextLevelDTO));
            PropertyValuesHolder pNc = PropertyValuesHolder.ofInt("max", getMaxProgress(nextLevelDTO), getMaxProgress(nextLevelDTO));

            Timber.d("Updating level: %d, baseExp: %d, maxExp: %d, from with Primary: %d, Secondary from: %d to: %d, MaxProgress: %d, xpGained: %d",
                    nextLevelDTO.getCurrentLevel(), nextLevelDTO.getBaseXp(), nextLevelDTO.getMaxXp(), startProgress,
                    startProgress, gainedXpToNormalisedProgress(nextLevelDTO.getBaseXp(), xpGainedN, nextLevelDTO), getMaxProgress(nextLevelDTO), xpGainedN);

            ValueAnimator vN = ObjectAnimator.ofPropertyValuesHolder(xpProgressBar, pNa, pNb, pNc);
            vN.addUpdateListener(createUpdateListener());
            vN.setDuration(getAnimationDuration(startProgress, gainedXpToNormalisedProgress(nextLevelDTO.getBaseXp(), xpGainedN, nextLevelDTO)));
            vN.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationStart(Animator animation)
                {
                    super.onAnimationStart(animation);
                    UserLevelProgressBarListener userLevelProgressBarListenerCopy = userLevelProgressBarListener;
                    LevelDTO nextLevel = levelUtil.getNextLevelDTO(currentLevelDTO.getCurrentLevel());
                    if (userLevelProgressBarListenerCopy != null)
                    {
                        userLevelProgressBarListenerCopy.onLevelUp(currentLevelDTO, nextLevel);
                    }
                    currentLevelDTO = nextLevel;
                    updateDisplay();
                }
            });

            aList.add(vN);

            levelDTO = nextLevelDTO;
        }

        return aList;
    }

    private long getAnimationDuration(int startProgress, int endProgress)
    {
        long diff = ((long) endProgress - (long) startProgress) * MS_PER_XP;
        if(diff < 0)
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
        void onLevelUp(LevelDTO fromLevel, LevelDTO toLevel);
    }
}
