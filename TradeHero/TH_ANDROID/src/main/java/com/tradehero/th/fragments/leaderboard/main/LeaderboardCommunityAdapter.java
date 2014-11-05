package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class LeaderboardCommunityAdapter extends ArrayAdapter<LeaderboardDefDTO>
        implements StickyListHeadersAdapter
{
    @Inject LeaderboardCommunityTypeFactory leaderboardCommunityTypeFactory;

    private final int leaderboardDefViewResourceId;

    //<editor-fold desc="Constructors">
    public LeaderboardCommunityAdapter(
            @NonNull Context context,
            int leaderboardDefViewResourceId)
    {
        super(context, 0);
        this.leaderboardDefViewResourceId = leaderboardDefViewResourceId;
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return LeaderboardCommunityType.values().length;
    }

    @Override public int getItemViewType(int position)
    {
        return leaderboardCommunityTypeFactory.createFrom(getItem(position)).ordinal();
    }

    public int getItemViewResId(@SuppressWarnings("UnusedParameters") int position)
    {
        return leaderboardDefViewResourceId;
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getItemViewResId(position), viewGroup, false);
        }
        ((DTOView<LeaderboardDefDTO>) convertView).display(getItem(position));
        return convertView;
    }

    //<editor-fold desc="For headers">
    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_separator, parent, false);
        }
        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        return getItemViewType(position);
    }
    //</editor-fold>
}
