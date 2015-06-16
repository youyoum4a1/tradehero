package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.content.res.Resources;
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
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
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

    public void display(@NonNull LeaderboardMarkedUserItemDisplayDto parentViewDTO)
    {
        //super.display(parentViewDTO);
        //if (parentViewDTO instanceof DTO)
        //{
        //    DTO viewDTO = (DTO) parentViewDTO;
        //    if (prizeIndicator != null)
        //    {
        //        prizeIndicator.setVisibility(viewDTO.prizeIndicatorVisibility);
        //    }
        //    if (lbmuPl != null)
        //    {
        //        lbmuPl.setText(viewDTO.lbmuPl);
        //    }
        //    if (lbmuCommentsCount != null)
        //    {
        //        lbmuCommentsCount.setText(viewDTO.lbmuCommentsCount);
        //    }
        //    if (lbmuBenchmarkRoi != null)
        //    {
        //        lbmuBenchmarkRoi.setText(viewDTO.lbmuBenchmarkRoi);
        //    }
        //    if (lbmuPositionsCount != null)
        //    {
        //        lbmuPositionsCount.setText(viewDTO.lbmuPositionsCount);
        //    }
        //    if (lbmuAvgDaysHeld != null)
        //    {
        //        lbmuAvgDaysHeld.setText(viewDTO.lbmuAvgDaysHeld);
        //    }
        //    if (lbmuFollowersCount != null)
        //    {
        //        lbmuFollowersCount.setText(viewDTO.lbmuFollowersCount);
        //    }
        //    if (lbmuWinRatio != null)
        //    {
        //        lbmuWinRatio.setText(viewDTO.lbmuWinRatio);
        //    }
        //    if (lbmuNumberOfTrades != null)
        //    {
        //        lbmuNumberOfTrades.setText(viewDTO.lbmuNumberOfTrades);
        //    }
        //    if (lbmuPeriod != null)
        //    {
        //        lbmuPeriod.setText(viewDTO.lbmuPeriod);
        //    }
        //    if (lbmuNumberTradesInPeriod != null)
        //    {
        //        lbmuNumberTradesInPeriod.setText(viewDTO.lbmuNumberTradesInPeriod);
        //    }
        //}
    }

    public static class DTOList extends LeaderboardItemDisplayDTO.DTOList<LeaderboardItemDisplayDTO>
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
            int prizeDTOSize = competitionLeaderboardDTO.prizes == null ? 0 : competitionLeaderboardDTO.prizes.size(); //TODO for what?
            for (LeaderboardUserDTO leaderboardItem : leaderboardDTO.getList())
            {
                add(new CompetitionLeaderboardItemDisplayDto(resources, currentUserId, leaderboardItem, currentUserProfile, providerDTO));
            }
        }

        @Override public DTOList getList()
        {
            return this;
        }
    }

    public static class Requisite extends LeaderboardMarkedUserItemDisplayDto.Requisite
    {
        @NonNull final ProviderDTO providerDTO;
        @NonNull final CompetitionLeaderboardDTO competitionLeaderboardDTO;

        public Requisite(@NonNull LeaderboardMarkedUserItemDisplayDto.Requisite parent,
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
