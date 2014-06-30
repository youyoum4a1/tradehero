package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class LeaderboardCommunityAdapter extends ArrayAdapter<CommunityPageDTO>
        implements StickyListHeadersAdapter
{
    @Inject LeaderboardCommunityTypeFactory leaderboardCommunityTypeFactory;

    private final int competitionCompactViewResourceId;
    private final int leaderboardDefViewResourceId;

    public LeaderboardCommunityAdapter(Context context,
            int leaderboardDefViewResourceId,
            int competitionCompactViewResourceId)
    {
        super(context, 0);
        this.leaderboardDefViewResourceId = leaderboardDefViewResourceId;
        this.competitionCompactViewResourceId = competitionCompactViewResourceId;
        DaggerUtils.inject(this);
    }

    @Override public int getViewTypeCount()
    {
        return LeaderboardCommunityType.values().length;
    }

    @Override public int getItemViewType(int position)
    {
        return leaderboardCommunityTypeFactory.createFrom(getItem(position)).ordinal();
    }

    public int getItemViewResId(int position)
    {
        CommunityPageDTO item = getItem(position);
        if (item instanceof ProviderCommunityPageDTO)
        {
            return competitionCompactViewResourceId;
        }
        if (item instanceof LeaderboardDefCommunityPageDTO)
        {
            return leaderboardDefViewResourceId;
        }
        throw new IllegalArgumentException("Unhandled item " + getItem(position));
    }

    @SuppressWarnings("unchecked")
    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getItemViewResId(position), viewGroup, false);
        }
        ((DTOView<CommunityPageDTO>) convertView).display(getItem(position));
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
