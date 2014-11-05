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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Pair;
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
import butterknife.InjectViews;
import butterknife.OnClick;
import com.chrisbanes.colorfinder.ColorScheme;
import com.chrisbanes.colorfinder.ColorUtils;
import com.chrisbanes.colorfinder.DominantColorCalculator;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.achievement.AchievementShareFormDTOFactory;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.fragments.level.LevelUpDialogFragment;
import com.tradehero.th.fragments.settings.SendLoveBroadcastSignal;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.achievement.UserAchievementCacheRx;
import com.tradehero.th.persistence.level.LevelDefListCacheRx;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.widget.UserLevelProgressBar;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public abstract class AbstractAchievementDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = AbstractAchievementDialogFragment.class.getName();
    private static final String BUNDLE_KEY_USER_ACHIEVEMENT_ID = AbstractAchievementDialogFragment.class.getName() + ".UserAchievementDTOKey";

    private static final String PROPERTY_XP_EARNED = "xpEarned";
    private static final String PROPERTY_BTN_COLOR = "btnColor";

    private static final int DEFAULT_FILTER_COLOR = Color.GRAY;
    private static final float TOO_BRIGHT_CUT_OFF = 0.2f;

    public static final int SHARE_PANEL_DEFAULT_INDEX = 0;
    public static final int SHARE_PANEL_SHARED_INDEX = 1;
    public static final int SHARE_PANEL_FAILED_INDEX = 2;
    public static final int SHARE_PANEL_SHARING_INDEX = 3;

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

    @InjectViews({
            R.id.btn_achievement_dismiss,
            R.id.achievement_pulse,
            R.id.achievement_pulse2,
            R.id.achievement_pulse3,
            R.id.achievement_starburst})
    ImageView[] imagesToColorFilter;

    @Inject UserAchievementCacheRx userAchievementCache;
    @Inject Picasso picasso;
    @Inject GraphicUtil graphicUtil;
    @Inject LevelDefListCacheRx levelDefListCache;

    @Inject AchievementServiceWrapper achievementServiceWrapper;
    @Inject AchievementShareFormDTOFactory achievementShareFormDTOFactory;

    @Inject BroadcastUtils broadcastUtils;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject Lazy<WeChatDTOFactory> weChatDTOFactoryLazy;

    @NonNull protected UserAchievementId userAchievementId;
    @Nullable protected UserAchievementDTO userAchievementDTO;
    protected int mCurrentColor = DEFAULT_FILTER_COLOR;
    private ValueAnimator colorValueAnimator;
    private ObjectAnimator btnColorAnimation;
    private ValueAnimator mAnim;
    @NonNull private LevelDefListId mLevelDefListId = new LevelDefListId();
    private Callback mBadgeCallback;

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Achievement_Dialog);
        d.getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_Animation);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        return d;
    }

    protected void init()
    {
        userAchievementId = new UserAchievementId(getArguments().getBundle(BUNDLE_KEY_USER_ACHIEVEMENT_ID));
        userAchievementDTO = userAchievementCache.pop(userAchievementId);
        // TODO destroy if null?
        if (userAchievementDTO == null)
        {
            Timber.e(new Exception(), "Popped UserAchievementDTO is null for %s", userAchievementId);
        }
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
        AndroidObservable.bindFragment(
                this,
                levelDefListCache.get(mLevelDefListId))
                .subscribe(createLevelDefCacheObserver());
    }

    private void displayStarburst()
    {
        Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.achievement_starburst);
        starBurst.startAnimation(a);
    }

    private void updateColor(@Nullable ColorScheme colorScheme)
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null && colorScheme == null)
        {
            int color = graphicUtil.parseColor(userAchievementDTO.achievementDef.hexColor, Color.BLACK);
            updateColor(color);
        }
        else if (colorScheme != null)
        {
            updateColor(colorScheme.primaryAccent);
        }
    }

    private void updateColor(int color)
    {
        // Darken if too bright
        if (ColorUtils.calculateYiqLuma(color) > (256 * (1 - TOO_BRIGHT_CUT_OFF))) // 80% or brighter
        {
            Timber.d("Darkening from %d for %s", color, userAchievementDTO);
            color = ColorUtils.darken(color, TOO_BRIGHT_CUT_OFF); // Add 20% black
        }

        if (color != mCurrentColor)
        {
            colorValueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mCurrentColor, color);
            colorValueAnimator.setDuration(500l);
            colorValueAnimator.addUpdateListener(valueAnimator -> {
                int color1 = (Integer) valueAnimator.getAnimatedValue();
                setColor(color1);
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
        graphicUtil.applyColorFilter(imagesToColorFilter, color);
        title.setTextColor(color);
    }

    private void displayHeader()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            header.setText(userAchievementDTOCopy.achievementDef.header);
        }
    }

    private void displayBadge()
    {
        final UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null
                && !StringUtils.isNullOrEmpty(userAchievementDTOCopy.achievementDef.visual))
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
                        Timber.e("Unable to load image for %s : %s", userAchievementDTOCopy.achievementDef.thName,
                                userAchievementDTOCopy.achievementDef.visual);
                    }
                };
            }

            picasso.load(userAchievementDTOCopy.achievementDef.visual)
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
        setShareButtonColor();
    }

    private void showShareSuccess()
    {
        shareFlipper.setDisplayedChild(SHARE_PANEL_SHARED_INDEX);
        shareFlipper.setEnabled(false);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.achievement_share_failed, R.id.achievement_shared})
    protected void showSharePanel()
    {
        shareFlipper.setDisplayedChild(SHARE_PANEL_DEFAULT_INDEX);
    }

    private void share()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            List<SocialNetworkEnum> shareTos = getEnabledSharePreferences();
            if (shareTos.contains(SocialNetworkEnum.WECHAT))
            {
                shareTos.remove(SocialNetworkEnum.WECHAT);
                WeChatDTO weChatDTO = weChatDTOFactoryLazy.get().createFrom(getActivity(), userAchievementDTOCopy);
                socialSharerLazy.get().setSharedListener(createSocialSharedListener());
                socialSharerLazy.get().share(weChatDTO);
            }
            if (!shareTos.isEmpty())
            {
                //If only there are other social network to share to other than WeChat.
                AndroidObservable.bindFragment(
                        this,
                        achievementServiceWrapper.shareAchievementRx(
                                achievementShareFormDTOFactory.createFrom(
                                        shareTos,
                                        userAchievementDTO)))
                        .subscribe(createShareAchievementObserver());
                showSharing();
            }
            else
            {
                showShareSuccess();
            }
        }
    }

    private void showSharing()
    {
        shareFlipper.setDisplayedChild(SHARE_PANEL_SHARING_INDEX);
        shareFlipper.setEnabled(false);
    }

    private void showShareFailed()
    {
        shareFlipper.setDisplayedChild(SHARE_PANEL_FAILED_INDEX);
        shareFlipper.setEnabled(true);
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

        mAnim.addUpdateListener(createEarnedAnimatorUpdateListener());

        mAnim.start();
    }

    protected void onCreatePropertyValuesHolder(List<PropertyValuesHolder> propertyValuesHolders)
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            PropertyValuesHolder xp = PropertyValuesHolder.ofInt(PROPERTY_XP_EARNED, 0, userAchievementDTOCopy.xpEarned);
            propertyValuesHolders.add(xp);
        }
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
        for (View v : views)
        {
            v.setDrawingCacheEnabled(true);
        }
    }

    private void displayTitle()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            title.setText(userAchievementDTOCopy.achievementDef.thName);
        }
    }

    private void displayText()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null)
        {
            description.setText(Html.fromHtml(userAchievementDTOCopy.achievementDef.text));
        }
    }

    private void displaySubText()
    {
        UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
        if (userAchievementDTOCopy != null
                && userAchievementDTOCopy.achievementDef.subText != null)
        {
            moreDescription.setText(Html.fromHtml(userAchievementDTOCopy.achievementDef.subText));
        }
        else
        {
            moreDescription.setVisibility(View.GONE);
        }
    }

    private void displayXpEarned(int xp)
    {
        xpEarned.setText(
                getString(R.string.achievement_xp_earned_format, THSignedNumber.builder(xp).relevantDigitCount(1).withOutSign().build().toString()));
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
        btnColorAnimation.addUpdateListener(valueAnimator -> {
            int color = (Integer) valueAnimator.getAnimatedValue(PROPERTY_BTN_COLOR);
            StateListDrawable drawable = graphicUtil.createStateListDrawable(getActivity(), color);
            int textColor = graphicUtil.getContrastingColor(color);
            graphicUtil.setBackground(btnShare, drawable);
            btnShare.setTextColor(textColor);
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
                                if (getDialog() != null)
                                {
                                    removeDialogAnimation();
                                    getDialog().dismiss();
                                }
                            }
                        }));
    }

    private void removeDialogAnimation()
    {
        getDialog().getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_NoAnimation);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_achievement_share)
    public void shareButtonClicked()
    {
        if (!getEnabledSharePreferences().isEmpty())
        {
            share();
        }
        else
        {
            alertDialogUtil.popWithNegativeButton(
                    getActivity(),
                    R.string.link_select_one_social,
                    R.string.link_select_one_social_description,
                    R.string.ok);
        }
    }

    @Override public void onPause()
    {
        contentContainer.setOnTouchListener(null);
        super.onPause();
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
        userLevelProgressBar.setUserLevelProgressBarLevelUpListener(null);

        socialSharerLazy.get().setSharedListener(null);
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (badge != null)
        {
            picasso.cancelRequest(badge);
        }
        if (userLevelProgressBar != null)
        {
            userLevelProgressBar.setUserLevelProgressBarLevelUpListener(null);
        }
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
        broadcastUtils.enqueue(new SendLoveBroadcastSignal());
        broadcastUtils.nextPlease();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.achievement_dummy_container, R.id.btn_achievement_dismiss})
    public void onOutsideContentClicked()
    {
        getDialog().dismiss();
    }

    @NonNull protected ValueAnimator.AnimatorUpdateListener createEarnedAnimatorUpdateListener()
    {
        return new AbstractAchievementValueAnimatorUpdateListener();
    }

    protected class AbstractAchievementValueAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener
    {
        @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
        {
            int xp = (Integer) valueAnimator.getAnimatedValue(PROPERTY_XP_EARNED);
            displayXpEarned(xp);
        }
    }

    @NonNull private Observer<Pair<LevelDefListId, LevelDefDTOList>> createLevelDefCacheObserver()
    {
        return new LevelDefListObserver();
    }

    protected class LevelDefListObserver implements Observer<Pair<LevelDefListId, LevelDefDTOList>>
    {
        @Override public void onNext(Pair<LevelDefListId, LevelDefDTOList> pair)
        {
            userLevelProgressBar.setLevelDefDTOList(pair.second);
            UserAchievementDTO userAchievementDTOCopy = userAchievementDTO;
            if (userAchievementDTOCopy != null)
            {
                userLevelProgressBar.startsWith(userAchievementDTOCopy.getBaseExp());
                userLevelProgressBar.increment(userAchievementDTOCopy.xpEarned);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
        @NonNull UserAchievementCacheRx userAchievementCacheInner;

        @Inject public Creator(@NonNull UserAchievementCacheRx userAchievementCacheInner)
        {
            super();
            this.userAchievementCacheInner = userAchievementCacheInner;
        }

        @NonNull public Observable<AbstractAchievementDialogFragment> newInstance(@NonNull UserAchievementId userAchievementId)
        {
            if (!userAchievementCacheInner.shouldShow(userAchievementId))
            {
                return Observable.empty();
            }

            final Bundle args = new Bundle();
            args.putBundle(BUNDLE_KEY_USER_ACHIEVEMENT_ID, userAchievementId.getArgs());
            return userAchievementCacheInner.get(userAchievementId)
                    .map(pair -> {
                        AbstractAchievementDialogFragment dialogFragment;
                        if (pair.second.achievementDef.isQuest)
                        {
                            dialogFragment = new QuestDialogFragment();
                        }
                        else
                        {
                            dialogFragment = new AchievementDialogFragment();
                        }
                        dialogFragment.setArguments(args);
                        return dialogFragment;
                    });
        }
    }

    protected SocialSharer.OnSharedListener createSocialSharedListener()
    {
        return new ShareAchievementListener();
    }

    protected class ShareAchievementListener implements SocialSharer.OnSharedListener
    {
        @Override public void onConnectRequired(SocialShareFormDTO shareFormDTO)
        {
            throw new IllegalStateException("It should have been taken care of at the network button press");
        }

        @Override public void onShared(SocialShareFormDTO shareFormDTO, SocialShareResultDTO socialShareResultDTO)
        {
            showShareSuccess();
        }

        @Override public void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable)
        {
            showShareFailed();
        }
    }

    protected Observer<BaseResponseDTO> createShareAchievementObserver()
    {
        return new ShareAchievementObserver();
    }

    protected class ShareAchievementObserver implements Observer<BaseResponseDTO>
    {
        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            showShareSuccess();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            showShareFailed();
        }
    }
}
