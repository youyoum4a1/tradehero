package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;

public class LeaderboardMarkUserListAdapter extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, LeaderboardMarkUserItemView, LeaderboardMarkUserLoader>
    implements PullToRefreshBase.OnRefreshListener<ListView>
{
    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;
    protected LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(Context context, int loaderId, int layoutResourceId)
    {
        super(context, loaderId, layoutResourceId);
    }
    //</editor-fold>

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        this.applicablePortfolioId = ownedPortfolioId;
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
        dtoView.linkWith(applicablePortfolioId);
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
            dtoView.onExpand(dto.isExpanded());
        }
    }

    @Override public void onRefresh(PullToRefreshBase<ListView> refreshView)
    {
        getLoader().loadPrevious();
    }

    protected LeaderboardMarkUserItemView.OnFollowRequestedListener createChildFollowRequestedListener()
    {
        return this::notifyFollowRequested;
    }

    protected void notifyFollowRequested(@NonNull UserBaseDTO userBaseDTO)
    {
        LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }
}
