package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;

public class LeaderboardMarkUserListAdapter
        extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, LeaderboardMarkUserItemView, LeaderboardMarkUserLoader>
        implements SwipeRefreshLayout.OnRefreshListener
{
    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;
    protected LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(Context context, int loaderId, @LayoutRes int layoutResId)
    {
        super(context, loaderId, layoutResId);
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

    @Override public LeaderboardUserDTO getItem(int position)
    {
        LeaderboardUserDTO dto = (LeaderboardUserDTO) super.getItem(position);
        dto.setPosition(position);
        dto.setLeaderboardId(getLoader().getLeaderboardId());
        dto.setIncludeFoF(getLoader().isIncludeFoF());

        return dto;
    }

    @Override protected void fineTune(int position, LeaderboardUserDTO dto, LeaderboardMarkUserItemView dtoView)
    {
        dtoView.linkWith(currentUserProfileDTO);
        dtoView.linkWith(applicablePortfolioId);
        dtoView.setFollowRequestedListener(createChildFollowRequestedListener());

        final ExpandingLayout expandingLayout = (ExpandingLayout) dtoView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.expandWithNoAnimation(dto.isExpanded());
            dtoView.onExpand(dto.isExpanded());
        }
    }

    @Override public void onRefresh()
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
