package com.tradehero.th.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import butterknife.InjectView;
import butterknife.OnClick;
import com.chrisbanes.colorfinder.ColorScheme;
import com.chrisbanes.colorfinder.DominantColorCalculator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.share.achievement.AchievementShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.fragments.level.LevelUpDialogFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.persistence.level.LevelDefListCache;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.widget.UserLevelProgressBar;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractAchievementDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = AbstractAchievementDialogFragment.class.getName();
    private static final String BUNDLE_KEY_USER_ACHIEVEMENT_ID = AbstractAchievementDialogFragment.class.getName() + ".UserAchievementDTOKey";

    private static final String PROPERTY_XP_EARNED = "xpEarned";
    private static final String PROPERTY_BTN_COLOR = "btnColor";

    private static final int DEFAULT_FILTER_COLOR = Color.GRAY;

    @InjectView(R.id.achievement_content_container) ViewGroup contentContainer;

    @InjectView(R.id.achievement_header) TextView header;

    @InjectView(R.id.achievement_share_flipper) ViewFlipper shareFlipper;

    @InjectView(R.id.achievement_title) TextView title;
    @InjectView(R.id.achievement_description) TextView description;
    @InjectView(R.id.achievement_more_description) TextView moreDescription;
    @InjectView(R.id.achievement_badge) ImageView badge;

    @InjectView(R.id.achievement_pulse) ImageView pulseEffect;
    @InjectView(R.id.achievement_pulse2) ImageView pulseEffect2;
    @InjectView(R.id.achievement_pulse3) ImageView pulseEffect3;
    @InjectView(R.id.achievement_starburst) ImageView starBurst;

    @InjectView(R.id.user_level_progress_xp_earned) TextView xpEarned;

    @InjectView(R.id.btn_achievement_share) Button btnShare;
    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @Inject UserAchievementCache userAchievementCache;
    @Inject Picasso picasso;
    @Inject GraphicUtil graphicUtil;
    @Inject LevelDefListCache levelDefListCache;

    @Inject AchievementServiceWrapper achievementServiceWrapper;
    @Inject AchievementShareFormDTOFactory achievementShareFormDTOFactory;

    @Inject BroadcastUtils broadcastUtils;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Lazy<WeChatDTOFactory> weChatDTOFactoryLazy;

    protected UserAchievementId userAchievementId;
    protected UserAchievementDTO userAchievementDTO;
    protected int mCurrentColor = DEFAULT_FILTER_COLOR;
    private ValueAnimator colorValueAnimator;
    private ObjectAnimator btnColorAnimation;
    private ValueAnimator mAnim;
    private LevelDefListId mLevelDefListId = new LevelDefListId();
    private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> levelDefListCacheListener;
    private Callback mBadgeCallback;

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
        levelDefListCacheListener = createLevelDefCacheListener();
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
        displayXpEarned(0);
        startAnimation();

        userLevelProgressBar.setPauseDurationWhenLevelUp(getResources().getInteger(R.integer.user_level_pause_on_level_up));
        userLevelProgressBar.setUserLevelProgressBarLevelUpListener(new LevelUpListener());
        levelDefListCache.register(mLevelDefListId, levelDefListCacheListener);
        levelDefListCache.getOrFetchAsync(mLevelDefListId);
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
            colorValueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override public void onAnimationEnd(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    setColor(mCurrentColor);
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
        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void displayHeader()
    {
        header.setText(userAchievementDTO.achievementDef.header);
    }

    private void displayBadge()
    {
        if (!StringUtils.isNullOrEmpty(userAchievementDTO.achievementDef.visual))
        {
            if (mBadgeCallback == null)
            {
                mBadgeCallback = new Callback()
                {
                    @Override public void onSuccess()
                    {
                        handleBadgeSuccess();
                    }

                    @Override public void onError()
                    {

                    }
                };
            }

            picasso.load(userAchievementDTO.achievementDef.visual)
                    .placeholder(R.drawable.achievement_unlocked_placeholder)
                    .fit()
                    .centerInside()
                    .into(badge, mBadgeCallback);
        }
        else
        {
            badge.setImageResource(R.drawable.achievement_unlocked_placeholder);
        }
    }

    protected void handleBadgeSuccess()
    {
        DominantColorCalculator dominantColorCalculator =
                new DominantColorCalculator(((BitmapDrawable) badge.getDrawable()).getBitmap());
        ColorScheme colorScheme = dominantColorCalculator.getColorScheme();
        updateColor(colorScheme);
    }

    private void showShareSuccess()
    {
        //No need to hold reference to middle callback since we did not pass a listener
        List<SocialNetworkEnum> shareTos = getEnabledSharePreferences();
        if (shareTos.contains(SocialNetworkEnum.WECHAT))
        {
            WeChatDTO weChatDTO = weChatDTOFactoryLazy.get().createFrom(getActivity(), userAchievementDTO);
            socialSharerLazy.get().share(weChatDTO);
        }
        achievementServiceWrapper.shareAchievement(
                achievementShareFormDTOFactory.createFrom(
                        getEnabledSharePreferences(),
                        userAchievementDTO),
                null);
        shareFlipper.setDisplayedChild(1);
    }

    private void playRotatingAnimation()
    {
        displayPulse();
        displayStarburst();
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

    protected void onCreatePropertyValuesHolder(List<PropertyValuesHolder> propertyValuesHolders)
    {
        PropertyValuesHolder xp = PropertyValuesHolder.ofInt(PROPERTY_XP_EARNED, 0, userAchievementDTO.xpEarned);
        propertyValuesHolders.add(xp);
    }

    private void displayPulse()
    {
        setDrawingCacheEnabled(pulseEffect, pulseEffect2, pulseEffect3);

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

    private void setDrawingCacheEnabled(View... views)
    {
        for (int i = 0; i < views.length; i++)
        {
            View v = views[i];
            v.setDrawingCacheEnabled(true);
        }
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

    private void displayXpEarned(int xp)
    {
        xpEarned.setText(getString(R.string.achievement_xp_earned_format, THSignedNumber.builder(xp).relevantDigitCount(1).withOutSign().build().toString()));
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

    @OnClick(R.id.btn_achievement_share)
    public void shareButtonClicked()
    {
        if (!getEnabledSharePreferences().isEmpty())
        {
            showShareSuccess();
        }
        else
        {
            alertDialogUtil.popWithNegativeButton(getActivity(), R.string.link_select_one_social, R.string.link_select_one_social_description,
                    R.string.ok);
        }
    }

    @Override public void onPause()
    {
        contentContainer.setOnTouchListener(null);
        super.onPause();
    }

    @Override public void onDestroy()
    {
        levelDefListCacheListener = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        if (colorValueAnimator != null)
        {
            cleanupAnimation(colorValueAnimator);
            colorValueAnimator = null;
        }
        if (mAnim != null)
        {
            cleanupAnimation(mAnim);
            mAnim = null;
        }
        if (btnColorAnimation != null)
        {
            cleanupAnimation(btnColorAnimation);
            btnColorAnimation = null;
        }
        picasso.cancelRequest(badge);
        mBadgeCallback = null;
        levelDefListCache.unregister(levelDefListCacheListener);
        userLevelProgressBar.setUserLevelProgressBarLevelUpListener(null);
        super.onDestroyView();
    }

    private void cleanupAnimation(ValueAnimator animator)
    {
        if (animator.isRunning())
        {
            animator.end();
        }
        animator.cancel();
        animator.removeAllUpdateListeners();
        animator.removeAllListeners();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        broadcastUtils.nextPlease();
    }

    @OnClick(R.id.achievement_dummy_container)
    public void onOutsideContentClicked()
    {
        getDialog().dismiss();
    }

    protected ValueAnimator.AnimatorUpdateListener createEarnedAnimatorUpdateListener()
    {
        return new AbstractAchievementValueAnimatorUpdateListener();
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

    protected class AbstractAchievementValueAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
    {
        @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
        {
            int xp = (Integer) valueAnimator.getAnimatedValue(PROPERTY_XP_EARNED);
            displayXpEarned(xp);
        }
    }

    private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> createLevelDefCacheListener()
    {
        return new LevelDefListListener();
    }

    protected class LevelDefListListener implements DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList>
    {

        @Override public void onDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            userLevelProgressBar.setLevelDefDTOList(value);
            userLevelProgressBar.startsWith(userAchievementDTO.getBaseExp());
            userLevelProgressBar.increment(userAchievementDTO.xpEarned);
        }

        @Override public void onErrorThrown(@NotNull LevelDefListId key, @NotNull Throwable error)
        {

        }
    }

    protected class LevelUpListener implements UserLevelProgressBar.UserLevelProgressBarLevelUpListener
    {

        @Override public void onLevelUp(LevelDefDTO fromLevel, LevelDefDTO toLevel)
        {
            LevelUpDialogFragment levelUpDialogFragment = LevelUpDialogFragment.newInstance(fromLevel.getId(), toLevel.getId());
            levelUpDialogFragment.show(getFragmentManager(), LevelUpDialogFragment.class.getName());
        }
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
            if (userAchievementDTO == null)
            {
                return null;
            }
            else if (userAchievementDTO.achievementDef.isQuest)
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
