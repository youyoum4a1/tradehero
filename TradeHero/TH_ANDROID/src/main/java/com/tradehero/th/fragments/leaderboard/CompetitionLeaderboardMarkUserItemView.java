package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.PrizeDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import java.text.SimpleDateFormat;

public class CompetitionLeaderboardMarkUserItemView extends LeaderboardMarkUserItemView
{
    @InjectView(R.id.leaderboard_prize_amount) View prizeIndicator;
    @InjectView(R.id.lbmu_pl) TextView lbmuPl;
    @InjectView(R.id.lbmu_comments_count) TextView lbmuCommentsCount;
    @InjectView(R.id.lbmu_benchmark_roi) TextView lbmuBenchmarkRoi;
    @InjectView(R.id.lbmu_positions_count) TextView lbmuPositionsCount;
    @InjectView(R.id.lbmu_avg_days_held) TextView lbmuAvgDaysHeld;
    @InjectView(R.id.lbmu_followers_count) TextView lbmuFollowersCount;
    @InjectView(R.id.lbmu_win_ratio) TextView lbmuWinRatio;
    @InjectView(R.id.lbmu_number_of_trades) TextView lbmuNumberOfTrades;
    @InjectView(R.id.lbmu_period) TextView lbmuPeriod;
    @InjectView(R.id.lbmu_number_trades_in_period) @Optional @Nullable TextView lbmuNumberTradesInPeriod;

    @Nullable protected ProviderDTO providerDTO;
    protected int prizeDTOSize;

    //<editor-fold desc="Constructors">
    public CompetitionLeaderboardMarkUserItemView(Context context)
    {
        super(context);
    }

    public CompetitionLeaderboardMarkUserItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CompetitionLeaderboardMarkUserItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public void setProviderDTO(@NonNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayLbmuPl();
        if (countryLogo != null)
        {
            countryLogo.setVisibility(View.GONE);
        }
    }

    public void setPrizeDTOSize(int i)
    {
        this.prizeDTOSize = i;
        displayPrize();
    }

    @Override protected void displayExpandableSection()
    {
        super.displayExpandableSection();

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

    protected void displayPrize()
    {
        if (prizeDTOSize != 0 && this.getCurrentRank() != null && this.getCurrentRank() <= prizeDTOSize)
        {
            prizeIndicator.setVisibility(View.VISIBLE);
        }
        else
        {
            prizeIndicator.setVisibility(View.GONE);
        }
    }

    @Override protected String getLbmuPlCurrencyDisplay()
    {
        if (leaderboardItem != null && leaderboardItem.hasValidCurrencyDisplay())
        {
            return leaderboardItem.getNiceCurrency();
        }
        else if (providerDTO != null && providerDTO.hasValidCurrencyDisplay())
        {
            return providerDTO.getNiceCurrency();
        }
        return super.getLbmuPlCurrencyDisplay();
    }

    @Override protected void pushLeaderboardPositionListFragment(GetPositionsDTOKey getPositionsDTOKey, LeaderboardDefDTO leaderboardDefDTO)
    {
        if (leaderboardItem == null || providerDTO == null)
        {
            THToast.show(R.string.error_incomplete_info_message);
            return;
        }
        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();

        CompetitionLeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, leaderboardItem.getLeaderboardMarkUserId());
        CompetitionLeaderboardPositionListFragment.putShownUser(bundle, leaderboardItem.getBaseKey());
        CompetitionLeaderboardPositionListFragment.putProviderId(bundle, providerDTO.getProviderId());

        if (applicablePortfolioId != null)
        {
            CompetitionLeaderboardPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        navigator.pushFragment(CompetitionLeaderboardPositionListFragment.class, bundle);
    }
}
