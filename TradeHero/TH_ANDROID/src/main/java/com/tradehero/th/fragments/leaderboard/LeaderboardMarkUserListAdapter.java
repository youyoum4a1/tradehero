package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.leaderboard.BaseLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.FxLeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;

public class LeaderboardMarkUserListAdapter
        <LeaderboardUserDTO extends BaseLeaderboardUserDTO,
                LoaderType extends LeaderboardMarkUserLoader<LeaderboardUserDTO>>
        extends
        LoaderDTOAdapter<
                LeaderboardUserDTO, BaseLeaderboardMarkUserItemView<LeaderboardUserDTO>, LoaderType>
        implements SwipeRefreshLayout.OnRefreshListener
{
    private static final int VIEW_TYPE_STOCK = 0;
    private static final int VIEW_TYPE_FX = 1;

    @LayoutRes private static final int fxLeaderboardLayoutResId = R.layout.lbmu_item_fx_mode;
    @LayoutRes private static final int stockLeaderboardLayoutResId = R.layout.lbmu_item_roi_mode;

    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;
    protected LeaderboardMarkUserStockItemView.OnFollowRequestedListener followRequestedListener;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(Context context, int loaderId)
    {
        super(context, loaderId, 0);
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        BaseLeaderboardUserDTO leaderboardUserDTO = getItem(position);
        if(leaderboardUserDTO instanceof StocksLeaderboardUserDTO)
        {
            return VIEW_TYPE_STOCK;
        }
        else if(leaderboardUserDTO instanceof FxLeaderboardUserDTO)
        {
            return VIEW_TYPE_FX;
        }
        else
        {
            return super.getItemViewType(position);
        }
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
    }

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        this.applicablePortfolioId = ownedPortfolioId;
    }

    public void setFollowRequestedListener(LeaderboardMarkUserStockItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }

    @Override public BaseLeaderboardUserDTO getItem(int position)
    {
        StocksLeaderboardUserDTO dto = (StocksLeaderboardUserDTO) super.getItem(position);
        dto.setPosition(position);
        dto.setLeaderboardId(getLoader().getLeaderboardId());
        dto.setIncludeFoF(getLoader().isIncludeFoF());

        return dto;
    }

    @Override protected View conditionalInflate(int position, View convertView, ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            convertView = getInflater().inflate(getLayoutIdforPosition(position), viewGroup, false);
        }
        return super.conditionalInflate(position, convertView, viewGroup);
    }

    @Override protected void fineTune(int position, BaseLeaderboardUserDTO dto, BaseLeaderboardMarkUserItemView dtoView)
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

    @Override public void onRefresh()
    {
        getLoader().loadPrevious();
    }

    protected LeaderboardMarkUserStockItemView.OnFollowRequestedListener createChildFollowRequestedListener()
    {
        return this::notifyFollowRequested;
    }

    protected void notifyFollowRequested(@NonNull UserBaseDTO userBaseDTO)
    {
        LeaderboardMarkUserStockItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }

    private int getLayoutIdforPosition(int position)
    {
        int viewType = getItemViewType(position);
        switch (viewType)
        {
            case VIEW_TYPE_FX:
                return fxLeaderboardLayoutResId;
            case VIEW_TYPE_STOCK:
                return stockLeaderboardLayoutResId;
            default:
                throw new RuntimeException("Unknown layout for class " + getItem(position).getClass().getName());
        }
    }
}
