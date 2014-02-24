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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO currentUserProfileDTO;

    // data
    private LeaderboardUserDTO leaderboardItem;
    protected WeakReference<THIABUserInteractor> parentUserInteractor = new WeakReference<>(null);
    protected THIABUserInteractor ownUserInteractor;

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
    @InjectView(R.id.leaderboard_user_item_follow) View lbmuFollowUser;
    @InjectView(R.id.leaderboard_user_item_following) View lbmuFollowingUser;

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
        TextView lbmuOpenPositionsList = (TextView) findViewById(R.id.leaderboard_user_item_open_positions_list);
        if (lbmuOpenPositionsList != null)
        {
            lbmuOpenPositionsList.setOnClickListener(this);
        }

        lbmuFollowUser.setEnabled(false);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
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
        ownUserInteractor = null;

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

    /**
     * The userInteractor should be strongly referenced elsewhere
     * @param userInteractor
     * @param andDisplay
     */
    public void linkWith(THIABUserInteractor userInteractor, boolean andDisplay)
    {
        this.parentUserInteractor = new WeakReference<>(userInteractor);
        if (andDisplay)
        {
            displayFollow();
        }
    }

    public Boolean isCurrentUserFollowing()
    {
        THIABUserInteractor userInteractorCopy = parentUserInteractor.get();
        if (currentUserProfileDTO == null || leaderboardItem == null ||
                userInteractorCopy == null ||
                userInteractorCopy.getApplicablePortfolioId() == null)
        {
            return null;
        }
        return currentUserProfileDTO.isFollowingUser(leaderboardItem.getBaseKey());
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
        lbmuDisplayName.setText(leaderboardItem.displayName);
        lbmuHeroQuotient.setText(leaderboardItem.getHeroQuotientFormatted());
        if (lbmuFoF != null)
        {
            lbmuFoF.setVisibility(leaderboardItem.isIncludeFoF() && !StringUtils.isNullOrEmptyOrSpaces(leaderboardItem.friendOf_markupString) ? VISIBLE : GONE);
            lbmuFoF.setText(leaderboardItem.friendOf_markupString);
        }

        if (leaderboardItem.picture != null)
        {
            picasso.get()
                    .load(leaderboardItem.picture)
                    .transform(peopleIconTransformation)
                    .into(lbmuProfilePicture,
                            new Callback()
                            {
                                @Override public void onSuccess()
                                {
                                }

                                @Override public void onError()
                                {
                                    loadDefaultUserImage();
                                }
                            });
        }
        else
        {
            loadDefaultUserImage();
        }
    }

    private void loadDefaultUserImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(lbmuProfilePicture);
    }

    private void displayExpandableSection()
    {
        // display P&L

        THSignedNumber formattedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, leaderboardItem.PLinPeriodRefCcy, false);
        lbmuPl.setText(formattedNumber.toString());
        String periodFormat = getContext().getString(R.string.leaderboard_ranking_period);

        // display period
        SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        String formattedEndPeriodUtc = sdf.format(leaderboardItem.periodEndUtc);
        String period = String.format(periodFormat, formattedStartPeriodUtc, formattedEndPeriodUtc);
        lbmuPeriod.setText(period);

        // display Roi
        THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, leaderboardItem.roiInPeriod * 100);
        lbmuRoi.setText(roi.toString());
        lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

        // display Roi annualized
        THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, leaderboardItem.roiAnnualizedInPeriod * 100);
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
        lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));

        // benchmark roi
        THSignedNumber benchmarkRoiInPeriodVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, leaderboardItem.getBenchmarkRoiInPeriod() * 100);
        String benchmarkRoiInPeriodFormat = getContext().getString(R.string.leaderboard_benchmark_roi_format);
        String benchmarkRoiInPeriod = String.format(benchmarkRoiInPeriodFormat, benchmarkRoiInPeriodVal.toString());
        lbmuBenchmarkRoi.setText(Html.fromHtml(benchmarkRoiInPeriod));

        // sharpe ratio
        if (leaderboardItem.sharpeRatioInPeriod_vsSP500 != null)
        {
            lbmuSharpeRatio.setText(new THSignedNumber(THSignedNumber.TYPE_MONEY, leaderboardItem.sharpeRatioInPeriod_vsSP500, false).toString());
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
                leaderboardItem.getNumberOfTrades() > 1 ? R.string.leaderboard_number_of_trades_plural : R.string.leaderboard_number_of_trade);
        String numberOfTrades = String.format(numberOfTradeFormat, leaderboardItem.getNumberOfTrades());
        lbmuNumberOfTrades.setText(Html.fromHtml(numberOfTrades));

        // Number of trades in Period
        lbmuNumberTradesInPeriod.setText(String.format("%,d", leaderboardItem.numberOfTradesInPeriod));

        // average days held
        lbmuAvgDaysHeld.setText(NumberDisplayUtils.formatWithRelevantDigits((double) leaderboardItem.avgHoldingPeriodMins / (60*24), 3));
        String winRatioFormat = getContext().getString(R.string.leaderboard_win_ratio);
        String digitsWinRatio = NumberDisplayUtils.formatWithRelevantDigits(leaderboardItem.getWinRatio() * 100, 3);
        String winRatio = String.format(winRatioFormat, digitsWinRatio);
        lbmuWinRatio.setText(digitsWinRatio + "%");

        // followers & comments count
        lbmuFollowersCount.setText("" + leaderboardItem.getTotalFollowersCount());
        lbmuCommentsCount.setText("" + leaderboardItem.getCommentsCount());
    }

    private void displayFollow()
    {
        if (lbmuFollowUser != null)
        {
            Boolean isFollowing = isCurrentUserFollowing();
            boolean showButton = isFollowing == null || !isFollowing;
            lbmuFollowUser.setVisibility(showButton ? VISIBLE : GONE);
            boolean enableButton = isFollowing != null && !isFollowing;
            lbmuFollowUser.setEnabled(enableButton);
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
                handleOpenProfileButtonClicked();
                break;

            case R.id.leaderboard_user_item_open_positions_list:
                handleOpenPositionListClicked();
                break;

            case R.id.leaderboard_user_item_follow:
                openFollowUserDialog();
                break;
        }
    }

    private void openFollowUserDialog()
    {
        THIABUserInteractor parentCopy = parentUserInteractor.get();
        if (ownUserInteractor == null && parentCopy != null)
        {
            ownUserInteractor = new LeaderboardMarkUserItemViewTHIABUserInteractor();
            ownUserInteractor.setApplicablePortfolioId(parentCopy.getApplicablePortfolioId());
        }
        THIABUserInteractor interactor = ownUserInteractor;
        if (interactor != null)
        {
            interactor.followHero(leaderboardItem.getBaseKey());
        }
    }

    private void handleOpenPositionListClicked()
    {
        int userId = leaderboardItem.id;

        // portfolio, to display position list
        int portfolioId = leaderboardItem.portfolioId;
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(userId, portfolioId);

        Bundle bundle = new Bundle();
        // to display time of value on start investment
        SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
        bundle.putString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING, formattedStartPeriodUtc);

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        LeaderboardDefDTO leaderboardDef = leaderboardDefCache.get().get(new LeaderboardDefKey(leaderboardItem.getLeaderboardId()));
        boolean isTimeRestrictedLeaderboard = leaderboardDef != null && leaderboardDef.isTimeRestrictedLeaderboard();
        bundle.putBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED, isTimeRestrictedLeaderboard);

        if (leaderboardItem.lbmuId != -1)
        {
            // leaderboard mark user id, to get marking user information
            bundle.putBundle(LeaderboardPositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
            bundle.putLong(LeaderboardMarkUserId.BUNDLE_KEY, leaderboardItem.lbmuId);
            getNavigator().pushFragment(LeaderboardPositionListFragment.class, bundle);
        }
        else
        {
            bundle.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
            getNavigator().pushFragment(PositionListFragment.class, bundle);
        }
    }

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }

    private void handleOpenProfileButtonClicked()
    {
        int userId = leaderboardItem.id;

        if (currentUserId != null && currentUserId.get() != userId)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userId);
            getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    public class LeaderboardMarkUserItemViewTHIABUserInteractor extends THIABUserInteractor
    {
        public LeaderboardMarkUserItemViewTHIABUserInteractor()
        {
            super();
        }

        @Override protected void createFollowCallback()
        {
            followCallback = new LeaderboardMarkUserItemViewUserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get());
        }

        protected class LeaderboardMarkUserItemViewUserInteractorFollowHeroCallback extends UserInteractorFollowHeroCallback
        {
            public LeaderboardMarkUserItemViewUserInteractorFollowHeroCallback(HeroListCache heroListCache, UserProfileCache userProfileCache)
            {
                super(heroListCache, userProfileCache);
            }

            @Override public void success(UserProfileDTO userProfileDTO, Response response)
            {
                super.success(userProfileDTO, response);
                LeaderboardMarkUserItemView.this.linkWith(userProfileDTO, true);
            }
        }
    }
}
