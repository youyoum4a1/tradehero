package com.tradehero.th.fragments.leaderboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import java.util.ArrayList;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener
{
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_display_name) TextView name;
    @InjectView(R.id.leaderboard_user_item_social_name) TextView socialName;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.leaderboard_user_item_country_logo) ImageView countryLogo;
    @InjectView(R.id.leaderboard_user_item_invite_btn) TextView inviteBtn;

    private LeaderboardUserDTO mLeaderboardUserDTO;
    private MiddleCallback<Response> middleCallbackInvite;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    private ProgressDialog progressDialog;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<SocialServiceWrapper> socialServiceWrapperLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    public LeaderboardFriendsItemView(Context context)
    {
        super(context);
    }

    public LeaderboardFriendsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardFriendsItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        loadDefaultPicture();
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

    @Override public void display(LeaderboardUserDTO dto)
    {
        //Timber.d("lyl %s", dto.toString());
        mLeaderboardUserDTO = dto;
        if (mLeaderboardUserDTO != null)
        {
            //updatePosition();
            displayPicture();
            updateName();
            updateROI();
            displayCountryLogo();
            updateInviteButton();
        }
    }

    public void updatePosition(int position)
    {
        if (lbmuPosition != null)
        {
            lbmuPosition.setText("" + (position + 1));
        }
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (mLeaderboardUserDTO != null && getPicture() != null)
            {
                picasso.load(getPicture())
                        .transform(peopleIconTransformation)
                        .placeholder(avatar.getDrawable())
                        .into(avatar);
            }

            if (mLeaderboardUserDTO.displayName != null
                    && !mLeaderboardUserDTO.displayName.isEmpty())
            {
                avatar.setOnClickListener(this);
            }
        }
    }

    public String getPicture()
    {
        if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.picture != null)
        {
            return mLeaderboardUserDTO.picture;
        }
        else if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.fbPicUrl != null)
        {
            return mLeaderboardUserDTO.fbPicUrl;
        }
        else if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.liPicUrl != null)
        {
            return mLeaderboardUserDTO.liPicUrl;
        }
        else if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.twPicUrl != null)
        {
            return mLeaderboardUserDTO.twPicUrl;
        }
        return null;
    }

    public void updateName()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            if (mLeaderboardUserDTO.displayName.isEmpty())
            {
                name.setText(mLeaderboardUserDTO.firstName + " " + mLeaderboardUserDTO.lastName);
            }
            else
            {
                name.setText(mLeaderboardUserDTO.displayName);
            }
            name.setVisibility(VISIBLE);
            socialName.setVisibility(INVISIBLE);
        }
        else if (mLeaderboardUserDTO.name != null && !mLeaderboardUserDTO.name.isEmpty())
        {
            name.setVisibility(INVISIBLE);
            socialName.setVisibility(VISIBLE);
            socialName.setText(mLeaderboardUserDTO.name);
        }
    }

    public void updateROI()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                    mLeaderboardUserDTO.roiInPeriod * 100);
            lbmuRoi.setText(roi.toString());
            lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

            THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                    mLeaderboardUserDTO.roiAnnualizedInPeriod * 100);
            String roiAnnualizedFormat =
                    getContext().getString(R.string.leaderboard_roi_annualized);
            String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
            lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));
            lbmuRoi.setVisibility(VISIBLE);
            lbmuRoiAnnualized.setVisibility(VISIBLE);
        }
        else
        {
            lbmuRoi.setVisibility(INVISIBLE);
            lbmuRoiAnnualized.setVisibility(INVISIBLE);
        }
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null)
        {
            if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.countryCode != null)
            {
                countryLogo.setImageResource(getCountryLogoId(0, mLeaderboardUserDTO.countryCode));
                countryLogo.setVisibility(VISIBLE);
            }
            else
            {
                countryLogo.setVisibility(GONE);
            }
        }
    }

    public int getCountryLogoId(int defaultResId, String country)
    {
        try
        {
            return Country.valueOf(country).logoId;
        } catch (IllegalArgumentException ex)
        {
            return defaultResId;
        }
    }

    private void updateInviteButton()
    {
        if (inviteBtn != null)
        {
            if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.name != null)
            {
                inviteBtn.setVisibility(VISIBLE);
                inviteBtn.setOnClickListener(this);
            }
            else
            {
                inviteBtn.setVisibility(GONE);
            }
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.leaderboard_user_item_profile_picture:
                handleOpenProfileButtonClicked();
                break;
            case R.id.leaderboard_user_item_invite_btn:
                invite();
                break;
        }
    }

    private void handleOpenProfileButtonClicked()
    {
        if (mLeaderboardUserDTO != null && currentUserId != null
                && currentUserId.get() != mLeaderboardUserDTO.id)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, mLeaderboardUserDTO.id);
            DashboardNavigator dashboardNavigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    private void invite()
    {
        Timber.d("lyl invite");
        if (mLeaderboardUserDTO.liId != null || mLeaderboardUserDTO.twId != null)
        {
            InviteFormDTO inviteFriendForm = new InviteFormDTO();
            inviteFriendForm.users = new ArrayList<>();
            InviteDTO inviteDTO = new InviteDTO();
            if (!mLeaderboardUserDTO.liId.isEmpty())
            {
                inviteDTO.liId = mLeaderboardUserDTO.liId;
            }
            else if (!mLeaderboardUserDTO.twId.isEmpty())
            {
                inviteDTO.twId = mLeaderboardUserDTO.twId;
            }
            inviteFriendForm.users.add(inviteDTO);
            getProgressDialog().show();
            detachMiddleCallbackInvite();
            middleCallbackInvite = userServiceWrapperLazy.get()
                    .inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm,
                            new TrackShareCallback());
        }
        else if (mLeaderboardUserDTO.fbId != null)
        {
            if (Session.getActiveSession() == null)
            {
                facebookUtils.get().logIn(currentActivityHolderLazy.get().getCurrentActivity(),
                        new TrackFacebookCallback());
            }
            else
            {
                sendRequestDialog();
            }
        }
    }

    private void sendRequestDialog()
    {
        Timber.d("lyl sendRequestDialog");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLeaderboardUserDTO.fbId);
        Timber.d("lyl list of fbIds: %s", stringBuilder.toString());

        Bundle params = new Bundle();
        String messageToFacebookFriends = getContext().getString(
                R.string.invite_friend_facebook_tradehero_refer_friend_message);
        if (messageToFacebookFriends.length() > 60)
        {
            messageToFacebookFriends = messageToFacebookFriends.substring(0, 60);
        }

        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(
                currentActivityHolderLazy.get().getCurrentActivity(), Session.getActiveSession(),
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

    private void detachMiddleCallbackInvite()
    {
        if (middleCallbackInvite != null)
        {
            middleCallbackInvite.setPrimaryCallback(null);
        }
        middleCallbackInvite = null;
    }

    @Override protected void onDetachedFromWindow()
    {
        //Timber.d("lyl onDetachedFromWindow");
        avatar.setOnClickListener(null);
        inviteBtn.setOnClickListener(null);
        detachMiddleCallbackInvite();
        super.onDetachedFromWindow();
    }

    public void setPosition(int position)
    {
        updatePosition(position);
    }

    private class TrackShareCallback implements retrofit.Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            Timber.d("lyl success " + response);
            THToast.show(R.string.invite_friend_success);
            getProgressDialog().hide();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().hide();
        }
    }

    private class TrackFacebookCallback extends LogInCallback
    {
        @Override public void done(UserLoginDTO user, THException ex)
        {
            Timber.d("lyl done");
            getProgressDialog().dismiss();
        }

        @Override public void onStart()
        {
            Timber.d("lyl onStart");
            getProgressDialog().show();
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            Timber.d("lyl onSocialAuthDone");
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
                currentActivityHolderLazy.get().getCurrentContext(),
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
            Timber.d("lyl success");
            userProfileCacheLazy.get().put(currentUserId.toUserBaseKey(), userProfileDTO);
            invite();
        }

        @Override protected void failure(THException ex)
        {
            Timber.d("lyl failure");
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            getProgressDialog().dismiss();
        }
    }
}
