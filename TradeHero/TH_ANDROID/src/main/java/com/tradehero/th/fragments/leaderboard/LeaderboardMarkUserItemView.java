package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.client.Response;
import timber.log.Timber;

public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>,
        ExpandingLayout.OnExpandListener
{
    public static final int MAX_OWN_RANKING = 1000;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject Lazy<LeaderboardCache> leaderboardCache;
    @Inject Lazy<Picasso> picasso;
    @Inject THRouter thRouter;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    protected UserProfileDTO currentUserProfileDTO;
    protected OnFollowRequestedListener followRequestedListener;
    protected OwnedPortfolioId applicablePortfolioId;
    // data
    protected LeaderboardUserDTO leaderboardItem;

    // top view
    @InjectView(R.id.leaderboard_user_item_display_name) protected TextView lbmuDisplayName;
    @InjectView(R.id.lbmu_roi) protected TextView lbmuRoi;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView lbmuProfilePicture;
    @InjectView(R.id.leaderboard_user_item_position) TextView lbmuPosition;
    @InjectView(R.id.leaderboard_user_item_info) ImageView lbmuPositionInfo;

    // expanding view
    @InjectView(R.id.lbmu_pl) TextView lbmuPl;
    @InjectView(R.id.leaderboard_user_item_hq) TextView lbmuHeroQuotient;
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

    @InjectView(R.id.lbmu_inner_view_container) @Optional @Nullable ViewGroup innerViewContainer;

    private @Nullable DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardOwnUserRankingListener;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserItemView(Context context)
    {
        super(context);
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
        leaderboardOwnUserRankingListener = createLeaderboardUserRankingListener();
    }

    private void initViews()
    {
        ButterKnife.inject(this);
        // top part
        if (lbmuFoF != null)
        {
            DaggerUtils.inject(lbmuFoF);
        }

        lbmuProfilePicture.setLayerType(LAYER_TYPE_SOFTWARE, null);
        expandingLayout.setOnExpandListener(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        initViews();
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(LinkMovementMethod.getInstance());
        }
        if (leaderboardOwnUserRankingListener == null)
        {
            leaderboardOwnUserRankingListener = createLeaderboardUserRankingListener();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (lbmuFoF != null)
        {
            lbmuFoF.setMovementMethod(null);
        }
        loadDefaultUserImage();

        if (lbmuProfilePicture != null)
        {
            lbmuProfilePicture.setImageDrawable(null);
        }
        detachOwnRankingLeaderboardCache();
        leaderboardOwnUserRankingListener = null;

        ButterKnife.reset(this);
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

    @Override public void display(LeaderboardUserDTO leaderboardUserDTO)
    {
        linkWith(leaderboardUserDTO, true);
    }

    private void detachOwnRankingLeaderboardCache()
    {
        leaderboardCache.get().unregister(leaderboardOwnUserRankingListener);
    }

    private void linkWith(LeaderboardUserDTO leaderboardUserDTO, boolean andDisplay)
    {
        this.leaderboardItem = leaderboardUserDTO;

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
        displayRankingPosition();
        if (leaderboardItem.getPosition() != null)
        {
            lbmuPosition.setText("" + (leaderboardItem.getPosition() + 1));
        }

        lbmuHeroQuotient.setText(leaderboardItem.getHeroQuotientFormatted());

        if (lbmuFoF != null)
        {
            lbmuFoF.setVisibility(
                    leaderboardItem.isIncludeFoF() != null && leaderboardItem.isIncludeFoF() &&
                            !StringUtils.isNullOrEmptyOrSpaces(
                                    leaderboardItem.friendOfMarkupString) ? VISIBLE : GONE);
            lbmuFoF.setText(leaderboardItem.friendOfMarkupString);
        }

        linkWith(leaderboardItem);
    }

    public void linkWith(@NotNull UserBaseDTO userBaseDTO)
    {
        displayRankingColor(userBaseDTO);

        lbmuDisplayName.setText(userBaseDTO.getDisplayName());

        loadDefaultUserImage();
        if (userBaseDTO.picture != null)
        {
            picasso.get()
                    .load(userBaseDTO.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(lbmuProfilePicture.getDrawable())
                    .into(lbmuProfilePicture);
        }

        displayCountryLogo(userBaseDTO);
    }

    private void displayRankingColor(@NotNull UserBaseDTO userBaseDTO)
    {
        if (currentUserId.get() == userBaseDTO.id)
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

    private void loadDefaultUserImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(lbmuProfilePicture);
    }

    private void displayCountryLogo(UserBaseDTO userBaseDTO)
    {
        if (countryLogo != null)
        {
            try
            {
                int imageResId = Country.getCountryLogo(R.drawable.default_image, userBaseDTO.countryCode);
                countryLogo.setImageResource(imageResId);
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, null);
            }
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
            // you can't follow yourself
            if (currentUserId.get() == leaderboardItem.id)
            {
                lbmuFollowUser.setVisibility(GONE);
                return;
            }

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

    @OnClick(R.id.leaderboard_user_item_info)
    protected void handleUserInfoClicked()
    {
        // Nothing?
    }

    @OnClick(R.id.leaderboard_user_item_open_profile)
    protected void handleProfileClicked()
    {
        handleOpenProfileButtonClicked();
    }

    @OnClick(R.id.leaderboard_user_item_open_positions_list)
    protected void handlePositionButtonClicked()
    {
        handleOpenPositionListClicked();
    }

    @OnClick(R.id.leaderboard_user_item_follow)
    protected void handleFollowButtonClicked()
    {
        follow(leaderboardItem);
    }

    @OnClick(R.id.leaderboard_user_item_profile_picture)
    protected void handleUserIconClicked()
    {
        handleOpenProfileButtonClicked();
    }

    protected void follow(@NotNull UserBaseDTO userBaseDTO)
    {
        notifyFollowRequested(userBaseDTO);
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

    protected void handleOpenProfileButtonClicked()
    {
        if (leaderboardItem == null)
        {
            // TODO nicer
            return;
        }
        int userId = leaderboardItem.id;

        openTimeline(userId);
    }

    protected void openTimeline(int userId)
    {
        Bundle bundle = new Bundle();
        UserBaseKey userToSee = new UserBaseKey(userId);
        thRouter.save(bundle, userToSee);
        if (currentUserId.toUserBaseKey().equals(userToSee))
        {
            getNavigator().pushFragment(MeTimelineFragment.class, bundle);
        }
        else
        {
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected void handleSuccess(UserProfileDTO userProfileDTO, Response response)
    {
        linkWith(userProfileDTO, true);
    }

    protected void notifyFollowRequested(@NotNull UserBaseDTO userBaseDTO)
    {
        OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }

    public void displayRankingPosition()
    {
        @Nullable Integer currentRank = getCurrentRank();
        if (currentRank == null)
        {
            // TODO decide
            return;
        }
        else if (currentRank <= MAX_OWN_RANKING)
        {
            lbmuPosition.setText("" + currentRank);
        }
        else
        {
            lbmuPosition.setText(R.string.leaderboard_max_ranked_position);
        }

        //Add touch feedback
        if (innerViewContainer != null)
        {
            innerViewContainer.setBackgroundResource(R.drawable.basic_white_selector);
        }
    }

    @Nullable protected Integer getCurrentRank()
    {
        return leaderboardItem == null ? null : (leaderboardItem.ordinalPosition + 1);
    }

    protected void displayUserIsNotRanked()
    {
        // disable touch feedback so we don't confuse the user
        if (innerViewContainer != null)
        {
            innerViewContainer.setBackgroundResource(R.color.white);
        }

        lbmuRoi.setText(R.string.leaderboard_not_ranked);
        lbmuPosition.setText("-");
    }

    public static interface OnFollowRequestedListener
    {
        void onFollowRequested(UserBaseDTO userBaseKey);
    }

    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> createLeaderboardUserRankingListener()
    {
        return new LeaderboardUserRankingCacheListener();
    }

    protected class LeaderboardUserRankingCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO leaderboardDTO)
        {
            if (leaderboardDTO.users != null && !leaderboardDTO.users.isEmpty())
            {
                LeaderboardUserDTO ownLeaderboardUserDTO = leaderboardDTO.users.get(0);
                display(ownLeaderboardUserDTO);
            }
            else
            {
                displayUserIsNotRanked();
            }
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
