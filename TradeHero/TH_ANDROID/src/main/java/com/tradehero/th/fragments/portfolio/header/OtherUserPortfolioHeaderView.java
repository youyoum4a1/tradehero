package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

public class OtherUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    @InjectView(R.id.header_portfolio_following_container) RelativeLayout followContainer;
    @InjectView(R.id.portfolio_person_container) View userViewContainer;
    @InjectView(R.id.portfolio_header_avatar) ImageView userImageView;
    @InjectView(R.id.header_portfolio_username) TextView usernameTextView;
    @InjectView(R.id.header_portfolio_following_image) ImageView followingImageView;
    @InjectView(R.id.follow_button) TextView followButton;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userCache;
    @Inject Picasso picasso;
    @Inject Analytics analytics;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<HeroAlertDialogUtil> heroAlertDialogUtilLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;

    @Nullable private Subscription freeFollowMiddleSubscription;
    private UserProfileDTO userProfileDTO;
    private WeakReference<OnFollowRequestedListener> followRequestedListenerWeak = new WeakReference<>(null);
    private WeakReference<OnTimelineRequestedListener> timelineRequestedListenerWeak = new WeakReference<>(null);
    protected FollowDialogCombo followDialogCombo;

    //<editor-fold desc="Constructors">
    public OtherUserPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (followButton != null)
        {
            followButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    showFollowDialog();
                }
            });
        }

        if (userViewContainer != null)
        {
            userViewContainer.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    if (userProfileDTO != null)
                    {
                        notifyTimelineRequested(userProfileDTO.getBaseKey());
                    }
                }
            });
        }
    }

    /**
     * show the dialog to let the user follow the hero the user's browsing
     */
    public void showFollowDialog()
    {
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Positions_Follow));
        detachFollowDialogCombo();
        followDialogCombo = heroAlertDialogUtilLazy.get().showFollowDialog(getContext(), userProfileDTO,
                UserProfileDTOUtil.IS_NOT_FOLLOWER,
                new OtherUserPortfolioFollowRequestedListener());
    }

    public class OtherUserPortfolioFollowRequestedListener implements com.tradehero.th.models.social.OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(@NonNull UserBaseKey heroId)
        {
            freeFollow(heroId);
        }

        @Override public void premiumFollowRequested(@NonNull UserBaseKey heroId)
        {
            follow(heroId);
        }
    }

    protected void freeFollow(@NonNull UserBaseKey heroId)
    {
        heroAlertDialogUtilLazy.get().showProgressDialog(getContext(), getContext().getString(R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleSubscription =
                userServiceWrapperLazy.get().freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new FreeFollowObserver());
    }

    protected void follow(@NonNull UserBaseKey heroId)
    {
        if (userProfileDTO != null)
        {
            notifyFollowRequested(heroId);
        }
    }

    private void detachFreeFollowMiddleCallback()
    {
        Subscription copy = freeFollowMiddleSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        freeFollowMiddleSubscription = null;
    }

    protected void detachFollowDialogCombo()
    {
        FollowDialogCombo followDialogComboCopy = followDialogCombo;
        if (followDialogComboCopy != null)
        {
            followDialogComboCopy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    public class FreeFollowObserver extends EmptyObserver<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
            configureFollowItemsVisibility();
            notifyUserFollowed(userProfileDTO.getBaseKey());
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.PositionList));
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
            heroAlertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (followButton != null)
        {
            followButton.setOnClickListener(null);
        }
        if (userViewContainer != null)
        {
            userViewContainer.setOnClickListener(null);
        }

        detachFreeFollowMiddleCallback();
        detachFollowDialogCombo();

        super.onDetachedFromWindow();
    }

    @Override public void linkWith(UserProfileDTO userProfileDTO)
    {
        display(userProfileDTO);
    }

    private void display(UserProfileDTO user)
    {
        this.userProfileDTO = user;
        configureUserViews();
        configureFollowItemsVisibility();
    }

    @Override public void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        // Nothing to do
    }

    private void configureUserViews()
    {
        if (userProfileDTO != null)
        {
            if (usernameTextView != null)
            {
                usernameTextView.setText(userProfileDTO.displayName);
            }

            if (this.userImageView != null)
            {
                displayDefaultUserImage();
                picasso.load(this.userProfileDTO.picture)
                        .transform(peopleIconTransformation)
                        .placeholder(this.userImageView.getDrawable())
                        .into(this.userImageView);
            }
        }
    }

    private void displayDefaultUserImage()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(this.userImageView);
    }

    public void configureFollowItemsVisibility()
    {
        UserProfileDTO currentUser = this.userCache.getValue(currentUserId.toUserBaseKey());
        if (this.userProfileDTO == null || isCurrentUserID(this.userProfileDTO.id))
        {
            this.followingImageView.setVisibility(GONE);
            this.followButton.setVisibility(GONE);
        }
        // TODO rework so we handle better the case where currentUser is null
        else if (currentUser != null && currentUser.isFollowingUser(this.userProfileDTO.id))
        {
            this.followingImageView.setVisibility(VISIBLE);
            this.followButton.setVisibility(GONE);
        }
        else
        {
            this.followingImageView.setVisibility(GONE);
            this.followButton.setVisibility(VISIBLE);
        }
    }

    public boolean isCurrentUserID(int userId)
    {
        UserProfileDTO currentUser = this.userCache.getValue(currentUserId.toUserBaseKey());
        if(currentUser!=null)
        {
            return currentUser.id == userId;
        }
        return false;
    }

    /**
     * The listener should be strongly referenced elsewhere.
     */
    @Override public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListenerWeak = new WeakReference<>(followRequestedListener);
    }

    protected void notifyFollowRequested(@NonNull UserBaseKey userBaseKey)
    {
        OnFollowRequestedListener followRequestedListener = followRequestedListenerWeak.get();
        if (followRequestedListener != null)
        {
            followRequestedListener.onFollowRequested(userBaseKey);
        }
    }

    protected void notifyUserFollowed(@NonNull UserBaseKey userBaseKey)
    {
        OnFollowRequestedListener followRequestedListener = followRequestedListenerWeak.get();
        if (followRequestedListener != null)
        {
            followRequestedListener.onUserFollowed(userBaseKey);
        }
    }

    /**
     * The listener should be strongly referenced elsewhere
     */
    @Override public void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener)
    {
        this.timelineRequestedListenerWeak = new WeakReference<>(timelineRequestedListener);
    }

    protected void notifyTimelineRequested(@NonNull UserBaseKey userBaseKey)
    {
        OnTimelineRequestedListener timelineRequestedListener = timelineRequestedListenerWeak.get();
        if (timelineRequestedListener != null)
        {
            timelineRequestedListener.onTimelineRequested(userBaseKey);
        }
    }
}
