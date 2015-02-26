package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
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

    @Override public void display(@NonNull LeaderboardMarkUserItemView.DTO parentViewDTO)
    {
        super.display(parentViewDTO);
        if (parentViewDTO instanceof DTO)
        {
            DTO viewDTO = (DTO) parentViewDTO;
            if (prizeIndicator != null)
            {
                prizeIndicator.setVisibility(viewDTO.prizeIndicatorVisibility);
            }
            if (lbmuPl != null)
            {
                lbmuPl.setText(viewDTO.lbmuPl);
            }
            if (lbmuCommentsCount != null)
            {
                lbmuCommentsCount.setText(viewDTO.lbmuCommentsCount);
            }
            if (lbmuBenchmarkRoi != null)
            {
                lbmuBenchmarkRoi.setText(viewDTO.lbmuBenchmarkRoi);
            }
            if (lbmuPositionsCount != null)
            {
                lbmuPositionsCount.setText(viewDTO.lbmuPositionsCount);
            }
            if (lbmuAvgDaysHeld != null)
            {
                lbmuAvgDaysHeld.setText(viewDTO.lbmuAvgDaysHeld);
            }
            if (lbmuFollowersCount != null)
            {
                lbmuFollowersCount.setText(viewDTO.lbmuFollowersCount);
            }
            if (lbmuWinRatio != null)
            {
                lbmuWinRatio.setText(viewDTO.lbmuWinRatio);
            }
            if (lbmuNumberOfTrades != null)
            {
                lbmuNumberOfTrades.setText(viewDTO.lbmuNumberOfTrades);
            }
            if (lbmuPeriod != null)
            {
                lbmuPeriod.setText(viewDTO.lbmuPeriod);
            }
            if (lbmuNumberTradesInPeriod != null)
            {
                lbmuNumberTradesInPeriod.setText(viewDTO.lbmuNumberTradesInPeriod);
            }
        }
    }

    @Override protected void pushLeaderboardPositionListFragment(GetPositionsDTOKey getPositionsDTOKey, LeaderboardDefDTO leaderboardDefDTO)
    {
        if (viewDTO == null)
        {
            THToast.show(R.string.error_incomplete_info_message);
            return;
        }
        // leaderboard mark user id, to get marking user information
        Bundle bundle = new Bundle();

        CompetitionLeaderboardPositionListFragment.putGetPositionsDTOKey(bundle, viewDTO.leaderboardUserDTO.getLeaderboardMarkUserId());
        CompetitionLeaderboardPositionListFragment.putShownUser(bundle, viewDTO.leaderboardUserDTO.getBaseKey());
        CompetitionLeaderboardPositionListFragment.putProviderId(bundle, ((DTO) viewDTO).providerDTO.getProviderId());

        if (applicablePortfolioId != null)
        {
            CompetitionLeaderboardPositionListFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
        }

        navigator.pushFragment(CompetitionLeaderboardPositionListFragment.class, bundle);
    }

    public static class DTO extends LeaderboardMarkUserItemView.DTO
    {
        final int prizeDTOSize;
        @NonNull final ProviderDTO providerDTO;
        @ViewVisibilityValue final int prizeIndicatorVisibility;
        @NonNull final Spanned lbmuPl;
        @NonNull final Spanned lbmuCommentsCount;
        @NonNull final Spanned lbmuBenchmarkRoi;
        @NonNull final String lbmuPositionsCount;
        @NonNull final Spanned lbmuAvgDaysHeld;
        @NonNull final Spanned lbmuFollowersCount;
        @NonNull final Spanned lbmuWinRatio;
        @NonNull final Spanned lbmuNumberOfTrades;
        @NonNull final String lbmuPeriod;
        @NonNull final Spanned lbmuNumberTradesInPeriod;

        public DTO(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull LeaderboardUserDTO leaderboardItem,
                @NonNull UserProfileDTO currentUserProfile,
                int prizeDTOSize,
                @NonNull ProviderDTO providerDTO)
        {
            super(resources, currentUserId, leaderboardItem, currentUserProfile);
            this.prizeDTOSize = prizeDTOSize;
            this.providerDTO = providerDTO;
            int currentRank = leaderboardItem.ordinalPosition + 1;
            if (prizeDTOSize != 0 && currentRank <= prizeDTOSize)
            {
                prizeIndicatorVisibility = View.VISIBLE;
            }
            else
            {
                prizeIndicatorVisibility = View.GONE;
            }
            String currencyDisplay;
            if (leaderboardItem.hasValidCurrencyDisplay())
            {
                currencyDisplay = leaderboardItem.getNiceCurrency();
            }
            else if (providerDTO.hasValidCurrencyDisplay())
            {
                currencyDisplay = providerDTO.getNiceCurrency();
            }
            else
            {
                currencyDisplay = leaderboardItem.getNiceCurrency();
            }
            this.lbmuPl = THSignedMoney
                .builder(leaderboardItem.PLinPeriodRefCcy)
                .withOutSign()
                .currency(currencyDisplay)
                .build().createSpanned();
            this.lbmuCommentsCount = THSignedNumber
                    .builder(leaderboardItem.getCommentsCount())
                    .build().createSpanned();
            String benchmarkRoiInPeriodFormat =
                    resources.getString(R.string.leaderboard_benchmark_roi_format);
            this.lbmuBenchmarkRoi = THSignedPercentage
                    .builder(leaderboardItem.getBenchmarkRoiInPeriod() * 100)
                    .withSign()
                    .signTypeArrow()
                    .relevantDigitCount(3)
                    .format(benchmarkRoiInPeriodFormat)
                    .boldValue()
                    .build().createSpanned();
            this.lbmuPositionsCount = "" + leaderboardItem.numberOfPositionsInPeriod;
            this.lbmuAvgDaysHeld = THSignedNumber
                    .builder(leaderboardItem.avgHoldingPeriodMins / (60 * 24))
                    .relevantDigitCount(3)
                    .build().createSpanned();
            this.lbmuFollowersCount = THSignedNumber
                    .builder(leaderboardItem.getTotalFollowersCount())
                    .build().createSpanned();
            this.lbmuWinRatio = THSignedPercentage
                    .builder(leaderboardItem.getWinRatio() * 100)
                    .relevantDigitCount(3)
                    .build().createSpanned();
            String numberOfTradeFormat =
                    resources.getQuantityString(R.plurals.leaderboard_number_of_trade, leaderboardItem.getNumberOfTrades());
            this.lbmuNumberOfTrades = THSignedNumber.builder(leaderboardItem.getNumberOfTrades())
                    .relevantDigitCount(1)
                    .withOutSign()
                    .format(numberOfTradeFormat)
                    .boldValue()
                    .build().createSpanned();
            String periodFormat = resources.getString(R.string.leaderboard_ranking_period);
            SimpleDateFormat sdf =
                    new SimpleDateFormat(resources.getString(R.string.leaderboard_datetime_format));
            String formattedStartPeriodUtc = sdf.format(leaderboardItem.periodStartUtc);
            String formattedEndPeriodUtc = sdf.format(leaderboardItem.periodEndUtc);
            this.lbmuPeriod = String.format(periodFormat, formattedStartPeriodUtc, formattedEndPeriodUtc);
            this.lbmuNumberTradesInPeriod = THSignedNumber
                    .builder(leaderboardItem.numberOfTradesInPeriod)
                    .build().createSpanned();
        }
    }

    public static class DTOList extends LeaderboardMarkUserItemView.DTOList
    {
        @NonNull final CompetitionLeaderboardDTO competitionLeaderboardDTO;

        public DTOList(@NonNull Resources resources,
                @NonNull CurrentUserId currentUserId,
                @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO,
                @NonNull UserProfileDTO currentUserProfile,
                @NonNull ProviderDTO providerDTO)
        {
            super(competitionLeaderboardDTO.leaderboard);
            this.competitionLeaderboardDTO = competitionLeaderboardDTO;
            int prizeDTOSize = competitionLeaderboardDTO.prizes == null? 0 : competitionLeaderboardDTO.prizes.size();
            for (LeaderboardUserDTO leaderboardItem : leaderboardDTO.getList())
            {
                add(new DTO(resources, currentUserId, leaderboardItem, currentUserProfile, prizeDTOSize, providerDTO));
            }
        }

        @Override public DTOList getList()
        {
            return this;
        }
    }

    public static class Requisite extends LeaderboardMarkUserItemView.Requisite
    {
        @NonNull final ProviderDTO providerDTO;
        @NonNull final CompetitionLeaderboardDTO competitionLeaderboardDTO;

        public Requisite(@NonNull LeaderboardMarkUserItemView.Requisite parent,
                @NonNull Pair<ProviderId, ProviderDTO> providerPair,
                @NonNull Pair<CompetitionLeaderboardId, CompetitionLeaderboardDTO> competitionLeaderboardPair)
        {
            this(parent.currentLeaderboardUserDTO,
                    parent.currentUserProfileDTO,
                    providerPair.second,
                    competitionLeaderboardPair.second);
        }

        public Requisite(
                @Nullable LeaderboardUserDTO currentLeaderboardUserDTO,
                @NonNull UserProfileDTO currentUserProfileDTO,
                @NonNull ProviderDTO providerDTO,
                @NonNull CompetitionLeaderboardDTO competitionLeaderboardDTO)
        {
            super(currentLeaderboardUserDTO, currentUserProfileDTO);
            this.providerDTO = providerDTO;
            this.competitionLeaderboardDTO = competitionLeaderboardDTO;
        }
    }
}
