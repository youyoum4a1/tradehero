package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.social.InviteFormUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.MiddleLogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<UserFriendsDTO>, View.OnClickListener
{
    @InjectView(R.id.leaderboard_user_item_network_label) ImageView networkLabel;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_social_name) TextView socialName;
    @InjectView(R.id.leaderboard_user_item_invite_btn) TextView inviteBtn;

    @InjectView(R.id.leaderboard_user_item_follow) @Optional RelativeLayout lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) @Optional RelativeLayout lbmuFollowingUser;

    @Nullable private UserFriendsDTO userFriendsDTO;
    private MiddleCallback<Response> middleCallbackInvite;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private MiddleLogInCallback middleTrackbackFacebook;
    private ProgressDialog progressDialog;
    protected UserProfileDTO currentUserProfileDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject Provider<Activity> activityProvider;
    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<SocialServiceWrapper> socialServiceWrapperLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject THRouter thRouter;
    @Inject Analytics analytics;
    @Inject DashboardNavigator dashboardNavigator;

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
        detachTrackbackFacebook();
        detachMiddleCallbackInvite();
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

    public Boolean isCurrentUserFollowing()
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
            detachMiddleCallbackInvite();
            middleCallbackInvite = userServiceWrapperLazy.get()
                    .inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm,
                            new TrackShareCallback());
        }
        else if (userFriendsDTO instanceof UserFriendsFacebookDTO)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.InviteFriends, AnalyticsConstants.Facebook));
            if (Session.getActiveSession() == null)
            {
                detachTrackbackFacebook();
                middleTrackbackFacebook = new MiddleLogInCallback(new TrackFacebookCallback());
                facebookUtils.get().logIn(activityProvider.get(),
                        middleTrackbackFacebook);
            }
            else
            {
                sendRequestDialogFacebook();
            }
        }
    }

    private void sendRequestDialogFacebook()
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((UserFriendsFacebookDTO) userFriendsDTO).fbId);

        UserProfileDTO userProfileDTO = userProfileCacheLazy.get().get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            Bundle params = new Bundle();
            String messageToFacebookFriends = getContext().getString(
                    R.string.invite_friend_facebook_tradehero_refer_friend_message, userProfileDTO.referralCode);
            //if (messageToFacebookFriends.length() > 60)
            //{
            //    messageToFacebookFriends = messageToFacebookFriends.substring(0, 60);
            //}

            params.putString("message", messageToFacebookFriends);
            params.putString("to", stringBuilder.toString());

            WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
                    activityProvider.get(), Session.getActiveSession(),
                    params))
                    .setOnCompleteListener(new WebDialog.OnCompleteListener()
                    {
                        @Override
                        public void onComplete(Bundle values, FacebookException error)
                        {
                            if (error != null)
                            {
                                if (error instanceof FacebookOperationCanceledException)
                                {
                                    THToast.show(R.string.invite_friend_request_canceled);
                                }
                            }
                            else
                            {
                                final String requestId = values.getString("request");
                                if (requestId != null)
                                {
                                    THToast.show(R.string.invite_friend_request_sent);
                                }
                                else
                                {
                                    THToast.show(R.string.invite_friend_request_canceled);
                                }
                            }
                        }
                    })
                    .build();
            requestsDialog.show();
        }
    }

    private void detachMiddleCallbackInvite()
    {
        if (middleCallbackInvite != null)
        {
            middleCallbackInvite.setPrimaryCallback(null);
        }
        middleCallbackInvite = null;
    }

    private class TrackShareCallback implements retrofit.Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            THToast.show(R.string.invite_friend_success);
            getProgressDialog().hide();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().hide();
        }
    }

    private void detachTrackbackFacebook()
    {
        if (middleTrackbackFacebook != null)
        {
            middleTrackbackFacebook.setInnerCallback(null);
        }
        middleTrackbackFacebook = null;
    }

    private class TrackFacebookCallback extends LogInCallback
    {
        @Override public void done(UserLoginDTO user, THException ex)
        {
            getProgressDialog().dismiss();
        }

        @Override public void onStart()
        {
            getProgressDialog().show();
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            detachMiddleCallbackConnect();
            middleCallbackConnect = socialServiceWrapperLazy.get().connect(
                    currentUserId.toUserBaseKey(), UserFormFactory.create(json),
                    new SocialLinkingCallback());
            progressDialog.setMessage(getContext().getString(
                    R.string.authentication_connecting_tradehero,
                    "Facebook"));
            return false;
        }
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(
                activityProvider.get(),
                R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }

    protected void detachMiddleCallbackConnect()
    {
        if (middleCallbackConnect != null)
        {
            middleCallbackConnect.setPrimaryCallback(null);
        }
        middleCallbackConnect = null;
    }

    private class SocialLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            userProfileCacheLazy.get().put(currentUserId.toUserBaseKey(), userProfileDTO);
            invite();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            getProgressDialog().dismiss();
        }
    }
}
