package com.androidth.general.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.adapters.WrapperRecyclerAdapter;
import com.androidth.general.inject.HierarchyInjector;
import javax.inject.Inject;

public class CompetitionLeaderboardWrapperRecyclerAdapter extends WrapperRecyclerAdapter<WrapperRecyclerAdapter.ExtraItem>
{
    @Inject Picasso picasso;

    public CompetitionLeaderboardWrapperRecyclerAdapter(Context context, RecyclerView.Adapter realItemAdapter)
    {
        super(realItemAdapter);
        HierarchyInjector.inject(context, this);
    }

    @Override protected RecyclerView.ViewHolder onCreateExtraItemViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == CompetitionAdsExtraItem.VIEW_TYPE_ADS)
        {
            return new AdExtraItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_zone_ads, parent, false));
        }
        else if (viewType == CompetitionTimeExtraItem.VIEW_TYPE_TIME)
        {
            return new TimeExtraItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_timer_view, parent, false));
        }
        else
        {
            throw new IllegalArgumentException("Unhandled viewType");
        }
    }

    @Override protected void onBindExtraItemViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if (holder instanceof AdExtraItemViewHolder)
        {
            ExtraItem extraItem = getExtraItem(position);
            if (extraItem != null && extraItem instanceof CompetitionAdsExtraItem)
            {
                picasso.load(((CompetitionAdsExtraItem) extraItem).adDTO.bannerImageUrl).into(((AdExtraItemViewHolder) holder).banner);
            }
        }
        else if (holder instanceof TimeExtraItemViewHolder)
        {
            ExtraItem extraItem = getExtraItem(position);
            if (extraItem != null && extraItem instanceof CompetitionTimeExtraItem)
            {
                ((TimeExtraItemViewHolder) holder).display((CompetitionTimeExtraItem) extraItem);
            }
        }
    }

    protected static class AdExtraItemViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.banner) ImageView banner;

        public AdExtraItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class TimeExtraItemViewHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.value_day_count) protected TextView dayCountView;
        @BindView(R.id.value_hour_count) protected TextView hourCountView;
        @BindView(R.id.value_minute_count) protected TextView minuteCountView;
        @BindView(R.id.value_second_count) protected TextView secondCountView;

        //<editor-fold desc="Constructors">
        public TimeExtraItemViewHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        //</editor-fold>

        public void display(@NonNull CompetitionTimeExtraItem competitionTimeExtraItem)
        {
            dayCountView.setText(competitionTimeExtraItem.dayString);
            hourCountView.setText(competitionTimeExtraItem.hoursString);
            minuteCountView.setText(competitionTimeExtraItem.minutesString);
            secondCountView.setText(competitionTimeExtraItem.secondsString);
        }
    }
}
