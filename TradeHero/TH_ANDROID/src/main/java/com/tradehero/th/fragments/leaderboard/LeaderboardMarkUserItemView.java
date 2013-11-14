package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.THSignedNumber;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserItemView extends RelativeLayout
        implements DTOView<LeaderboardMarkUserListAdapter.ExpandableLeaderboardUserRankItemWrapper>,View.OnClickListener
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    // data
    private LeaderboardMarkUserListAdapter.ExpandableLeaderboardUserRankItemWrapper leaderboardItem;

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
    private Navigator navigator;
    private TextView lbmuRoiAnnualized;
    private TextView lbmuWinRatio;
    private TextView lbmuVolatility;
    private TextView lbmuNumberOfTrades;
    private TextView lbmuPeriod;

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
        navigator = ((NavigatorActivity) getContext()).getNavigator();

        // top part
        lbmuPosition = (TextView) findViewById(R.id.leaderboard_user_item_position);
        lbmuDisplayName = (TextView) findViewById(R.id.leaderboard_user_item_display_name);
        lbmuProfilePicture = (ImageView) findViewById(R.id.leaderboard_user_item_profile_picture);

        if (lbmuProfilePicture != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(lbmuProfilePicture);
        }

        lbmuHeroQuotient = (TextView) findViewById(R.id.leaderboard_user_item_hq);
        lbmuPositionInfo = (ImageView) findViewById(R.id.leaderboard_user_item_info);
        if (lbmuPositionInfo != null)
        {
            lbmuPositionInfo.setOnClickListener(this);
        }

        // for expanding part
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

    @Override public void display(LeaderboardMarkUserListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem)
    {
        linkWith(expandableItem, true);
    }

    private void linkWith(LeaderboardMarkUserListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem, boolean andDisplay)
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
        if (leaderboardItem == null || leaderboardItem.getModel() == null)
        {
            return;
        }

        displayTopSection();
        displayExpandableSection();
    }

    private void displayTopSection()
    {
        LeaderboardUserDTO dto = leaderboardItem.getModel();

        lbmuPosition.setText("" + (leaderboardItem.getPosition() + 1));
        lbmuDisplayName.setText(dto.displayName);
        lbmuHeroQuotient.setText(dto.getHeroQuotientFormatted());

        if (dto.picture != null)
        {
            picasso.get().load(dto.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(lbmuProfilePicture);
        }
        else
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(lbmuProfilePicture);
        }
    }

    private void displayExpandableSection()
    {
        LeaderboardUserDTO dto = leaderboardItem.getModel();

        // display P&L
        lbmuPl.setText(dto.getFormattedPL() + " " + getContext().getString(R.string.ref_currency));
        String periodFormat = getContext().getString(R.string.leaderboard_ranking_period);

        // display period
        SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(dto.periodStartUtc);
        String formattedEndPeriodUtc = sdf.format(dto.periodEndUtc);
        String period = String.format(periodFormat, formattedStartPeriodUtc, formattedEndPeriodUtc);
        lbmuPeriod.setText(period);

        // display Roi
        THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.roiInPeriod * 100);
        lbmuRoi.setText(roi.toString());
        lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

        // display Roi annualized
        THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.roiAnnualizedInPeriod * 100);
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
        lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));

        // benchmark roi
        THSignedNumber benchmarkRoiInPeriodVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.getBenchmarkRoiInPeriod() * 100);
        String benchmarkRoiInPeriodFormat = getContext().getString(R.string.leaderboard_benchmark_roi_format);
        String benchmarkRoiInPeriod = String.format(benchmarkRoiInPeriodFormat, benchmarkRoiInPeriodVal.toString());
        lbmuBenchmarkRoi.setText(Html.fromHtml(benchmarkRoiInPeriod));

        // sharpe ratio
        lbmuSharpeRatio.setText(dto.getFormattedSharpeRatio());

        // volatility
        String volatilityFormat = getContext().getString(R.string.leaderboard_volatility);
        String volatility = String.format(volatilityFormat, dto.getVolatility());
        lbmuVolatility.setText(Html.fromHtml(volatility));

        // number of positions holding
        lbmuPositionsCount.setText("" + dto.numberOfPositionsInPeriod);

        // number of trades
        String numberOfTradeFormat = getContext().getString(
                dto.getNumberOfTrades() > 1 ? R.string.leaderboard_number_of_trades_plural : R.string.leaderboard_number_of_trade);
        String numberOfTrades = String.format(numberOfTradeFormat, dto.getNumberOfTrades());
        lbmuNumberOfTrades.setText(Html.fromHtml(numberOfTrades));

        // average days held
        lbmuAvgDaysHeld.setText(NumberDisplayUtils.formatWithRelevantDigits((double) dto.avgHoldingPeriodMins / (60*24), 3));
        String winRatioFormat = getContext().getString(R.string.leaderboard_win_ratio);
        String winRatio = String.format(winRatioFormat, NumberDisplayUtils.formatWithRelevantDigits(dto.getWinRatio() * 100, 3));
        lbmuWinRatio.setText(winRatio);

        // followers & comments count
        lbmuFollowersCount.setText("" + dto.getTotalFollowersCount());
        lbmuCommentsCount.setText("" + dto.getCommentsCount());
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
        LeaderboardUserDTO model = leaderboardItem.getModel();

        int userId = model.getId();

        // portfolio, to display position list
        int portfolioId = leaderboardItem.getModel().portfolioId;
        OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(userId, portfolioId);

        // leaderboard mark user id, to get marking user information
        Bundle bundle = ownedPortfolioId.getArgs();
        bundle.putLong(LeaderboardMarkUserId.BUNDLE_KEY, model.lbmuId);

        // to display time of value on start investment
        SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(model.periodStartUtc);
        bundle.putString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING, formattedStartPeriodUtc);

        // get leaderboard definition from cache, supposedly it exists coz this view appears after leaderboard definition list
        LeaderboardDefDTO leaderboardDef = leaderboardDefCache.get().get(new LeaderboardDefKey(leaderboardItem.getLeaderboardId()));
        boolean isTimeRestrictedLeaderboard = leaderboardDef != null && leaderboardDef.isTimeRestrictedLeaderboard();
        bundle.putBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED, isTimeRestrictedLeaderboard);

        navigator.pushFragment(LeaderboardPositionListFragment.class, bundle, true);
    }

    private void handleOpenProfileButtonClicked()
    {
        int userId = leaderboardItem.getModel().getId();

        Bundle b = new Bundle();
        b.putInt(UserBaseKey.BUNDLE_KEY_KEY, userId);

        if (currentUserBaseKeyHolder != null && currentUserBaseKeyHolder.getCurrentUserBaseKey().key != userId)
        {
            navigator.pushFragment(TimelineFragment.class, b, true);
        }
    }
}
