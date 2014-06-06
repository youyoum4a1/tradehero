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
import butterknife.Optional;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.GaugeView;
import com.tradehero.common.widget.NumericalAnimatedTextView;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.social.InviteDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
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
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener,
        ExpandingLayout.OnExpandListener
{
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_lable) ImageView lable;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_display_name) TextView name;
    @InjectView(R.id.leaderboard_user_item_social_name) TextView socialName;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.leaderboard_user_item_country_logo) ImageView countryLogo;
    @InjectView(R.id.leaderboard_user_item_invite_btn) TextView inviteBtn;

    @InjectView(R.id.expanding_layout) ExpandingLayout expandingLayout;
    @InjectView(R.id.leaderboard_gauge_winrate) @Optional GaugeView winRateGauge;
    @InjectView(R.id.leaderboard_gauge_performance) @Optional GaugeView performanceGauge;
    @InjectView(R.id.leaderboard_gauge_tradeconsistency) @Optional GaugeView tradeConsistencyGauge;
    @InjectView(R.id.leaderboard_dayshold_tv) @Optional NumericalAnimatedTextView daysHoldTv;
    @InjectView(R.id.leaderboard_position_tv) @Optional NumericalAnimatedTextView positionsCountTv;
    @InjectView(R.id.leaderboard_tradecount_tv) @Optional NumericalAnimatedTextView tradeCountTv;
    @InjectView(R.id.leaderboard_user_item_open_profile) @Optional TextView lbmuOpenProfile;
    @InjectView(R.id.leaderboard_user_item_open_positions_list) @Optional TextView
            lbmuOpenPositionsList;
    @InjectView(R.id.leaderboard_user_item_follow) @Optional RelativeLayout lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) @Optional RelativeLayout lbmuFollowingUser;

    private LeaderboardUserDTO mLeaderboardUserDTO;
    private MiddleCallback<Response> middleCallbackInvite;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    private MiddleCallback<UserProfileDTO> middleCallbackConnect;
    protected OnFollowRequestedListener followRequestedListener;
    private ProgressDialog progressDialog;
    protected UserProfileDTO currentUserProfileDTO;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
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
        if (expandingLayout != null)
        {
            expandingLayout.setOnExpandListener(this);
            expandingLayout.setVisibility(GONE);
        }
        if (lbmuOpenProfile != null)
        {
            lbmuOpenProfile.setOnClickListener(this);
        }
        if (lbmuOpenPositionsList != null)
        {
            lbmuOpenPositionsList.setOnClickListener(this);
        }
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
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(GONE);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        avatar.setOnClickListener(null);
        inviteBtn.setOnClickListener(null);
        detachMiddleCallbackInvite();
        if (lbmuOpenProfile != null)
        {
            lbmuOpenProfile.setOnClickListener(null);
            lbmuOpenProfile = null;
        }
        if (lbmuOpenPositionsList != null)
        {
            lbmuOpenPositionsList.setOnClickListener(null);
            lbmuOpenPositionsList = null;
        }
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

    @Override public void display(LeaderboardUserDTO dto)
    {
        mLeaderboardUserDTO = dto;
        if (mLeaderboardUserDTO != null)
        {
            updatePosition();
            displayPicture();
            updateName();
            updateROI();
            displayCountryLogo();
            updateInviteButton();
            showValueWithoutAnimation();
        }
    }

    private void updatePosition()
    {
        if (lable != null)
        {
            boolean isSocial = false;
            if (mLeaderboardUserDTO.getLableRes() != null)
            {
                isSocial = true;
                lable.setBackgroundResource(mLeaderboardUserDTO.getLableRes());
            }
            if (isSocial)
            {
                lbmuPosition.setVisibility(INVISIBLE);
                lable.setVisibility(VISIBLE);
            }
            else
            {
                lbmuPosition.setVisibility(VISIBLE);
                lable.setVisibility(INVISIBLE);
            }
        }
    }

    public void setPosition(int position)
    {
        if (lbmuPosition != null)
        {
            lbmuPosition.setText("" + (position + 1));
            if (currentUserId.get() == mLeaderboardUserDTO.id)
            {
                lbmuPosition.setTextColor(
                        getContext().getResources().getColor(R.color.button_green));
            }
            else
            {
                lbmuPosition.setTextColor(
                        getContext().getResources().getColor(R.color.leaderboard_ranking_position));
            }
        }
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.getPicture() != null)
            {
                picasso.load(mLeaderboardUserDTO.getPicture())
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

    private void showValueWithoutAnimation()
    {
        if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.displayName != null)
        {
            if (tradeCountTv != null)
            {
                tradeCountTv.setEndValue(
                        mLeaderboardUserDTO.avgNumberOfTradesPerMonth.floatValue());
                tradeCountTv.setFractionDigits(2);
            }
            if (daysHoldTv != null)
            {
                daysHoldTv.setEndValue(mLeaderboardUserDTO.avgHoldingPeriodMins * 1.0f / (60 * 24));
                daysHoldTv.setFractionDigits(2);
            }
            if (positionsCountTv != null)
            {
                positionsCountTv.setEndValue(mLeaderboardUserDTO.numberOfPositionsInPeriod);
                positionsCountTv.setFractionDigits(0);
            }

            String digitsWinRatio =
                    NumberDisplayUtils.formatWithRelevantDigits(
                            mLeaderboardUserDTO.getWinRatio() * 100, 3);
            if (winRateGauge != null)
            {
                winRateGauge.setContentText(digitsWinRatio + "%");
                winRateGauge.setSubText(
                        getContext().getString(R.string.leaderboard_win_ratio_title));
                winRateGauge.setAnimiationFlag(false);
                winRateGauge.setCurrentValue((float) mLeaderboardUserDTO.getWinRatio() * 100);
            }

            if (performanceGauge != null)
            {
                performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
                performanceGauge.setSubText(
                        getContext().getString(R.string.leaderboard_performance_title));
                performanceGauge.setAnimiationFlag(false);
                performanceGauge.setDrawStartValue(50f);
                performanceGauge.setCurrentValue((float) mLeaderboardUserDTO.normalizePerformance());
            }

            if (tradeConsistencyGauge != null)
            {
                tradeConsistencyGauge.setSubText(
                        getContext().getString(R.string.leaderboard_consistency_title));
                tradeConsistencyGauge.setAnimiationFlag(false);
                tradeConsistencyGauge.setCurrentValue((float) normalizeConsistency());
            }
            Timber.d("showValueWithoutAnimation normalizeConsistency %s", normalizeConsistency());

            if (tradeCountTv != null)
            {
                tradeCountTv.showText();
            }
            if (daysHoldTv != null)
            {
                daysHoldTv.showText();
            }
            if (positionsCountTv != null)
            {
                positionsCountTv.showText();
            }
        }
    }

    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.leaderboard_user_item_profile_picture:
            case R.id.leaderboard_user_item_open_profile:
                if (mLeaderboardUserDTO.displayName != null
                        && !mLeaderboardUserDTO.displayName.isEmpty())
                {
                    handleOpenProfileButtonClicked();
                }
                break;
            case R.id.leaderboard_user_item_invite_btn:
                invite();
                break;
            case R.id.leaderboard_user_item_open_positions_list:
                handleOpenPositionListClicked();
                break;
            case R.id.leaderboard_user_item_follow:
                alertDialogUtilLazy.get().showFollowDialog(getContext(), mLeaderboardUserDTO,
                        UserProfileDTOUtil.IS_NOT_FOLLOWER,
                        new LeaderBoardFollowRequestedListener());
                break;
        }
    }

    public class LeaderBoardFollowRequestedListener
            implements com.tradehero.th.models.social.OnFollowRequestedListener
    {
        @Override public void freeFollowRequested(UserBaseKey heroId)
        {
            freeFollow(heroId);
        }

        @Override public void premiumFollowRequested(UserBaseKey heroId)
        {
            follow(heroId);
        }
    }

    protected void freeFollow(UserBaseKey heroId)
    {
        alertDialogUtilLazy.get().showProgressDialog(getContext(), getContext().getString(
                R.string.following_this_hero));
        detachFreeFollowMiddleCallback();
        freeFollowMiddleCallback =
                userServiceWrapperLazy.get()
                        .freeFollow(heroId, new FreeFollowCallback());
    }

    protected void follow(UserBaseKey heroId)
    {
        notifyFollowRequested(heroId);
    }

    protected void notifyFollowRequested(UserBaseKey heroId)
    {
        OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(heroId);
        }
    }

    public static interface OnFollowRequestedListener
    {
        void onFollowRequested(UserBaseKey userBaseKey);
    }

    public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
    }

    public class FreeFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
            linkWith(userProfileDTO, true);
            userProfileCacheLazy.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
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
        if (lbmuFollowUser != null)
        {
            if (currentUserId.get() == mLeaderboardUserDTO.id)
            {
                lbmuFollowUser.setVisibility(GONE);
            }
            else
            {
                boolean showButton = isFollowing == null || !isFollowing;
                lbmuFollowUser.setVisibility(showButton ? VISIBLE : GONE);
            }
        }
        if (lbmuFollowingUser != null)
        {
            if (currentUserId.get() == mLeaderboardUserDTO.id)
            {
                lbmuFollowingUser.setVisibility(GONE);
            }
            else
            {
                boolean showImage = isFollowing != null && isFollowing;
                lbmuFollowingUser.setVisibility(showImage ? VISIBLE : GONE);
            }
        }
    }

    public Boolean isCurrentUserFollowing()
    {
        if (currentUserProfileDTO == null || mLeaderboardUserDTO == null)
        {
            return null;
        }
        return currentUserProfileDTO.isFollowingUser(mLeaderboardUserDTO.getBaseKey());
    }

    private void handleOpenPositionListClicked()
    {
        int userId = mLeaderboardUserDTO.id;

        // portfolio, to display position list
        int portfolioId = mLeaderboardUserDTO.portfolioId;
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(userId, portfolioId);

        Bundle bundle = new Bundle();
        // to display time of value on start investment
        SimpleDateFormat sdf =
                new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(mLeaderboardUserDTO.periodStartUtc);
        bundle.putString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING,
                formattedStartPeriodUtc);

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        //LeaderboardDefDTO leaderboardDef = leaderboardDefCache.get()
        //        .get(new LeaderboardDefKey(mLeaderboardUserDTO.getLeaderboardId()));
        //boolean isTimeRestrictedLeaderboard =
        //        leaderboardDef != null && leaderboardDef.isTimeRestrictedLeaderboard();
        //bundle.putBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED,
        //        isTimeRestrictedLeaderboard);

        if (mLeaderboardUserDTO.lbmuId != -1)
        {
            // leaderboard mark user id, to get marking user information
            bundle.putBundle(LeaderboardPositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE,
                    ownedPortfolioId.getArgs());
            bundle.putLong(LeaderboardMarkUserId.BUNDLE_KEY, mLeaderboardUserDTO.lbmuId);
            DashboardNavigator dashboardNavigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                dashboardNavigator.pushFragment(LeaderboardPositionListFragment.class, bundle);
            }
        }
        else
        {
            bundle.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE,
                    ownedPortfolioId.getArgs());
            DashboardNavigator dashboardNavigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                dashboardNavigator.pushFragment(PositionListFragment.class, bundle);
            }
        }
    }

    private void handleOpenProfileButtonClicked()
    {
        if (mLeaderboardUserDTO != null && currentUserId != null)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, mLeaderboardUserDTO.id);
            DashboardNavigator dashboardNavigator =
                    ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
            if (dashboardNavigator != null)
            {
                if (currentUserId.get() == mLeaderboardUserDTO.id)
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
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLeaderboardUserDTO.fbId);

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

    @Override public void onExpand(boolean expand)
    {
        if (mLeaderboardUserDTO != null && mLeaderboardUserDTO.displayName != null)
        {
            if (expand)
            {
                showExpandAnimation();
            }
            else
            {
                clearExpandAnimation();
            }
        }
        else
        {
            if (expandingLayout != null)
            {
                mLeaderboardUserDTO.setExpanded(false);
                expandingLayout.setVisibility(GONE);
            }
        }
    }

    private void clearExpandAnimation()
    {
        if (winRateGauge != null)
        {
            winRateGauge.clear();
        }
        if (performanceGauge != null)
        {
            performanceGauge.clear();
        }
        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.clear();
        }
    }

    private void showExpandAnimation()
    {
        String digitsWinRatio =
                NumberDisplayUtils.formatWithRelevantDigits(mLeaderboardUserDTO.getWinRatio() * 100,
                        3);
        if (winRateGauge != null)
        {
            winRateGauge.setContentText(digitsWinRatio + "%");
            winRateGauge.setSubText(getContext().getString(R.string.leaderboard_win_ratio_title));
            winRateGauge.setAnimiationFlag(true);
            winRateGauge.setTargetValue((float) mLeaderboardUserDTO.getWinRatio() * 100);
        }

        if (performanceGauge != null)
        {
            performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
            performanceGauge.setSubText(
                    getContext().getString(R.string.leaderboard_performance_title));
            performanceGauge.setAnimiationFlag(true);
            performanceGauge.setDrawStartValue(50f);
            performanceGauge.setTargetValue((float) mLeaderboardUserDTO.normalizePerformance());
        }

        if (tradeConsistencyGauge != null)
        {
            tradeConsistencyGauge.setSubText(
                    getContext().getString(R.string.leaderboard_consistency_title));
            tradeConsistencyGauge.setAnimiationFlag(true);
            tradeConsistencyGauge.setTargetValue((float) normalizeConsistency());
        }

        if (tradeCountTv != null)
        {
            tradeCountTv.startAnimation();
        }
        if (daysHoldTv != null)
        {
            daysHoldTv.startAnimation();
        }
        if (positionsCountTv != null)
        {
            positionsCountTv.startAnimation();
        }
    }

    private double normalizeConsistency()
    {
        try
        {
            Double minConsistency = LeaderboardUserDTO.MIN_CONSISTENCY;
            Double maxConsistency = getAvgConsistency();
            Double consistency = mLeaderboardUserDTO.getConsistency();
            consistency = (consistency < minConsistency) ? minConsistency : consistency;
            consistency = (consistency > maxConsistency) ? maxConsistency : consistency;

            double result =
                    100 * (consistency - minConsistency) / (maxConsistency - minConsistency);
            return result;
        } catch (Exception e)
        {
            Timber.e("normalizeConsistency", e);
        }
        return getAvgConsistency();
    }

    private Double getAvgConsistency()
    {
        UserProfileDTO userProfileDTO =
                userProfileCacheLazy.get().get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            return userProfileDTO.mostSkilledLbmu.getAvgConsistency();
        }
        return LeaderboardUserDTO.MIN_CONSISTENCY;
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
