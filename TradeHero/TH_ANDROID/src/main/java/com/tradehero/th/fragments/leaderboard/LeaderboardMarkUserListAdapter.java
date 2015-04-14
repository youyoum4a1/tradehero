package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardMarkUserListAdapter extends PagedViewDTOAdapterImpl<
        LeaderboardMarkUserItemView.DTO,
        LeaderboardMarkUserItemView>
{
    @NonNull protected final LeaderboardKey leaderboardKey;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @NonNull protected final PublishSubject<LeaderboardMarkUserItemView.UserAction> followRequestedPublish;

    Set<Integer> pageState;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(
            @NonNull Context context,
            @LayoutRes int resource,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(context, resource);
        this.leaderboardKey = leaderboardKey;
        this.followRequestedPublish = PublishSubject.create();
        pageState = Collections.synchronizedSet(new HashSet<Integer>());
    }
    //</editor-fold>

    public void setApplicablePortfolioId(@Nullable OwnedPortfolioId ownedPortfolioId)
    {
        this.applicablePortfolioId = ownedPortfolioId;
    }

    @NonNull public Observable<LeaderboardMarkUserItemView.UserAction> getFollowRequestedObservable()
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
        if (position % 2 == 0) {
            dtoView.setBackgroundResource(R.color.lb_item_even);
        } else {
            dtoView.setBackgroundResource(R.color.lb_item_odd);
        }
        dtoView.linkWith(applicablePortfolioId);

        boolean expanded = getItem(position).isExpanded();
        dtoView.expandingLayout.expandWithNoAnimation(expanded);
        dtoView.setExpanded(expanded);

        return dtoView;
    }

    @Override public void addPage(int page, @NonNull List<LeaderboardMarkUserItemView.DTO> objects)
    {
        if (pageState.contains(page)) {
            return;
        }
        pageState.add(page);
        pagedObjects.put(page, objects);
        super.addAll(objects);
        notifyDataSetChanged();
        setNotifyOnChange(true);
    }

    @Override public void clear()
    {
        super.clear();
        pageState.clear();
    }

    @NonNull @Override protected LeaderboardMarkUserItemView inflate(int position, ViewGroup viewGroup)
    {
        LeaderboardMarkUserItemView view = super.inflate(position, viewGroup);
        view.getFollowRequestedObservable().subscribe(followRequestedPublish);
        return view;
    }
}
