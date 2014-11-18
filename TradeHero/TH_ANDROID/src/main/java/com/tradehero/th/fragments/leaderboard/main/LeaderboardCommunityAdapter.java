package com.tradehero.th.fragments.leaderboard.main;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.GenericArrayAdapter;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class LeaderboardCommunityAdapter extends GenericArrayAdapter<Pair<LeaderboardDefDTO, UserProfileDTO>>
        implements StickyListHeadersAdapter
{
    @Inject LeaderboardCommunityTypeFactory leaderboardCommunityTypeFactory;

    //<editor-fold desc="Constructors">
    public LeaderboardCommunityAdapter(@NonNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return LeaderboardCommunityType.values().length;
    }

    @Override public int getItemViewType(int position)
    {
        Pair<LeaderboardDefDTO, UserProfileDTO> pair = (Pair<LeaderboardDefDTO, UserProfileDTO>) getItem(position);
        return leaderboardCommunityTypeFactory.createFrom(pair.first).ordinal();
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
