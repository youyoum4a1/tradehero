package com.tradehero.th.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.chrisbanes.colorfinder.ColorScheme;
import com.chrisbanes.colorfinder.DominantColorCalculator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.StringUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAchievementDialogFragment extends BaseDialogFragment
{
    public static final String TAG = AbstractAchievementDialogFragment.class.getName();
    private static final String BUNDLE_KEY_USER_ACHIEVEMENT_ID = AbstractAchievementDialogFragment.class.getName() + ".UserAchievementDTOKey";

    private static final int DEFAULT_FILTER_COLOR = Color.BLACK;

    @InjectView(R.id.achievement_content_container) ViewGroup contentContainer;

    @InjectView(R.id.achievement_header) TextView header;
    @InjectView(R.id.achievement_title) TextView title;
    @InjectView(R.id.achievement_description) TextView description;
    @InjectView(R.id.achievement_more_description) TextView moreDescription;

    @InjectView(R.id.achievement_badge) ImageView badge;
    @InjectView(R.id.achievement_pulse) ImageView pulseEffect;
    @InjectView(R.id.achievement_pulse2) ImageView pulseEffect2;
    @InjectView(R.id.achievement_pulse3) ImageView pulseEffect3;

    @InjectView(R.id.achievement_starburst) ImageView starBurst;

    @InjectView(R.id.btn_achievement_dismiss) Button btnDismiss;

    @Inject UserAchievementCache userAchievementCache;
    @Inject Picasso picasso;
    @Inject GraphicUtil graphicUtil;

    protected UserAchievementId userAchievementId;
    protected UserAchievementDTO userAchievementDTO;
    protected int mCurrentColor = DEFAULT_FILTER_COLOR;
    private ValueAnimator colorValueAnimator;

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Achievement_Dialog);
        d.getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_Animation);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return d;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void init()
    {
        userAchievementId = new UserAchievementId(getArguments().getBundle(BUNDLE_KEY_USER_ACHIEVEMENT_ID));
        userAchievementDTO = userAchievementCache.pop(userAchievementId);
        initView();
    }

    protected void initView()
    {
        displayHeader();
        displayBadge();
        displayTitle();
        displayText();
        displaySubText();
        setColor(DEFAULT_FILTER_COLOR);
        playRotatingAnimation();
    }

    private void displayStarburst()
    {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.achievement_starburst);
        starBurst.startAnimation(a);
    }

    private void updateColor(@Nullable ColorScheme colorScheme)
    {
        if (colorScheme == null)
        {
            int color = graphicUtil.parseColor(userAchievementDTO.achievementDef.hexColor, Color.BLACK);
            updateColor(color);
        }
        else
        {
            updateColor(colorScheme.primaryAccent);
        }
    }

    private void updateColor(int color)
    {
        if (color != mCurrentColor)
        {
            colorValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mCurrentColor, color);
            colorValueAnimator.setDuration(500l);
            colorValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    int color = (Integer) valueAnimator.getAnimatedValue();
                    setColor(color);
                }
            });
            colorValueAnimator.start();
            mCurrentColor = color;
        }
    }

    private void setColor(int color)
    {
        applyColorFilter(pulseEffect, color);
        applyColorFilter(pulseEffect2, color);
        applyColorFilter(pulseEffect3, color);
        applyColorFilter(starBurst, color);
        title.setTextColor(color);
    }

    private void applyColorFilter(ImageView imageView, int color)
    {
        Drawable d = imageView.getDrawable();
        d.clearColorFilter();
        d.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    private void displayHeader()
    {
        header.setText(userAchievementDTO.achievementDef.header);
    }

    private void displayBadge()
    {
        if (!StringUtils.isNullOrEmpty(userAchievementDTO.achievementDef.visual))
        {
            picasso.load(userAchievementDTO.achievementDef.visual)
                    .placeholder(R.drawable.achievement_unlocked_placeholder)
                    .fit()
                    .centerInside()
                    .into(badge, new Callback()
                    {
                        @Override public void onSuccess()
                        {
                            DominantColorCalculator dominantColorCalculator =
                                    new DominantColorCalculator(((BitmapDrawable) badge.getDrawable()).getBitmap());
                            ColorScheme colorScheme = dominantColorCalculator.getColorScheme();
                            updateColor(colorScheme);
                        }

                        @Override public void onError()
                        {

                        }
                    });
        }
        else
        {
            badge.setImageResource(R.drawable.achievement_unlocked_placeholder);
        }
    }

    private void playRotatingAnimation()
    {
        displayPulse();
        displayStarburst();
    }

    private void displayPulse()
    {
        AnimatorSet animatorSet = new AnimatorSet();

        Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.achievement_pulse);
        Animator animator1 = animator.clone();
        Animator animator2 = animator.clone();

        animator.setTarget(pulseEffect);
        animator1.setTarget(pulseEffect2);
        animator2.setTarget(pulseEffect3);

        animator1.setStartDelay(getResources().getInteger(R.integer.achievement_pulse_delay));
        animator2.setStartDelay(getResources().getInteger(R.integer.achievement_pulse_delay2));

        animatorSet.playTogether(animator, animator1, animator2);
        animatorSet.start();
    }

    private void displayTitle()
    {
        title.setText(userAchievementDTO.achievementDef.thName);
    }

    private void displayText()
    {
        description.setText(Html.fromHtml(userAchievementDTO.achievementDef.text));
    }

    private void displaySubText()
    {
        if (userAchievementDTO.achievementDef.subText != null)
        {
            moreDescription.setText(Html.fromHtml(userAchievementDTO.achievementDef.subText));
        }
        else
        {
            moreDescription.setVisibility(View.GONE);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        contentContainer.setOnTouchListener(
                new AchievementDialogSwipeDismissTouchListener(contentContainer, null,
                        new AchievementDialogSwipeDismissTouchListener.DismissCallbacks()
                        {
                            @Override public boolean canDismiss(Object token)
                            {
                                return true;
                            }

                            @Override public void onDismiss(View view, Object token)
                            {
                                removeDialogAnimation();
                                getDialog().dismiss();
                            }
                        }));
    }

    private void removeDialogAnimation()
    {
        getDialog().getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_NoAnimation);
    }

    @Override public void onPause()
    {
        contentContainer.setOnTouchListener(null);
        super.onPause();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        if (colorValueAnimator != null)
        {
            colorValueAnimator.cancel();
            colorValueAnimator.removeAllUpdateListeners();
            colorValueAnimator.removeAllListeners();
            colorValueAnimator = null;
        }
        picasso.cancelRequest(badge);
        super.onDestroyView();
    }

    @OnClick(R.id.btn_achievement_dismiss)
    public void onDismissBtnClicked()
    {
        getDialog().dismiss();
    }

    @OnClick(R.id.achievement_dummy_container)
    public void onOutsideContentClicked()
    {
        getDialog().dismiss();
    }

    public static class Creator
    {
        @NotNull UserAchievementCache userAchievementCacheInner;

        @Inject public Creator(@NotNull UserAchievementCache userAchievementCacheInner)
        {
            super();
            this.userAchievementCacheInner = userAchievementCacheInner;
        }

        @Nullable public AbstractAchievementDialogFragment newInstance(@NotNull UserAchievementId userAchievementId)
        {
            if (!userAchievementCacheInner.shouldShow(userAchievementId))
            {
                return null;
            }

            Bundle args = new Bundle();
            args.putBundle(BUNDLE_KEY_USER_ACHIEVEMENT_ID, userAchievementId.getArgs());
            @Nullable UserAchievementDTO userAchievementDTO = userAchievementCacheInner.get(userAchievementId);
            AbstractAchievementDialogFragment dialogFragment;
            if(userAchievementDTO == null)
            {
                return null;
            }
            else if (userAchievementDTO.achievementDef.isQuest) // TODO handle case where userAchievementDTO is null
            {
                dialogFragment = new QuestDialogFragment();
            }
            else
            {
                dialogFragment = new AchievementDialogFragment();
            }
            dialogFragment.setArguments(args);
            return dialogFragment;
        }
    }
}
