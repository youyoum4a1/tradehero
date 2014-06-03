package com.tradehero.th.fragments.leaderboard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
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

/**
 * Created by tradehero on 14-5-29.
 */
public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener
{
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_display_name) TextView name;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.leaderboard_user_item_country_logo) ImageView countryLogo;
    @InjectView(R.id.leaderboard_user_item_invite_btn) Button inviteBtn;

    private LeaderboardUserDTO mLeaderboardUserDTO;
    private MiddleCallback<Response> middleCallbackInvite;
    private ProgressDialog progressDialog;
    @Inject CurrentUserId currentUserId;
    @Inject protected Picasso picasso;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Inject Lazy<FacebookUtils> facebookUtils;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;

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
        initViews();
    }

    private void initViews()
    {
        //upgradeNow.setOnClickListener(this);
        loadDefaultPicture();
        avatar.setOnClickListener(this);
        inviteBtn.setOnClickListener(this);
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
                        .into(avatar, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                //loadDefaultPicture();
                            }
                        });
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
        return null;
    }

    @Override public void display(LeaderboardUserDTO dto)
    {
        Timber.d("lyl %s", dto.toString());
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

    private void updateInviteButton()
    {
        if (inviteBtn != null)
        {
            if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.name != null)
            {
                inviteBtn.setVisibility(mLeaderboardUserDTO.alreadyInvited ? INVISIBLE : VISIBLE);
            }
            else
            {
                inviteBtn.setVisibility(GONE);
            }
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
                //countryLogo.setImageResource(R.drawable.default_image);
                countryLogo.setVisibility(GONE);
            }
        }
    }

    public int getCountryLogoId(int defaultResId, String country)
    {
        try
        {
            return Country.valueOf(country).logoId;
        }
        catch (IllegalArgumentException ex)
        {
            return defaultResId;
        }
    }

    public void updateROI()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, mLeaderboardUserDTO.roiInPeriod * 100);
            lbmuRoi.setText(roi.toString());
            lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

            THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                    mLeaderboardUserDTO.roiAnnualizedInPeriod * 100);
            String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
            String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
            lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));
        }
    }

    public void updatePosition(int position)
    {
        if (lbmuPosition != null)
        {
            lbmuPosition.setText("" + (position + 1));
        }
    }

    public void updateName()
    {
        if (mLeaderboardUserDTO.displayName != null)
        {
            if (mLeaderboardUserDTO.displayName.isEmpty())
            {
                name.setText(mLeaderboardUserDTO.firstName + mLeaderboardUserDTO.lastName);
            }
            else {
                name.setText(mLeaderboardUserDTO.displayName);
            }
        }
        else if (mLeaderboardUserDTO.name != null && !mLeaderboardUserDTO.name.isEmpty())
        {
            name.setText(mLeaderboardUserDTO.name);
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

    private void invite()
    {
        Timber.d("lyl invite");
        if (!mLeaderboardUserDTO.liId.isEmpty() || !mLeaderboardUserDTO.twId.isEmpty())
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
            progressDialogUtilLazy.get().show(getContext(), null, null);
            detachMiddleCallbackInvite();
            middleCallbackInvite = userServiceWrapperLazy.get().inviteFriends(currentUserId.toUserBaseKey(), inviteFriendForm, new TrackShareCallback());
        }
        else if (!mLeaderboardUserDTO.liId.isEmpty())
        {
            if (Session.getActiveSession() == null)
            {
                facebookUtils.get().logIn(currentActivityHolderLazy.get().getCurrentActivity(),
                        new LogInCallback()
                        {
                            @Override public void done(UserLoginDTO user, THException ex)
                            {
                                //if (!isDetached())
                                //{
                                    getProgressDialog().dismiss();
                                //}
                            }

                            @Override public void onStart()
                            {
                                //if (!isDetached())
                                //{
                                    getProgressDialog().show();
                                //}
                            }

                            @Override public boolean onSocialAuthDone(JSONCredentials json)
                            {
                                return false;
                            }
                        });
                return;
            }
            //if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
            //{
                sendRequestDialog();
            //}
        }
    }

    private void sendRequestDialog()
    {
        StringBuilder stringBuilder = new StringBuilder();
        //if (selectedFacebookFriends != null && !selectedFacebookFriends.isEmpty())
        //{
        //    Collections.shuffle(selectedFacebookFriends);
        //    for (int i = 0; i < selectedFacebookFriends.size() && i < MAX_FACEBOOK_FRIENDS_RECEIVERS; ++i)
        //    {
                stringBuilder.append(mLeaderboardUserDTO.fbId);
            //}
        //}
        // disable loop
        //selectedFacebookFriends = null;
        // remove the last comma
        //if (stringBuilder.length() > 0)
        //{
        //    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        //}
        Timber.d("list of fbIds: %s", stringBuilder.toString());

        Bundle params = new Bundle();
        String messageToFacebookFriends = getContext().getString(
                R.string.invite_friend_facebook_tradehero_refer_friend_message);
        if (messageToFacebookFriends.length() > 60)
        {
            messageToFacebookFriends = messageToFacebookFriends.substring(0, 60);
        }

        params.putString("message", messageToFacebookFriends);
        params.putString("to", stringBuilder.toString());

        WebDialog requestsDialog = (new WebDialog.RequestsDialogBuilder(currentActivityHolderLazy.get().getCurrentActivity(), Session.getActiveSession(), params))
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

    private void handleOpenProfileButtonClicked()
    {
        int userId = mLeaderboardUserDTO.id;

        if (currentUserId != null && currentUserId.get() != userId)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);
            DashboardNavigator dashboardNavigator = ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                dashboardNavigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    @Override protected void onDetachedFromWindow()
    {
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
            // do nothing for now
            //finish();
            Timber.d("lyl success " + response);
            progressDialogUtilLazy.get().dismiss(getContext());
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            progressDialogUtilLazy.get().dismiss(getContext());
            //finish();
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
}
