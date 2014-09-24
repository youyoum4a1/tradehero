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
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.api.level.UserXPMultiplierDTO;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.fragments.level.LevelUpDialogFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.level.LevelDefListCache;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import java.util.ArrayDeque;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class XpToast extends RelativeLayout
        implements UserLevelProgressBar.UserLevelProgressBarLevelUpListener, UserLevelProgressBar.UserLevelProgressBarListener
{
    @InjectView(R.id.xp_toast_text) TextSwitcher xpTextSwitcher;
    @InjectView(R.id.xp_toast_value) TextView xpValue;
    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @Inject LevelDefListCache levelDefListCache;
    @Inject BroadcastUtils broadcastUtils;

    private LevelDefListId levelDefListId = new LevelDefListId();
    private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> mLevelDefListCacheListener = new LevelDefCacheListener();

    private ArrayDeque<LevelAnimationDefinition> levelAnimationDefinitions = new ArrayDeque<>();
    private LevelAnimationDefinition currentLevelAnimationDefinition;

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
        if (!isInEditMode())
        {
            initViews();
        }
    }

    private void initViews()
    {
        userLevelProgressBar.setPauseDurationWhenLevelUp(getResources().getInteger(R.integer.user_level_pause_on_level_up));
        userLevelProgressBar.setUserLevelProgressBarLevelUpListener(this);
        userLevelProgressBar.setUserLevelProgressBarListener(this);
        levelDefListCache.register(levelDefListId, mLevelDefListCacheListener);
        levelDefListCache.getOrFetchAsync(levelDefListId);
        xpTextSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override public View makeView()
            {
                return LayoutInflater.from(getContext()).inflate(R.layout.layout_xp_toast_text, xpTextSwitcher, false);
            }
        });
    }

    public void showWhenReady(UserXPAchievementDTO userXPAchievementDTO)
    {
        userLevelProgressBar.startsWith(userXPAchievementDTO.getBaseXp());
        populateAnimationLists(userXPAchievementDTO);
        showAll();
    }

    private void populateAnimationLists(UserXPAchievementDTO userXPAchievementDTO)
    {
        LevelAnimationDefinition l0 = new LevelAnimationDefinition(0, userXPAchievementDTO.xpEarned, userXPAchievementDTO.text);
        levelAnimationDefinitions.add(l0);
        if (userXPAchievementDTO.multiplier != null && !userXPAchievementDTO.multiplier.isEmpty())
        {
            int from = l0.to();
            for (UserXPMultiplierDTO userXPMultiplierDTO : userXPAchievementDTO.multiplier)
            {
                int to = from + userXPMultiplierDTO.xpTotal;
                LevelAnimationDefinition ln = new LevelAnimationDefinition(from, to - from,
                        getContext().getString(R.string.user_level_xp_multiplier_format, userXPMultiplierDTO.text, userXPMultiplierDTO.multiplier));
                levelAnimationDefinitions.add(ln);
                from = to;
            }
        }
    }

    private void showAll()
    {
        displayXPEarned(0);
        xpValue.clearAnimation();

        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                setVisibility(View.VISIBLE);
                xpTextSwitcher.reset();
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                startXPAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        startAnimation(a);
    }

    private void startXPAnimation()
    {
        currentLevelAnimationDefinition = levelAnimationDefinitions.pop();

        xpTextSwitcher.setText(currentLevelAnimationDefinition.text);

        if (userLevelProgressBar.getLevelDefDTOList() != null)
        {
            userLevelProgressBar.increment(currentLevelAnimationDefinition.earned);
        }
    }

    private void displayXPEarned(int value)
    {
        if (xpValue != null)
        {
            xpValue.setText(getContext().getString(R.string.achievement_xp_earned_format,
                    THSignedNumber.builder(value).relevantDigitCount(1).withOutSign().build().toString()));
        }
    }

    public void hide()
    {
        currentLevelAnimationDefinition = null;
        levelAnimationDefinitions.clear();
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {
            }

            @Override public void onAnimationEnd(Animation animation)
            {
                setVisibility(View.GONE);
                broadcastUtils.nextPlease();
            }

            @Override public void onAnimationRepeat(Animation animation)
            {
            }
        });

        startAnimation(a);
    }

    private void setLevelDefList(LevelDefDTOList levelDefList)
    {
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefList);
        }
    }

    @Override public void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel)
    {
        if (getContext() instanceof DashboardActivity)
        {
            LevelUpDialogFragment levelUpDialogFragment = LevelUpDialogFragment.newInstance(fromLevel.getId(), toLevel.getId());
            levelUpDialogFragment.show(((DashboardActivity) getContext()).getFragmentManager(), LevelUpDialogFragment.class.getName());
        }
    }

    @Override public void onIncrementStarted()
    {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(currentLevelAnimationDefinition.from, currentLevelAnimationDefinition.to());
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
    }

    @Override public void onIncrementEnded()
    {
        if (levelAnimationDefinitions.isEmpty())
        {
            //Add emphasize effect on the total xp value.
            Animation fade = AnimationUtils.loadAnimation(getContext(), R.anim.emphasize);
            xpValue.startAnimation(fade);

            postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    hide();
                }
            }, getResources().getInteger(R.integer.xp_level_toast_dismiss_delay));
        }
        else
        {
            startXPAnimation();
        }
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

    private class LevelAnimationDefinition
    {
        String text;
        int from;
        int earned;

        private LevelAnimationDefinition(int from, int earned, String text)
        {
            this.from = from;
            this.earned = earned;
            this.text = text;
        }

        private int to()
        {
            return from + earned;
        }
    }
}
