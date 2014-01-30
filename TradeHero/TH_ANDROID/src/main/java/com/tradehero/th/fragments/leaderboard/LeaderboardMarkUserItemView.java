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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.models.graphics.TransformationUsage;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardUserDTO>, View.OnClickListener
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject @Named(TransformationUsage.USER_PHOTO) protected Transformation peopleIconTransformation;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    // data
    private LeaderboardUserDTO leaderboardItem;

    // top view
    private TextView lbmuDisplayName;
    private ImageView lbmuProfilePicture;
    private TextView lbmuPosition;
    private TextView lbmuHeroQuotient;
    private ImageView lbmuPositionInfo;

    // expanding view
    private TextView lbmuPl;
    private TextView lbmuRoi;
    private TextView lbmuCommentsCount;
    private TextView lbmuBenchmarkRoi;
    private TextView lbmuSharpeRatio;
    private TextView lbmuPositionsCount;
    private TextView lbmuAvgDaysHeld;
    private TextView lbmuFollowersCount;
    private TextView lbmuRoiAnnualized;
    private TextView lbmuWinRatio;
    private TextView lbmuVolatility;
    private TextView lbmuNumberOfTrades;
    private TextView lbmuPeriod;
    private MarkdownTextView lbmuFoF;

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
        // top part
        lbmuPosition = (TextView) findViewById(R.id.leaderboard_user_item_position);
        lbmuDisplayName = (TextView) findViewById(R.id.leaderboard_user_item_display_name);
        lbmuProfilePicture = (ImageView) findViewById(R.id.leaderboard_user_item_profile_picture);
        lbmuHeroQuotient = (TextView) findViewById(R.id.leaderboard_user_item_hq);
        lbmuFoF = (MarkdownTextView) findViewById(R.id.leaderboard_user_item_fof);
        if (lbmuFoF != null)
        {
            DaggerUtils.inject(lbmuFoF);
        }
        lbmuPositionInfo = (ImageView) findViewById(R.id.leaderboard_user_item_info);

        // expanding part
        lbmuPl = (TextView) findViewById(R.id.lbmu_pl);
        lbmuPeriod = (TextView) findViewById(R.id.lbmu_period);
        lbmuRoi = (TextView) findViewById(R.id.lbmu_roi);
        lbmuRoiAnnualized = (TextView) findViewById(R.id.lbmu_roi_annualized);
        lbmuBenchmarkRoi = (TextView) findViewById(R.id.lbmu_benchmark_roi);
        lbmuSharpeRatio = (TextView) findViewById(R.id.lbmu_sharpe_ratio);
        lbmuVolatility = (TextView) findViewById(R.id.lbmu_volatility);
        lbmuPositionsCount = (TextView) findViewById(R.id.lbmu_positions_count);
        lbmuAvgDaysHeld = (TextView) findViewById(R.id.lbmu_avg_days_held);
        lbmuFollowersCount = (TextView) findViewById(R.id.lbmu_followers_count);
        lbmuCommentsCount = (TextView) findViewById(R.id.lbmu_comments_count);
        lbmuWinRatio = (TextView) findViewById(R.id.lbmu_win_ratio);
        lbmuNumberOfTrades = (TextView) findViewById(R.id.lbmu_number_of_trades);

        // action buttons
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
        super.onDetachedFromWindow();
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
            picasso.get().load(leaderboardItem.picture)
                    .transform(peopleIconTransformation)
                    .into(lbmuProfilePicture);
        }
        else
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformation)
                    .into(lbmuProfilePicture);
        }
    }

    private void displayExpandableSection()
    {
        // display P&L
        lbmuPl.setText(leaderboardItem.getFormattedPL() + " " + getContext().getString(R.string.ref_currency));
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
        lbmuSharpeRatio.setText(leaderboardItem.getFormattedSharpeRatio());

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

        // average days held
        lbmuAvgDaysHeld.setText(NumberDisplayUtils.formatWithRelevantDigits((double) leaderboardItem.avgHoldingPeriodMins / (60*24), 3));
        String winRatioFormat = getContext().getString(R.string.leaderboard_win_ratio);
        String winRatio = String.format(winRatioFormat, NumberDisplayUtils.formatWithRelevantDigits(leaderboardItem.getWinRatio() * 100, 3));
        lbmuWinRatio.setText(winRatio);

        // followers & comments count
        lbmuFollowersCount.setText("" + leaderboardItem.getTotalFollowersCount());
        lbmuCommentsCount.setText("" + leaderboardItem.getCommentsCount());
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.leaderboard_user_item_info:
                break;
            case R.id.leaderboard_user_item_open_profile:
                handleOpenProfileButtonClicked();
                break;

            case R.id.leaderboard_user_item_open_positions_list:
                handleOpenPositionListClicked();
                break;
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

        if (currentUserBaseKeyHolder != null && currentUserBaseKeyHolder.getCurrentUserBaseKey().key != userId)
        {
            getNavigator().openTimeline(userId);
        }
    }
}
