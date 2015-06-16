package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.adapters.WrapperRecyclerAdapter;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class CompetitionLeaderboardWrapperRecyclerAdapter extends WrapperRecyclerAdapter<CompetitionAdsExtraItem>
{
    @Inject Picasso picasso;

    public CompetitionLeaderboardWrapperRecyclerAdapter(Context context, RecyclerView.Adapter realItemAdapter)
    {
        super(realItemAdapter);
        HierarchyInjector.inject(context, this);
    }

    @Override protected RecyclerView.ViewHolder onCreateExtraItemViewHolder(ViewGroup parent, int viewType)
    {
        return new AdExtraItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_zone_ads, parent, false));
    }

    @Override protected void onBindExtraItemViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof AdExtraItemViewHolder)
        {
            CompetitionAdsExtraItem adsExtraItem = getExtraItem(position);
            if (adsExtraItem != null)
            {
                picasso.load(adsExtraItem.adDTO.bannerImageUrl).into(((AdExtraItemViewHolder) holder).banner);
            }
        }
    }

    protected static class AdExtraItemViewHolder extends RecyclerView.ViewHolder
    {
        @InjectView(R.id.banner) ImageView banner;

        public AdExtraItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
