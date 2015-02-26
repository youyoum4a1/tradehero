package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardMarkUserListAdapter extends PagedViewDTOAdapterImpl<
        LeaderboardMarkUserItemView.DTO,
        LeaderboardMarkUserItemView>
{
    @NonNull protected final LeaderboardKey leaderboardKey;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @NonNull protected final PublishSubject<UserBaseDTO> followRequestedPublish;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(
            @NonNull Context context,
            @LayoutRes int resource,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(context, resource);
        this.leaderboardKey = leaderboardKey;
        this.followRequestedPublish = PublishSubject.create();
    }
    //</editor-fold>

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        this.applicablePortfolioId = ownedPortfolioId;
    }

    @NonNull public Observable<UserBaseDTO> getFollowRequestedObservable()
    {
        return followRequestedPublish.asObservable();
    }

    @Override public LeaderboardMarkUserItemView.DTO getItem(int position)
    {
        LeaderboardMarkUserItemView.DTO item = super.getItem(position);
        item.leaderboardUserDTO.setPosition(position);
        item.leaderboardUserDTO.setLeaderboardId(leaderboardKey.id);
        boolean includeFoF = leaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF != null &&
                ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF;
        item.leaderboardUserDTO.setIncludeFoF(includeFoF);
        return item;
    }

    @Override public LeaderboardMarkUserItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        LeaderboardMarkUserItemView dtoView = super.getView(position, convertView, viewGroup);
        dtoView.linkWith(applicablePortfolioId);

        boolean expanded = getItem(position).leaderboardUserDTO.isExpanded();
        dtoView.expandingLayout.expandWithNoAnimation(expanded);
        dtoView.setExpanded(expanded);

        return dtoView;
    }

    @NonNull @Override protected LeaderboardMarkUserItemView inflate(int position, ViewGroup viewGroup)
    {
        LeaderboardMarkUserItemView view = super.inflate(position, viewGroup);
        view.getFollowRequestedObservable().subscribe(followRequestedPublish);
        return view;
    }
}
