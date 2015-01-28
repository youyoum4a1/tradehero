package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class LeaderboardMarkUserListAdapter extends PagedViewDTOAdapterImpl<
        LeaderboardUserDTO,
        LeaderboardMarkUserItemView>
{
    @NonNull protected final LeaderboardKey leaderboardKey;
    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @NonNull protected final BehaviorSubject<UserBaseDTO> followRequestedBehavior;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(
            @NonNull Context context,
            @LayoutRes int resource,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(context, resource);
        this.leaderboardKey = leaderboardKey;
        this.followRequestedBehavior = BehaviorSubject.create();
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

    @NonNull public Observable<UserBaseDTO> getFollowRequestedObservable()
    {
        return followRequestedBehavior.asObservable();
    }

    @Override public LeaderboardUserDTO getItem(int position)
    {
        LeaderboardUserDTO item = super.getItem(position);
        item.setPosition(position);
        item.setLeaderboardId(leaderboardKey.id);
        boolean includeFoF = leaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF != null &&
                ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF;
        item.setIncludeFoF(includeFoF);
        return item;
    }

    @Override public LeaderboardMarkUserItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        LeaderboardMarkUserItemView dtoView = super.getView(position, convertView, viewGroup);

        dtoView.linkWith(currentUserProfileDTO);
        dtoView.linkWith(applicablePortfolioId);
        dtoView.getFollowRequestedObservable().subscribe(followRequestedBehavior);

        final ExpandingLayout expandingLayout = (ExpandingLayout) dtoView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.expandWithNoAnimation(getItem(position).isExpanded());
            dtoView.onExpand(getItem(position).isExpanded());
        }

        return dtoView;
    }
}
