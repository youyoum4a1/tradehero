package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ScreenFlowEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener,
        ExpandingLayout.OnExpandListener
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject Lazy<Picasso> picasso;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<UserProfileCache> userProfileCache;

    protected UserProfileDTO currentUserProfileDTO;
    protected OnFollowRequestedListener followRequestedListener;
    protected OwnedPortfolioId applicablePortfolioId;
    protected FollowDialogCombo followDialogCombo;
    // data
    protected LeaderboardUserDTO leaderboardItem;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;

    // top view
    @InjectView(R.id.leaderboard_user_item_display_name) TextView lbmuDisplayName;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView lbmuProfilePicture;
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_hq) TextView lbmuHeroQuotient;
    @InjectView(R.id.leaderboard_user_item_info) ImageView lbmuPositionInfo;

    // expanding view
    @InjectView(R.id.lbmu_pl) TextView lbmuPl;
    @InjectView(R.id.lbmu_roi) TextView lbmuRoi;
    @InjectView(R.id.lbmu_comments_count) TextView lbmuCommentsCount;
    @InjectView(R.id.lbmu_benchmark_roi) TextView lbmuBenchmarkRoi;
    @InjectView(R.id.lbmu_sharpe_ratio) TextView lbmuSharpeRatio;
    @InjectView(R.id.lbmu_positions_count) TextView lbmuPositionsCount;
    @InjectView(R.id.lbmu_avg_days_held) TextView lbmuAvgDaysHeld;
    @InjectView(R.id.lbmu_followers_count) TextView lbmuFollowersCount;
    @InjectView(R.id.lbmu_roi_annualized) TextView lbmuRoiAnnualized;
    @InjectView(R.id.lbmu_win_ratio) TextView lbmuWinRatio;
    @InjectView(R.id.lbmu_volatility) TextView lbmuVolatility;
    @InjectView(R.id.lbmu_number_of_trades) TextView lbmuNumberOfTrades;
    @InjectView(R.id.lbmu_period) TextView lbmuPeriod;
    @InjectView(R.id.leaderboard_user_item_fof) @Optional @Nullable MarkdownTextView lbmuFoF;
    @InjectView(R.id.lbmu_number_trades_in_period) @Optional @Nullable TextView lbmuNumberTradesInPeriod;
    @InjectView(R.id.leaderboard_user_item_follow) @Optional @Nullable View lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) @Optional @Nullable View lbmuFollowingUser;

    @InjectView(R.id.expanding_layout) ExpandingLayout expandingLayout;
    @InjectView(R.id.leaderboard_user_item_country_logo) @Optional @Nullable ImageView countryLogo;
    @InjectView(R.id.user_statistic_view) @Optional @Nullable UserStatisticView userStatisticView;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserItemView(Context context)
    {
        super(context);
    }

    @Override protected void onVisibilityChanged(View changedView, int visibility)
    {
        super.onVisibilityChanged(changedView, visibility);
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardMarkUserItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        ButterKnife.inject(this);
        // top part
        if (lbmuFoF != null)
        {
            DaggerUtils.inject(lbmuFoF);
        }

        TextView lbmuOpenProfile = (TextView) findViewById(R.id.leaderboard_user_item_open_profile);
        if (lbmuOpenProfile != null)
        {
            lbmuOpenProfile.setOnClickListener(this);
        }
        TextView lbmuOpenPositionsList =
                (TextView) findViewById(R.id.leaderboard_user_item_open_positions_list);
        if (lbmuOpenPositionsList != null)
        {
            lbmuOpenPositionsList.setOnClickListener(this);
        }

        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        if (expandingLayout != null)
        {
            expandingLayout.setOnExpandListener(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        initViews();
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (lbmuPositionInfo != null)
        {
            lbmuPositionInfo.setOnClickListener(this);
        }
        if (lbmuFollowUser != null)
        {
            lbmuFollowUser.setOnClickListener(this);
        }
        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setOnClickListener(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(null);
        }
        if (lbmuPositionInfo != null)
        {
            lbmuPositionInfo.setOnClickListener(null);
        }
        if (lbmuFollowUser != null)
        {
            lbmuFollowUser.setOnClickListener(null);
        }
        loadDefaultUserImage();

        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setImageDrawable(null);
        }
        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setOnClickListener(null);
        }

        detachFreeFollowMiddleCallback();
        detachFollowDialogCombo();

        super.onDetachedFromWindow();
    }

    public void linkWith(UserProfileDTO currentUserProfileDTO, boolean andDisplay)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        if (andDisplay)
        {
            displayFollow();
            displayIsFollowing();
        }
    }

    public void linkWith(OwnedPortfolioId applicablePortfolioId)
    {
        this.applicablePortfolioId = applicablePortfolioId;
    }

    public Boolean isCurrentUserFollowing()
    {
        if (currentUserProfileDTO == null || leaderboardItem == null)
        {
            return null;
        }
        return currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey());
    }

    public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }

    @Override public void display(LeaderboardUserDTO expandableItem)
    {
        linkWith(expandableItem, true);
    }

    private void linkWith(LeaderboardUserDTO expandableItem, boolean andDisplay)
    {
        this.leaderboardItem = expandableItem;

        if (andDisplay)
        {
            display();
        }
    }

    private void display()
    {
        if (leaderboardItem == null)
        {
            return;
        }

        displayTopSection();
        displayExpandableSection();
        displayFollow();
        displayIsFollowing();
    }

    private void displayTopSection()
    {
        lbmuPosition.setText("" + (leaderboardItem.getPosition() + 1));
        if (currentUserId.get() == leaderboardItem.id)
        {
            lbmuPosition.setTextColor(
                    getContext().getResources().getColor(R.color.button_green));
        }
        else
        {
            lbmuPosition.setTextColor(
                    getContext().getResources().getColor(R.color.leaderboard_ranking_position));
        }

        lbmuDisplayName.setText(leaderboardItem.displayName);
        lbmuHeroQuotient.setText(leaderboardItem.getHeroQuotientFormatted());
        if (lbmuFoF != null)
        {
            lbmuFoF.setVisibility(
                    leaderboardItem.isIncludeFoF() != null && leaderboardItem.isIncludeFoF() &&
                            !StringUtils.isNullOrEmptyOrSpaces(
                                    leaderboardItem.friendOfMarkupString) ? VISIBLE : GONE);
            lbmuFoF.setText(leaderboardItem.friendOfMarkupString);
        }

        loadDefaultUserImage();
        if (leaderboardItem.picture != null)
        {
            picasso.get()
                    .load(leaderboardItem.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(lbmuProfilePicture.getDrawable())
                    .into(lbmuProfilePicture);
        }
        displayCountryLogo();
    }

    private void loadDefaultUserImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(lbmuProfilePicture);
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null)
        {
            try
            {
                if (leaderboardItem != null && leaderboardItem.countryCode != null)
                {
                    countryLogo.setImageResource(getCountryLogoId(leaderboardItem.countryCode));
                }
                else
                {
                    countryLogo.setImageResource(R.drawable.default_image);
                }
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, null);
            }
        }
    }

    public int getCountryLogoId(String country)
    {
        return getCountryLogoId(R.drawable.default_image, country);
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

    private void displayExpandableSection()
    {
        // display P&L
        displayLbmuPl();

        // display period
        String periodFormat = getContext().getString(R.string.leaderboard_ranking_period);
        SimpleDateFormat sdf =
                new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        String formattedEndPeriodUtc = sdf.format(leaderboardItem.periodEndUtc);
        String period = String.format(periodFormat, formattedStartPeriodUtc, formattedEndPeriodUtc);
        lbmuPeriod.setText(period);

        // display Roi
        THSignedNumber roi = THSignedPercentage
                .builder(leaderboardItem.roiInPeriod * 100)
                .withSign()
                .signTypeArrow()
                .relevantDigitCount(3)
                .build();
        lbmuRoi.setText(roi.toString());
        lbmuRoi.setTextColor(getResources().getColor(roi.getColorResId()));

        // display Roi annualized
        THSignedNumber roiAnnualizedVal = THSignedPercentage
                .builder(leaderboardItem.roiAnnualizedInPeriod * 100)
                .withSign()
                .signTypeArrow()
                .relevantDigitCount(3)
                .build();
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
        lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));

        // benchmark roi
        THSignedNumber benchmarkRoiInPeriodVal = THSignedPercentage
                .builder(leaderboardItem.getBenchmarkRoiInPeriod() * 100)
                .withSign()
                .signTypeArrow()
                .relevantDigitCount(3)
                .build();
        String benchmarkRoiInPeriodFormat =
                getContext().getString(R.string.leaderboard_benchmark_roi_format);
        String benchmarkRoiInPeriod =
                String.format(benchmarkRoiInPeriodFormat, benchmarkRoiInPeriodVal.toString());
        lbmuBenchmarkRoi.setText(Html.fromHtml(benchmarkRoiInPeriod));

        // sharpe ratio
        if (leaderboardItem.sharpeRatioInPeriodVsSP500 != null)
        {
            lbmuSharpeRatio.setText(THSignedNumber
                    .builder(leaderboardItem.sharpeRatioInPeriodVsSP500)
                    .build()
                    .toString());
        }
        else
        {
            lbmuSharpeRatio.setText("0");
        }

        // volatility
        String volatility = getContext().getString(R.string.leaderboard_volatility, leaderboardItem.getVolatility());
        lbmuVolatility.setText(Html.fromHtml(volatility));

        // number of positions holding
        lbmuPositionsCount.setText("" + leaderboardItem.numberOfPositionsInPeriod);

        // number of trades
        String numberOfTradeFormat = getContext().getString(
                leaderboardItem.getNumberOfTrades() > 1
                        ? R.string.leaderboard_number_of_trades_plural
                        : R.string.leaderboard_number_of_trade);
        String numberOfTrades =
                String.format(numberOfTradeFormat, leaderboardItem.getNumberOfTrades());
        lbmuNumberOfTrades.setText(Html.fromHtml(numberOfTrades));

        // Number of trades in Period
        if (lbmuNumberTradesInPeriod != null)
        {
            lbmuNumberTradesInPeriod.setText(THSignedNumber
                    .builder(leaderboardItem.numberOfTradesInPeriod)
                    .build().toString());
        }

        // average days held
        lbmuAvgDaysHeld.setText(THSignedNumber
                .builder(leaderboardItem.avgHoldingPeriodMins / (60 * 24))
                .relevantDigitCount(3)
                .build().toString());
        lbmuWinRatio.setText(THSignedPercentage
                .builder(leaderboardItem.getWinRatio() * 100)
                .relevantDigitCount(3)
                .build().toString());

        // followers & comments count
        lbmuFollowersCount.setText(THSignedNumber
                .builder(leaderboardItem.getTotalFollowersCount())
                .build().toString());
        lbmuCommentsCount.setText(THSignedNumber
                .builder(leaderboardItem.getCommentsCount())
                .build().toString());
    }

    @Override public void onExpand(boolean expand)
    {
        if (userStatisticView != null)
        {
            if (expand)
            {
                userStatisticView.display(leaderboardItem);
            }
            else
            {
                userStatisticView.display(null);
                Timber.d("clearExpandAnimation");
            }
        }
    }

    protected void displayLbmuPl()
    {
        if (lbmuPl != null && leaderboardItem != null)
        {
            THSignedNumber formattedNumber = THSignedMoney
                    .builder(leaderboardItem.PLinPeriodRefCcy)
                    .withOutSign()
                    .currency(getLbmuPlCurrencyDisplay())
                    .build();
            lbmuPl.setText(formattedNumber.toString());
        }
    }

    protected String getLbmuPlCurrencyDisplay()
    {
        if (leaderboardItem != null)
        {
            return leaderboardItem.getNiceCurrency();
        }
        return SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY;
    }

    private void displayFollow()
    {
        if (lbmuFollowUser != null)
        {
            Boolean isFollowing = isCurrentUserFollowing();
            boolean showButton = isFollowing == null || !isFollowing;
            lbmuFollowUser.setVisibility(showButton ? VISIBLE : GONE);
        }
    }

    private void displayIsFollowing()
    {
        if (lbmuFollowingUser != null)
        {
            Boolean isFollowing = isCurrentUserFollowing();
            boolean showImage = isFollowing != null && isFollowing;
            lbmuFollowingUser.setVisibility(showImage ? VISIBLE : GONE);
        }
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.leaderboard_user_item_info:
                // TODO right now the icon is gone
                break;
            case R.id.leaderboard_user_item_open_profile:
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Profile));
                handleOpenProfileButtonClicked();
                break;

            case R.id.leaderboard_user_item_open_positions_list:
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Positions));
                handleOpenPositionListClicked();
                break;

            case R.id.leaderboard_user_item_follow:
                analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboard_Follow));
                detachFollowDialogCombo();
                followDialogCombo = alertDialogUtilLazy.get().showFollowDialog(getContext(), leaderboardItem,
                        UserProfileDTOUtil.IS_NOT_FOLLOWER,
                        new LeaderBoardFollowRequestedListener());
                break;
            case R.id.leaderboard_user_item_profile_picture:
                handleUserIconClicked();
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

    public class FreeFollowCallback implements retrofit.Callback<UserProfileDTO>
    {
        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            alertDialogUtilLazy.get().dismissProgressDialog();
            LeaderboardMarkUserItemView.this.linkWith(userProfileDTO, true);
            userProfileCacheLazy.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
            analytics.addEvent(new ScreenFlowEvent(AnalyticsConstants.FreeFollow_Success, AnalyticsConstants.Leaderboard));
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    private void handleUserIconClicked()
    {
        //OtherTimelineFragment.viewProfile((DashboardActivity) getContext(), null);
        handleOpenProfileButtonClicked();
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

    private void handleOpenPositionListClicked()
    {
        @Nullable GetPositionsDTOKey getPositionsDTOKey = leaderboardItem.getGetPositionsDTOKey();
        if (getPositionsDTOKey == null)
        {
            Timber.e(new NullPointerException(), "Unable to get positions %s", leaderboardItem);
            THToast.show(R.string.leaderboard_friends_position_failed);
            return;
        }

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        @Nullable LeaderboardDefDTO leaderboardDef = null;
        Integer leaderboardId = leaderboardItem.getLeaderboardId();
        if (leaderboardId != null)
        {
            leaderboardDef = leaderboardDefCache.get()
                    .get(new LeaderboardDefKey(leaderboardItem.getLeaderboardId()));
        }

        if (leaderboardItem.lbmuId != -1)
        {
            pushLeaderboardPositionListFragment(getPositionsDTOKey, leaderboardDef);
        }
        else
        {
            pushPositionListFragment(getPositionsDTOKey);
        }
    }

    protected void pushLeaderboardPositionListFragment(GetPositionsDTOKey getPositionsDTOKey, LeaderboardDefDTO leaderboardDefDTO)
    {
        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();
        LeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, getPositionsDTOKey);
        LeaderboardPositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
        if (leaderboardDefDTO != null)
        {
            LeaderboardPositionListFragment.putLeaderboardTimeRestricted(bundle, leaderboardDefDTO.isTimeRestrictedLeaderboard());
        }
        SimpleDateFormat sdf =
                new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        LeaderboardPositionListFragment.putLeaderboardPeriodStartString(bundle, formattedStartPeriodUtc);

        if (applicablePortfolioId != null)
        {
            LeaderboardPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        getNavigator().pushFragment(LeaderboardPositionListFragment.class, bundle);
    }

    protected void pushPositionListFragment(GetPositionsDTOKey getPositionsDTOKey)
    {
        Bundle bundle = new Bundle();
        PositionListFragment.putGetPositionsDTOKey(bundle, getPositionsDTOKey);
        PositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());

        if (applicablePortfolioId != null)
        {
            PositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        getNavigator().pushFragment(PositionListFragment.class, bundle);
    }

    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }

    private void handleOpenProfileButtonClicked()
    {
        int userId = leaderboardItem.id;

        if (currentUserId != null && currentUserId.get() != userId)
        {
            Bundle bundle = new Bundle();
            thRouter.save(bundle, new UserBaseKey(userId));
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected void handleSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        linkWith(userProfileDTO, true);
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

    private void detachFreeFollowMiddleCallback()
    {
        if (freeFollowMiddleCallback != null)
        {
            freeFollowMiddleCallback.setPrimaryCallback(null);
        }
        freeFollowMiddleCallback = null;
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
}
