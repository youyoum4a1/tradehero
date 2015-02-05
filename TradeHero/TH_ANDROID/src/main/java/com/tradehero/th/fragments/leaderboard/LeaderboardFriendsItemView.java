package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.facebook.FacebookOperationCanceledException;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.social.friend.SocialFriendHandlerFacebook;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<UserFriendsDTO>, View.OnClickListener
{
    @InjectView(R.id.leaderboard_user_item_network_label) ImageView networkLabel;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_social_name) TextView socialName;
    @InjectView(R.id.leaderboard_user_item_invite_btn) TextView inviteBtn;

    @InjectView(R.id.leaderboard_user_item_follow) @Optional View lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) @Optional View lbmuFollowingUser;

    @Nullable private UserFriendsDTO userFriendsDTO;
    @Nullable private Subscription inviteSubscription;
    private ProgressDialog progressDialog;
    protected UserProfileDTO currentUserProfileDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject Provider<Activity> activityProvider;
    @Inject Lazy<SocialFriendHandlerFacebook> socialFriendHandlerFacebookLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject THRouter thRouter;
    @Inject Analytics analytics;
    @Inject DashboardNavigator dashboardNavigator;
    private Subscription facebookInvitationSubscription;

    public LeaderboardFriendsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        loadDefaultPicture();
        if (lbmuFollowUser != null)
        {
            lbmuFollowUser.setOnClickListener(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        loadDefaultPicture();
    }

    @Override protected void onDetachedFromWindow()
    {
        avatar.setOnClickListener(null);
        inviteBtn.setOnClickListener(null);
        detachInviteSubscription();
        detachFacebookSubscription();
        super.onDetachedFromWindow();
    }

    protected void loadDefaultPicture()
    {
        if (avatar != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(avatar);
        }
    }

    @Override public void display(UserFriendsDTO dto)
    {
        userFriendsDTO = dto;
        if (userFriendsDTO != null)
        {
            updatePosition();
            displayPicture();
            updateName();
            updateInviteButton();
        }
    }

    private void updatePosition()
    {
        if (networkLabel != null)
        {
            boolean isSocial = true;
            networkLabel.setBackgroundResource(userFriendsDTO.getNetworkLabelImage());
            networkLabel.setVisibility(VISIBLE);
        }
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (userFriendsDTO != null && userFriendsDTO.getProfilePictureURL() != null)
            {
                picasso.load(userFriendsDTO.getProfilePictureURL())
                        .transform(peopleIconTransformation)
                        .placeholder(avatar.getDrawable())
                        .into(avatar);
            }
        }
    }

    public void updateName()
    {
        socialName.setVisibility(VISIBLE);
        socialName.setText(userFriendsDTO.name);
    }

    private void updateInviteButton()
    {
        if (inviteBtn != null)
        {
            inviteBtn.setOnClickListener(this);
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.leaderboard_user_item_profile_picture:
                if (userFriendsDTO.isTradeHeroUser())
                {
                    handleOpenProfileButtonClicked();
                }
                break;
            case R.id.leaderboard_user_item_invite_btn:
                invite();
                break;
            case R.id.leaderboard_user_item_follow:
                THToast.show("TODO");
                break;
        }
    }

    public void linkWith(UserProfileDTO currentUserProfileDTO, boolean andDisplay)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        if (andDisplay)
        {
            displayFollow();
        }
    }

    private void displayFollow()
    {
        Boolean isFollowing = isCurrentUserFollowing();
        boolean showFollow = isFollowing == null || !isFollowing;
        if (lbmuFollowUser != null)
        {
            lbmuFollowUser.setVisibility(showFollow ? VISIBLE : GONE);
        }
        if (lbmuFollowingUser != null)
        {
            lbmuFollowingUser.setVisibility(!showFollow ? VISIBLE : GONE);
        }
    }

    @Nullable public Boolean isCurrentUserFollowing()
    {
        if (currentUserProfileDTO == null || userFriendsDTO == null || !userFriendsDTO.isTradeHeroUser())
        {
            return null;
        }
        return currentUserProfileDTO.isFollowingUser(userFriendsDTO.thUserId);
    }

    private void handleOpenProfileButtonClicked()
    {
        if (userFriendsDTO != null && currentUserId != null)
        {
            Bundle bundle = new Bundle();
            thRouter.save(bundle, new UserBaseKey(userFriendsDTO.thUserId));
            if (dashboardNavigator != null)
            {
                if (currentUserId.get() == userFriendsDTO.thUserId)
                {
                    dashboardNavigator.pushFragment(MeTimelineFragment.class, bundle);
                }
                else
                {
                    dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
                }
            }
        }
    }

    private void invite()
    {
        if (userFriendsDTO instanceof UserFriendsLinkedinDTO || userFriendsDTO instanceof UserFriendsTwitterDTO)
        {
            if (userFriendsDTO instanceof UserFriendsLinkedinDTO)
            {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, AnalyticsConstants.Linkedin));
            }
            else
            {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, AnalyticsConstants.Twitter));
            }
            InviteFormUserDTO inviteFriendForm = new InviteFormUserDTO();
            inviteFriendForm.add(userFriendsDTO);
            getProgressDialog().show();
            detachInviteSubscription();
            inviteSubscription = userServiceWrapperLazy.get()
                    .inviteFriendsRx(currentUserId.toUserBaseKey(), inviteFriendForm)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::onInvitationDone,
                            this::onInvitationError);
        }
        else if (userFriendsDTO instanceof UserFriendsFacebookDTO)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, AnalyticsConstants.Facebook));
            detachFacebookSubscription();
            facebookInvitationSubscription = socialFriendHandlerFacebookLazy.get()
                    .createShareRequestObservable(Arrays.asList((UserFriendsFacebookDTO) userFriendsDTO), null)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Bundle>()
                    {
                        @Override public void onCompleted()
                        {
                            Timber.d("completed");
                        }

                        @Override public void onError(Throwable e)
                        {
                            if (e instanceof FacebookOperationCanceledException)
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                            }
                            Timber.e(e, "Error on subscribing to Facebook");
                        }

                        @Override public void onNext(Bundle bundle)
                        {
                            final String requestId = bundle.getString("request");
                            if (requestId != null)
                            {
                                THToast.show(R.string.invite_friend_request_sent);
                            }
                            else
                            {
                                THToast.show(R.string.invite_friend_request_cancelled);
                            }

                            Timber.d("next %s", bundle);
                        }
                    });
        }
    }

    private void detachInviteSubscription()
    {
        Subscription copy = inviteSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        inviteSubscription = null;
    }

    private void detachFacebookSubscription()
    {
        Subscription copy = facebookInvitationSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        facebookInvitationSubscription = null;
    }

    @SuppressWarnings("UnusedParameters")
    protected void onInvitationDone(BaseResponseDTO args)
    {
        THToast.show(R.string.invite_friend_success);
        getProgressDialog().hide();
    }

    protected void onInvitationError(Throwable e)
    {
        THToast.show(new THException(e));
        getProgressDialog().hide();
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = ProgressDialogUtil.show(
                activityProvider.get(),
                R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }
}
