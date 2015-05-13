package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.GraphicUtil;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardMarkUserListAdapter extends PagedViewDTOAdapterImpl<
        LeaderboardMarkUserItemView.DTO,
        LeaderboardMarkUserItemView>
{
    private static final int VIEW_TYPE_MAIN = 0;
    private static final int VIEW_TYPE_OWN = 1;

    @LayoutRes protected final int ownRankingRes;
    @NonNull protected final LeaderboardKey leaderboardKey;
    @Nullable protected OwnedPortfolioId applicablePortfolioId;

    @NonNull protected final PublishSubject<LeaderboardMarkUserItemView.UserAction> followRequestedPublish;

    @NonNull final Set<Integer> pageState;
    @Nullable UserProfileDTO ownProfileDTO;
    @Nullable LeaderboardMarkUserItemView.DTO ownRankingDto;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserListAdapter(
            @NonNull Context context,
            @LayoutRes int resource,
            @LayoutRes int ownRankingRes,
            @NonNull LeaderboardKey leaderboardKey)
    {
        super(context, resource);
        this.leaderboardKey = leaderboardKey;
        this.ownRankingRes = ownRankingRes;
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

    public void isNotRanked(@Nullable UserProfileDTO ownProfileDTO)
    {
        this.ownProfileDTO = ownProfileDTO;
    }

    public void isRanked(@Nullable LeaderboardMarkUserItemView.DTO ownRankingDto)
    {
        this.ownRankingDto = ownRankingDto;
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        return position == 0 ? VIEW_TYPE_OWN : VIEW_TYPE_MAIN;
    }

    @Override public int getCount()
    {
        return super.getCount() + 1;
    }

    @Override public LeaderboardMarkUserItemView.DTO getItem(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_OWN:
                return ownRankingDto;

            case VIEW_TYPE_MAIN:
                position--;
                LeaderboardMarkUserItemView.DTO item = super.getItem(position);
                item.leaderboardUserDTO.setPosition(position);
                item.leaderboardUserDTO.setLeaderboardId(leaderboardKey.id);
                boolean includeFoF = leaderboardKey instanceof FriendsPerPagedLeaderboardKey &&
                        ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF != null &&
                        ((FriendsPerPagedLeaderboardKey) leaderboardKey).includeFoF;
                item.leaderboardUserDTO.setIncludeFoF(includeFoF);
                return item;

            default:
                throw new IllegalArgumentException("Unhandled view type " + getItemViewType(position));
        }
    }

    @Override public LeaderboardMarkUserItemView getView(int position, View convertView, ViewGroup viewGroup)
    {
        LeaderboardMarkUserItemView dtoView;
        if (position == 0)
        {
            if (convertView == null)
            {
                convertView = inflate(position, viewGroup);
            }
            dtoView = (LeaderboardMarkUserItemView) convertView;
            dtoView.linkWith(applicablePortfolioId);
            if (ownRankingDto != null)
            {
                dtoView.display(ownRankingDto);
            }
            else if (ownProfileDTO != null)
            {
                dtoView.displayUserIsNotRanked(ownProfileDTO);
            }
            else
            {
                dtoView.displayUserIsLoading();
            }
        }
        else
        {
            dtoView = super.getView(position, convertView, viewGroup);
            GraphicUtil.setEvenOddBackground(position, dtoView);
            dtoView.linkWith(applicablePortfolioId);

            boolean expanded = getItem(position).isExpanded();
            dtoView.expandingLayout.expandWithNoAnimation(expanded);
            dtoView.setExpanded(expanded);
        }
        return dtoView;
    }

    @Override public void addPage(int page, @NonNull List<LeaderboardMarkUserItemView.DTO> objects)
    {
        if (pageState.contains(page))
        {
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
        LeaderboardMarkUserItemView view;
        if (position == 0)
        {
            view = (LeaderboardMarkUserItemView) LayoutInflater.from(getContext()).inflate(ownRankingRes, null);
        }
        else
        {
            view = super.inflate(position, viewGroup);
        }
        view.getFollowRequestedObservable().subscribe(followRequestedPublish);
        return view;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return super.areAllItemsEnabled();
    }
}
