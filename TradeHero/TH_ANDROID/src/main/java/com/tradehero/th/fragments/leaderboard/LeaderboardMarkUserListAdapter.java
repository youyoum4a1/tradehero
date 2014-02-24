package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: tho Date: 10/21/13 Time: 4:13 PM Copyright (c) TradeHero */
public class LeaderboardMarkUserListAdapter extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, LeaderboardMarkUserItemView, LeaderboardMarkUserLoader>
    implements PullToRefreshBase.OnRefreshListener<ListView>
{
    protected WeakReference<THIABUserInteractor> userInteractor = new WeakReference<>(null);
    protected UserProfileDTO currentUserProfileDTO;

    public LeaderboardMarkUserListAdapter(Context context, LayoutInflater inflater, int loaderId, int layoutResourceId)
    {
        super(context, inflater, loaderId, layoutResourceId);
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        notifyDataSetChanged();
    }

    public void setUserInteractor(THIABUserInteractor userInteractor)
    {
        this.userInteractor = new WeakReference<>(userInteractor);
        notifyDataSetChanged();
    }

    @Override public Object getItem(int position)
    {
        LeaderboardUserDTO dto = (LeaderboardUserDTO) super.getItem(position);
        dto.setPosition(position);
        dto.setLeaderboardId(getLoader().getLeaderboardId());
        dto.setIncludeFoF(getLoader().isIncludeFoF());

        return dto;
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO dto, LeaderboardMarkUserItemView dtoView)
    {
        dtoView.linkWith(userInteractor.get(), false);
        dtoView.linkWith(currentUserProfileDTO, true);

        final View expandingLayout = dtoView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(dto.isExpanded() ? View.VISIBLE : View.GONE);
        }
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        getLoader().loadPrevious();
    }
}
