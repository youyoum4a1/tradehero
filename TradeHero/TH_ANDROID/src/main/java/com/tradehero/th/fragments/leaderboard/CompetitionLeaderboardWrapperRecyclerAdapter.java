package com.tradehero.th.fragments.leaderboard;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.WrapperRecyclerAdapter;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.leaderboard.competition.CompetitionLeaderboardDTO;

public class CompetitionLeaderboardWrapperRecyclerAdapter extends WrapperRecyclerAdapter<CompetitionLbmuExtraItem>
{
    public CompetitionLeaderboardWrapperRecyclerAdapter(RecyclerView.Adapter realItemAdapter)
    {
        super(realItemAdapter);
    }

    @Override protected RecyclerView.ViewHolder onCreateExtraItemViewHolder(ViewGroup parent, int viewType)
    {
        return null;
    }

    @Override protected void onBindExtraItemViewHolder(RecyclerView.ViewHolder holder, int position)
    {

    }

    public void setupCompetition(ProviderDTO providerDTO, CompetitionLeaderboardDTO competitionLeaderboardDTO)
    {

    }

    protected static class CompetitionExtraItemViewHolder extends RecyclerView.ViewHolder
    {

        public CompetitionExtraItemViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
