package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.picasso.Picasso;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;

public class CompetitionLeaderboardMarkUserRecyclerAdapter extends LeaderboardMarkUserRecyclerAdapter<LeaderboardItemDisplayDTO>
{
    public CompetitionLeaderboardMarkUserRecyclerAdapter(Context context, @LayoutRes int itemLayoutRes, @LayoutRes int ownRankingRes,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(LeaderboardItemDisplayDTO.class, context, itemLayoutRes, ownRankingRes, leaderboardKey);
    }

    @NonNull @Override
    protected LbmuHeaderViewHolder<LeaderboardItemDisplayDTO> createOwnRankingLbmuViewHolder(ViewGroup parent)
    {
        return new CompetitionLbmuHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(ownRankingRes, parent, false), picasso,
                analytics);
    }

    @NonNull @Override
    protected LbmuItemViewHolder<LeaderboardItemDisplayDTO> createLbmuItemViewholder(ViewGroup parent)
    {
        return new CompetitionLbmuItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(itemLayoutRes, parent, false), picasso, analytics);
    }

    public static class CompetitionLbmuItemViewHolder extends LbmuItemViewHolder<LeaderboardItemDisplayDTO>
    {
        public CompetitionLbmuItemViewHolder(View itemView, Picasso picasso, Analytics analytics)
        {
            super(itemView, picasso, analytics);
        }
    }

    public static class CompetitionLbmuHeaderViewHolder extends LbmuHeaderViewHolder<LeaderboardItemDisplayDTO>
    {
        public CompetitionLbmuHeaderViewHolder(View itemView, Picasso picasso, Analytics analytics)
        {
            super(itemView, picasso, analytics);
        }
    }
}
