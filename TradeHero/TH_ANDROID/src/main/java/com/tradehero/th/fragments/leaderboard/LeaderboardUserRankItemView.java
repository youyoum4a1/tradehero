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
import com.tradehero.th.api.leaderboard.LeaderboardUserRankDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.THSignedNumber;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import javax.inject.Named;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:14 PM Copyright (c) TradeHero */
public class LeaderboardUserRankItemView extends RelativeLayout
        implements DTOView<LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper>,View.OnClickListener
{
    @Inject protected Picasso picasso;
    @Inject protected Lazy<PositionCache> positionCache;
    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;

    // data
    private LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper leaderboardItem;
    private PositionDTO position;

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
    private TextView lbmuGotoProfile;
    private Navigator navigator;
    private TextView lbmuRoiAnnualized;
    private TextView lbmuWinRatio;
    private TextView lbmuVolatility;
    private TextView lbmuNumberOfTrades;
    private TextView lbmuPeriod;

    //<editor-fold desc="Constructors">
    public LeaderboardUserRankItemView(Context context)
    {
        super(context);
    }

    public LeaderboardUserRankItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardUserRankItemView(Context context, AttributeSet attrs, int defStyle)
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
        // action buttons
        lbmuGotoProfile = (TextView) findViewById(R.id.leaderboard_user_item_goto_profile);
        if (lbmuGotoProfile != null)
        {
            lbmuGotoProfile.setOnClickListener(this);
        }
        lbmuWinRatio = (TextView) findViewById(R.id.lbmu_win_ratio);
        lbmuNumberOfTrades = (TextView) findViewById(R.id.lbmu_number_of_trades);
    }

    @Override public void display(LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem)
    {
        linkWith(expandableItem, true);
    }

    private void linkWith(LeaderboardListAdapter.ExpandableLeaderboardUserRankItemWrapper expandableItem, boolean andDisplay)
    {
        this.leaderboardItem = expandableItem;
        if (leaderboardItem != null)
        {
            //this.position = positionCache.get().get(new OwnedPositionId());
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
        LeaderboardUserRankDTO dto = leaderboardItem.getModel();

        lbmuPosition.setText("" + (leaderboardItem.getPosition() + 1));
        lbmuDisplayName.setText(dto.displayName);
        lbmuHeroQuotient.setText(dto.getHeroQuotientFormatted());

        if (dto.picture != null)
        {
            picasso.load(dto.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(lbmuProfilePicture);
        }
    }

    private void displayExpandableSection()
    {
        LeaderboardUserRankDTO dto = leaderboardItem.getModel();

        lbmuPl.setText(dto.getFormattedPL() + " " + getContext().getString(R.string.ref_currency));
        String periodFormat = getContext().getString(R.string.leaderboard_ranking_period);

        SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(R.string.leaderboard_datetime_format));
        String formattedStartPeriodUtc = sdf.format(dto.periodStartUtc);
        String formattedEndPeriodUtc = sdf.format(dto.periodEndUtc);
        String period = String.format(periodFormat, formattedStartPeriodUtc, formattedEndPeriodUtc);
        lbmuPeriod.setText(period);

        THSignedNumber roi = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.roiInPeriod * 100);
        lbmuRoi.setText(roi.toString());
        lbmuRoi.setTextColor(getResources().getColor(roi.getColor()));

        THSignedNumber roiAnnualizedVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.roiAnnualizedInPeriod * 100);
        String roiAnnualizedFormat = getContext().getString(R.string.leaderboard_roi_annualized);
        String roiAnnualized = String.format(roiAnnualizedFormat, roiAnnualizedVal.toString());
        lbmuRoiAnnualized.setText(Html.fromHtml(roiAnnualized));

        THSignedNumber benchmarkRoiInPeriodVal = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.benchmarkRoiInPeriod * 100);
        String benchmarkRoiInPeriodFormat = getContext().getString(R.string.leaderboard_benchmark_roi_format);
        String benchmarkRoiInPeriod = String.format(benchmarkRoiInPeriodFormat, benchmarkRoiInPeriodVal.toString());
        lbmuBenchmarkRoi.setText(Html.fromHtml(benchmarkRoiInPeriod));

        lbmuSharpeRatio.setText(dto.getFormattedSharpeRatio());

        String volatilityFormat = getContext().getString(R.string.leaderboard_volatility);
        String volatility = String.format(volatilityFormat, dto.getVolatility());
        lbmuVolatility.setText(Html.fromHtml(volatility));

        lbmuPositionsCount.setText("" + dto.numberOfPositionsInPeriod);

        String numberOfTradeFormat = getContext().getString(
                dto.getNumberOfTrades() > 1 ? R.string.leaderboard_number_of_trades_plural : R.string.leaderboard_number_of_trade);
        String numberOfTrades = String.format(numberOfTradeFormat, dto.getNumberOfTrades());
        lbmuNumberOfTrades.setText(Html.fromHtml(numberOfTrades));


        lbmuAvgDaysHeld.setText(NumberDisplayUtils.formatWithRelevantDigits((double) dto.avgHoldingPeriodMins / (60*24), 3));
        String winRatioFormat = getContext().getString(R.string.leaderboard_win_ratio);
        String winRatio = String.format(winRatioFormat, NumberDisplayUtils.formatWithRelevantDigits(dto.getWinRatio() * 100, 3));
        lbmuWinRatio.setText(winRatio);

        lbmuFollowersCount.setText("" + dto.getTotalFollowersCount());
        lbmuCommentsCount.setText("" + dto.getCommentsCount());
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.leaderboard_user_item_info:
                break;
            case R.id.leaderboard_user_item_goto_profile:
                int userId = leaderboardItem.getModel().getId();
                Bundle b = new Bundle();
                b.putInt(UserBaseKey.BUNDLE_KEY_KEY, userId);

                if (currentUserBase != null && currentUserBase.id != userId)
                {
                    navigator.pushFragment(TimelineFragment.class, b, true);
                }
                break;
        }
    }
}
