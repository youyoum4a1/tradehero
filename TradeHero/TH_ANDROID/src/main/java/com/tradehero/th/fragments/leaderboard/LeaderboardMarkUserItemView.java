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
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
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
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener,
        ExpandingLayout.OnExpandListener
{
    @Inject @ForUserPhoto Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject THLocalyticsSession localyticsSession;
    @Inject THRouter thRouter;

    protected UserProfileDTO currentUserProfileDTO;
    protected OnFollowRequestedListener followRequestedListener;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    private MiddleCallback<UserProfileDTO> freeFollowMiddleCallback;
    protected FollowDialogCombo followDialogCombo;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<UserProfileCache> userProfileCacheLazy;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    // data
    protected LeaderboardUserDTO leaderboardItem;

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
    @InjectView(R.id.leaderboard_user_item_fof) MarkdownTextView lbmuFoF;
    @InjectView(R.id.lbmu_number_trades_in_period) TextView lbmuNumberTradesInPeriod;
    @InjectView(R.id.leaderboard_user_item_follow) @Optional View lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) @Optional View lbmuFollowingUser;

    @InjectView(R.id.expanding_layout) ExpandingLayout expandingLayout;
    @InjectView(R.id.leaderboard_user_item_country_logo) @Optional ImageView countryLogo;
    @InjectView(R.id.user_statistic_view) @Optional UserStatisticView userStatisticView;

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
        if (leaderboardItem != null)
        {
        }

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
                    leaderboardItem.isIncludeFoF() != null &&leaderboardItem.isIncludeFoF() &&
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
            }
            catch (OutOfMemoryError e)
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
        }
        catch (IllegalArgumentException ex)
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
        THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                leaderboardItem.roiInPeriod * 100);
        lbmuRoi.setText(roi.toString());
        lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

        // display Roi annualized
        THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                leaderboardItem.roiAnnualizedInPeriod * 100);
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
        lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));

        // benchmark roi
        THSignedNumber benchmarkRoiInPeriodVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE,
                leaderboardItem.getBenchmarkRoiInPeriod() * 100);
        String benchmarkRoiInPeriodFormat =
                getContext().getString(R.string.leaderboard_benchmark_roi_format);
        String benchmarkRoiInPeriod =
                String.format(benchmarkRoiInPeriodFormat, benchmarkRoiInPeriodVal.toString());
        lbmuBenchmarkRoi.setText(Html.fromHtml(benchmarkRoiInPeriod));

        // sharpe ratio
        if (leaderboardItem.sharpeRatioInPeriodVsSP500 != null)
        {
            lbmuSharpeRatio.setText(new THSignedNumber(THSignedNumber.TYPE_MONEY,
                    leaderboardItem.sharpeRatioInPeriodVsSP500, THSignedNumber.WITHOUT_SIGN).toString());
        }
        else
        {
            lbmuSharpeRatio.setText("0");
        }

        // volatility
        String volatilityFormat = getContext().getString(R.string.leaderboard_volatility);
        String volatility = String.format(volatilityFormat, leaderboardItem.getVolatility());
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
        lbmuNumberTradesInPeriod.setText(
                String.format("%,d", leaderboardItem.numberOfTradesInPeriod));

        // average days held
        lbmuAvgDaysHeld.setText(NumberDisplayUtils.formatWithRelevantDigits(
                (double) leaderboardItem.avgHoldingPeriodMins / (60 * 24), 3));
        String winRatioFormat = getContext().getString(R.string.leaderboard_win_ratio);
        String digitsWinRatio =
                NumberDisplayUtils.formatWithRelevantDigits(leaderboardItem.getWinRatio() * 100, 3);
        String winRatio = String.format(winRatioFormat, digitsWinRatio);
        lbmuWinRatio.setText(digitsWinRatio + "%");

        // followers & comments count
        lbmuFollowersCount.setText("" + leaderboardItem.getTotalFollowersCount());
        lbmuCommentsCount.setText("" + leaderboardItem.getCommentsCount());

//<<<<<<< HEAD
//        //winRateGauge.setText(digitsWinRatio + "%");
//        //winRateGauge.setTargetValue((float) leaderboardItem.getWinRatio() * 100);
//
//        if (tradeCountTv != null)
//        {
//            tradeCountTv.setEndValue(leaderboardItem.avgNumberOfTradesPerMonth.floatValue());
//            tradeCountTv.setFractionDigits(2);
//        }
//        if (daysHoldTv != null)
//        {
//            daysHoldTv.setEndValue(leaderboardItem.avgHoldingPeriodMins * 1.0f / (60 * 24));
//            daysHoldTv.setFractionDigits(2);
//        }
//        if (positionsCountTv != null)
//        {
//            positionsCountTv.setEndValue(leaderboardItem.numberOfPositionsInPeriod);
//            positionsCountTv.setFractionDigits(0);
//        }
//
//        showValueWithoutAnimation();
//    }
//
//    private void showValueWithoutAnimation()
//    {
//        String digitsWinRatio =
//                NumberDisplayUtils.formatWithRelevantDigits(leaderboardItem.getWinRatio() * 100, 3);
//        if (winRateGauge != null)
//        {
//            winRateGauge.setContentText(digitsWinRatio + "%");
//            winRateGauge.setSubText(getContext().getString(R.string.leaderboard_win_ratio_title));
//            winRateGauge.setAnimiationFlag(false);
//            winRateGauge.setCurrentValue((float) leaderboardItem.getWinRatio() * 100);
//        }
//
//        if (performanceGauge != null)
//        {
//            performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
//            performanceGauge.setSubText(
//                    getContext().getString(R.string.leaderboard_performance_title));
//            performanceGauge.setAnimiationFlag(false);
//            performanceGauge.setDrawStartValue(50f);
//            performanceGauge.setCurrentValue((float) leaderboardItem.normalizePerformance());
//        }
//
//        if (tradeConsistencyGauge != null)
//        {
//            tradeConsistencyGauge.setSubText(
//                    getContext().getString(R.string.leaderboard_consistency_title));
//            tradeConsistencyGauge.setAnimiationFlag(false);
//            tradeConsistencyGauge.setCurrentValue((float) normalizeConsistency());
//        }
//        Timber.d("showValueWithoutAnimation normalizeConsistency %s", normalizeConsistency());
//
//        if (tradeCountTv != null)
//        {
//            tradeCountTv.showText();
//        }
//        if (daysHoldTv != null)
//        {
//            daysHoldTv.showText();
//        }
//        if (positionsCountTv != null)
//        {
//            positionsCountTv.showText();
//        }
//    }
//
//    private void showExpandAnimation()
//    {
//        String digitsWinRatio =
//                NumberDisplayUtils.formatWithRelevantDigits(leaderboardItem.getWinRatio() * 100, 3);
//        if (winRateGauge != null)
//        {
//            winRateGauge.setContentText(digitsWinRatio + "%");
//            winRateGauge.setSubText(getContext().getString(R.string.leaderboard_win_ratio_title));
//            winRateGauge.setAnimiationFlag(true);
//            winRateGauge.setTargetValue((float) leaderboardItem.getWinRatio() * 100);
//        }
//
//        if (performanceGauge != null)
//        {
//            performanceGauge.setTopText(getContext().getString(R.string.leaderboard_SP_500));
//            performanceGauge.setSubText(
//                    getContext().getString(R.string.leaderboard_performance_title));
//            performanceGauge.setAnimiationFlag(true);
//            performanceGauge.setDrawStartValue(50f);
//            performanceGauge.setTargetValue((float) leaderboardItem.normalizePerformance());
//        }
//
//        if (tradeConsistencyGauge != null)
//        {
//            tradeConsistencyGauge.setSubText(
//                    getContext().getString(R.string.leaderboard_consistency_title));
//            tradeConsistencyGauge.setAnimiationFlag(true);
//            tradeConsistencyGauge.setTargetValue((float) normalizeConsistency());
//        }
//
//        if (tradeCountTv != null)
//        {
//            tradeCountTv.startAnimation();
//        }
//        if (daysHoldTv != null)
//        {
//            daysHoldTv.startAnimation();
//        }
//        if (positionsCountTv != null)
//        {
//            positionsCountTv.startAnimation();
//        }
//    }
//
//    private void clearExpandAnimation()
//    {
//        if (winRateGauge != null)
//        {
//            winRateGauge.clear();
//        }
//        if (performanceGauge != null)
//        {
//            performanceGauge.clear();
//        }
//        if (tradeConsistencyGauge != null)
//        {
//            tradeConsistencyGauge.clear();
//        }
//=======
//>>>>>>> origin/develop2.0
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
            THSignedNumber formattedNumber =
                    new THSignedNumber(THSignedNumber.TYPE_MONEY, leaderboardItem.PLinPeriodRefCcy,
                            THSignedNumber.WITHOUT_SIGN, getLbmuPlCurrencyDisplay());
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
            boolean enableButton = isFollowing != null && !isFollowing;
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
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_Profile);
                handleOpenProfileButtonClicked();
                break;

            case R.id.leaderboard_user_item_open_positions_list:
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_Positions);
                handleOpenPositionListClicked();
                break;

            case R.id.leaderboard_user_item_follow:
                localyticsSession.tagEvent(LocalyticsConstants.Leaderboard_Follow);
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
        int userId = leaderboardItem.id;

        // portfolio, to display position list
        int portfolioId = leaderboardItem.portfolioId;
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(userId, portfolioId);

        Bundle bundle = new Bundle();
        // to display time of value on start investment
        SimpleDateFormat sdf =
                new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        bundle.putString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING,
                formattedStartPeriodUtc);

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        LeaderboardDefDTO leaderboardDef = leaderboardDefCache.get()
                .get(new LeaderboardDefKey(leaderboardItem.getLeaderboardId()));
        boolean isTimeRestrictedLeaderboard =
                leaderboardDef != null && leaderboardDef.isTimeRestrictedLeaderboard();
        bundle.putBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED,
                isTimeRestrictedLeaderboard);

        if (leaderboardItem.lbmuId != -1)
        {
            pushLeaderboardPositionListFragment(bundle);
        }
        else
        {
            pushPositionListFragment(bundle, ownedPortfolioId);
        }
    }

    protected void pushLeaderboardPositionListFragment(Bundle bundle)
    {
        // leaderboard mark user id, to get marking user information
        LeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, leaderboardItem.getLeaderboardMarkUserId());
        LeaderboardPositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
        getNavigator().pushFragment(LeaderboardPositionListFragment.class, bundle);
    }

    protected void pushPositionListFragment(Bundle bundle, OwnedPortfolioId ownedPortfolioId)
    {
        PositionListFragment.putGetPositionsDTOKey(bundle, ownedPortfolioId);
        PositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
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

    //<<<<<<< HEAD
//
//    private Double getAvgConsistency()
//    {
//        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
//        if (userProfileDTO != null)
//        {
//            return userProfileDTO.mostSkilledLbmu.getAvgConsistency();
//        }
//        return LeaderboardUserDTO.MIN_CONSISTENCY;
//    }
//
//    private double normalizeConsistency()
//    {
//        try
//        {
//            Double minConsistency = LeaderboardUserDTO.MIN_CONSISTENCY;
//            Double maxConsistency = getAvgConsistency();
//            Double minConsistency = leaderboardItem.getConsistency();
//            minConsistency = (minConsistency < minConsistency) ? minConsistency : minConsistency;
//            minConsistency = (minConsistency > maxConsistency) ? maxConsistency : minConsistency;
//
//            double result =
//                    100 * (minConsistency - minConsistency) / (maxConsistency - minConsistency);
//            return result;
//        }
//        catch (Exception e)
//        {
//            Timber.e("normalizeConsistency", e);
//        }
//        return getAvgConsistency();
//    }
//=======
//>>>>>>> origin/develop2.0
}
