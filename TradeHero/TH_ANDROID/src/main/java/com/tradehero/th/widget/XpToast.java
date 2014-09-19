package com.tradehero.th.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.level.LevelDefListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class XpToast extends RelativeLayout implements UserLevelProgressBar.UserLevelProgressBarListener
{
    @InjectView(R.id.xp_toast_text) TextSwitcher textSwitcher;
    @InjectView(R.id.xp_toast_value) TextView xpValue;
    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @Inject LevelDefListCache levelDefListCache;

    private LevelDefListId levelDefListId = new LevelDefListId();
    private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> mLevelDefListCacheListener = new LevelDefCacheListener();

    public XpToast(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //Always hidden after inflate
        setVisibility(View.GONE);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews()
    {
        levelDefListCache.register(levelDefListId, mLevelDefListCacheListener);
        levelDefListCache.getOrFetchAsync(levelDefListId);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override public View makeView()
            {
                return LayoutInflater.from(getContext()).inflate(R.layout.layout_xp_toast_text, textSwitcher, false);
            }
        });
    }

    public void showWhenReady(final UserXPAchievementDTO userXPAchievementDTO)
    {
        userLevelProgressBar.startsWith(userXPAchievementDTO.xpFrom);
        displayXPEarned(0);
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_in);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {
                setVisibility(View.VISIBLE);
                textSwitcher.reset();
            }

            @Override public void onAnimationEnd(Animation animation)
            {
                startXPAnimation(userXPAchievementDTO);
            }

            @Override public void onAnimationRepeat(Animation animation)
            {

            }
        });

        startAnimation(a);
    }

    private void startXPAnimation(UserXPAchievementDTO userXPAchievementDTO)
    {
        textSwitcher.setText(userXPAchievementDTO.text);

        if(userLevelProgressBar.getLevelDefDTOList() != null)
        {
            userLevelProgressBar.increment(userXPAchievementDTO.xpEarned);
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, userXPAchievementDTO.xpEarned);
        valueAnimator.setDuration(userLevelProgressBar.getRoughDuration());
        valueAnimator.setStartDelay(userLevelProgressBar.getStartDelay());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (Integer) animation.getAnimatedValue();
                displayXPEarned(value);
            }
        });
        valueAnimator.start();
        postDelayed(new Runnable()
        {
            @Override public void run()
            {
                hide();
            }
        }, 3000);
    }

    private void displayXPEarned(int value)
    {
        if(xpValue != null)
        {
            xpValue.setText(getContext().getString(R.string.achievement_xp_earned_format, THSignedNumber.builder(value).relevantDigitCount(1).withOutSign().build().toString()));
        }
    }

    public void hide()
    {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_out);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {

            }

            @Override public void onAnimationEnd(Animation animation)
            {
                setVisibility(View.GONE);
            }

            @Override public void onAnimationRepeat(Animation animation)
            {

            }
        });

        startAnimation(a);
    }

    private void setLevelDefList(LevelDefDTOList levelDefList)
    {
        if(userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefList);
        }
    }

    @Override public void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel)
    {

    }

    private class LevelDefCacheListener implements DTOCacheNew.HurriedListener<LevelDefListId, LevelDefDTOList>
    {

        @Override public void onPreCachedDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            setLevelDefList(value);
        }

        @Override public void onDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            setLevelDefList(value);
        }

        @Override public void onErrorThrown(@NotNull LevelDefListId key, @NotNull Throwable error)
        {

        }
    }
}
