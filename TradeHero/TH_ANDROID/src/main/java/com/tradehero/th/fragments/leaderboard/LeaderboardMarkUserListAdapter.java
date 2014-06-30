package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.thm.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractor;
import javax.inject.Inject;

public class LeaderboardMarkUserListAdapter extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, LeaderboardMarkUserItemView, LeaderboardMarkUserLoader>
    implements PullToRefreshBase.OnRefreshListener<ListView>
{
    @Inject protected THBillingInteractor userInteractor;
    protected UserProfileDTO currentUserProfileDTO;
    protected LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener;

    public LeaderboardMarkUserListAdapter(Context context, LayoutInflater inflater, int loaderId, int layoutResourceId)
    {
        super(context, inflater, loaderId, layoutResourceId);
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        notifyDataSetChanged();
    }

    public void setFollowRequestedListener(LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
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
        dtoView.linkWith(currentUserProfileDTO, true);
        dtoView.setFollowRequestedListener(createChildFollowRequestedListener());

        final View expandingLayout = dtoView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            //if(expandingLayout instanceof ExpandingLayout)
            //{
            //    ((ExpandingLayout)expandingLayout).expand(dto.isExpanded());
            //}
            //else
            //{
            //    expandingLayout.setVisibility(dto.isExpanded() ? View.VISIBLE : View.GONE);
            //}
            //TODO
            expandingLayout.setVisibility(dto.isExpanded() ? View.VISIBLE : View.GONE);

        }
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        getLoader().loadPrevious();
    }

    protected LeaderboardMarkUserItemView.OnFollowRequestedListener createChildFollowRequestedListener()
    {
        return new LeaderboardMarkUserItemView.OnFollowRequestedListener()
        {
            @Override public void onFollowRequested(UserBaseKey userBaseKey)
            {
                notifyFollowRequested(userBaseKey);
            }
        };
    }

    protected void notifyFollowRequested(UserBaseKey userBaseKey)
    {
        LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseKey);
        }
    }
}
