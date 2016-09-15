package com.androidth.general.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
import com.androidth.general.api.level.LevelDefDTO;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.level.UserXPAchievementDTO;
import com.androidth.general.api.level.UserXPMultiplierDTO;
import com.androidth.general.api.level.key.LevelDefListId;
import com.androidth.general.fragments.level.LevelUpDialogFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.persistence.level.LevelDefListCacheRx;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import java.util.ArrayDeque;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class XpToast extends RelativeLayout
        implements UserLevelProgressBar.UserLevelProgressBarLevelUpListener, UserLevelProgressBar.UserLevelProgressBarListener
{
    @Bind(R.id.xp_toast_text) TextView xpTextSwitcher; // TODO reinstate it as a Switcher
    @Bind(R.id.xp_toast_value) TextView xpValue;
    @Bind(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @Inject LevelDefListCacheRx levelDefListCache;
    @Inject BroadcastUtils broadcastUtils;

    @NonNull private LevelDefListId levelDefListId = new LevelDefListId();
    private Subscription mLevelDefListCacheSubscription;

    @NonNull private ArrayDeque<LevelAnimationDefinition> levelAnimationDefinitions = new ArrayDeque<>();
    private LevelAnimationDefinition currentLevelAnimationDefinition;
    @Nullable private UserXPAchievementDTO xpToBePlayed;
    private boolean isLevelDefError;

    //<editor-fold desc="Constructors">
    public XpToast(@NonNull Context context, AttributeSet attrs)
    {
        super(context, attrs);
        //Always hidden after creation
        setVisibility(View.GONE);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        if (!isInEditMode())
        {
            userLevelProgressBar.setPauseDurationWhenLevelUp(getResources().getInteger(R.integer.user_level_pause_on_level_up));
            //xpTextSwitcher.setFactory(new ViewSwitcher.ViewFactory()
            //{
            //    @Override public View makeView()
            //    {
            //        return LayoutInflater.from(XpToast.this.getContext()).inflate(R.layout.layout_xp_toast_text, xpTextSwitcher, false);
            //    }
            //});
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            userLevelProgressBar.setUserLevelProgressBarLevelUpListener(this);
            userLevelProgressBar.setUserLevelProgressBarListener(this);
            mLevelDefListCacheSubscription = levelDefListCache.get(levelDefListId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new XPToastLevelDefCacheObserver());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        mLevelDefListCacheSubscription.unsubscribe();
        mLevelDefListCacheSubscription = null;
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setUserLevelProgressBarLevelUpListener(null);
            userLevelProgressBar.setUserLevelProgressBarListener(null);
        }
        isLevelDefError = false;
        super.onDetachedFromWindow();
    }

    public void showWhenReady(@NonNull UserXPAchievementDTO userXPAchievementDTO)
    {
        cleanUp();
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setUserLevelProgressBarLevelUpListener(this);
            userLevelProgressBar.setUserLevelProgressBarListener(this);
        }
        populateAnimationLists(userXPAchievementDTO);
        if (userLevelProgressBar.getLevelDefDTOList() != null && !userLevelProgressBar.getLevelDefDTOList().isEmpty())
        {
            setAndPlay(userXPAchievementDTO);
        }
        else if (isLevelDefError)
        {
            hideAndReleaseFlag();
        }
        else
        {
            this.xpToBePlayed = userXPAchievementDTO;
        }
    }

    private void setAndPlay(@NonNull UserXPAchievementDTO userXPAchievementDTO)
    {
        userLevelProgressBar.startsWith(userXPAchievementDTO.getBaseXp());
        playAllAnimations();
    }

    private void populateAnimationLists(@NonNull UserXPAchievementDTO userXPAchievementDTO)
    {
        LevelAnimationDefinition l0 = new LevelAnimationDefinition(0, userXPAchievementDTO.xpEarned, userXPAchievementDTO.text);
        levelAnimationDefinitions.add(l0);
        if (userXPAchievementDTO.multiplier != null && !userXPAchievementDTO.multiplier.isEmpty())
        {
            int from = l0.to();
            for (int i = 0; i < userXPAchievementDTO.multiplier.size(); i++)
            {
                UserXPMultiplierDTO userXPMultiplierDTO = userXPAchievementDTO.multiplier.get(i);
                int to = (i == 0 ? userXPMultiplierDTO.xpTotal : from + userXPMultiplierDTO.xpTotal);
                LevelAnimationDefinition ln = new LevelAnimationDefinition(from, to - from,
                        getContext().getString(R.string.user_level_xp_multiplier_format, userXPMultiplierDTO.text, userXPMultiplierDTO.multiplier));
                levelAnimationDefinitions.add(ln);
                from = to;
            }
        }
    }

    private void playAllAnimations()
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
                //xpTextSwitcher.reset();
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
        // TODO https://crashlytics.com/tradehero/android/apps/com.tradehero.th/issues/543e1b85e3de5099ba0dbd14
        try{
            currentLevelAnimationDefinition = levelAnimationDefinitions.pop();

            xpTextSwitcher.setText(currentLevelAnimationDefinition.text);

            if (userLevelProgressBar.getLevelDefDTOList() != null)
            {
                userLevelProgressBar.increment(currentLevelAnimationDefinition.earned);
            }
        }catch(NoSuchElementException e){
            e.printStackTrace();
        }

    }

    private void displayXPEarned(int value)
    {
        if (xpValue != null)
        {
            THSignedNumber.builder(value)
                    .relevantDigitCount(1)
                    .withOutSign()
                    .format(getContext().getString(R.string.achievement_xp_earned_format))
                    .build()
                    .into(xpValue);
        }
    }

    public void hide()
    {
        cleanUp();
        hideAndReleaseFlag();
        /*
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation)
            {
            }

            @Override public void onAnimationEnd(Animation animation)
            {
                hideAndReleaseFlag();
            }

            @Override public void onAnimationRepeat(Animation animation)
            {
            }
        });
        startAnimation(a);*/
    }

    private void hideAndReleaseFlag()
    {
        setVisibility(View.GONE);
        broadcastUtils.nextPlease();
    }

    private void cleanUp()
    {
        currentLevelAnimationDefinition = null;
        xpToBePlayed = null;
        levelAnimationDefinitions.clear();
    }

    private void setLevelDefList(@NonNull LevelDefDTOList levelDefList)
    {
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setLevelDefDTOList(levelDefList);
            if (xpToBePlayed != null)
            {
                showWhenReady(xpToBePlayed);
            }
        }
    }

    @Override public void onLevelUp(@NonNull LevelDefDTO fromLevel, @NonNull LevelDefDTO toLevel)
    {
        Context context = getContext();
        if (context instanceof DashboardActivity)
        {
            LevelUpDialogFragment levelUpDialogFragment = LevelUpDialogFragment.newInstance(fromLevel.getId(), toLevel.getId());
            try
            {
                levelUpDialogFragment.show(((DashboardActivity) context).getSupportFragmentManager(), LevelUpDialogFragment.class.getName());
            }
            catch (java.lang.IllegalStateException e)
            {
                Timber.d(e.toString());
                THToast.show(e.toString());
            }
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
                XpToast.this.displayXPEarned(value);
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
                    XpToast.this.hide();
                }
            }, getResources().getInteger(R.integer.xp_level_toast_dismiss_delay));
        }
        else
        {
            startXPAnimation();
        }
    }

    public void destroy()
    {
        userLevelProgressBar.stopIncrement();
        cleanUp();
    }

    private class XPToastLevelDefCacheObserver implements Observer<Pair<LevelDefListId, LevelDefDTOList>>
    {
        @Override public void onNext(Pair<LevelDefListId, LevelDefDTOList> pair)
        {
            setLevelDefList(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
//            Timber.e("Unable to get xp level definition: %s", e);
            Timber.e("Unable to get xp level definition");
            //Release flag from broadcast utils.
            isLevelDefError = true;
            cleanUp();
            hideAndReleaseFlag();
        }
    }

    private static class LevelAnimationDefinition
    {
        String text;
        int from;
        int earned;

        private LevelAnimationDefinition(int from, int earned, String text)
        {
            this.from = from;
            this.earned = earned;
            this.text = text;
            if (text == null)
            {
                Timber.e(new Exception(), "Null text for XP Level change");
            }
        }

        private int to()
        {
            return from + earned;
        }
    }
}
