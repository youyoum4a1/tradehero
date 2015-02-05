package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class LeaderboardFriendsSetAdapter extends PagedDTOAdapterImpl<FriendLeaderboardUserDTO>
{
    public static final int VIEW_TYPE_MARK = 0;
    public static final int VIEW_TYPE_SOCIAL = 1;

    @LayoutRes private final int markedLayoutResId;
    @LayoutRes private final int socialLayoutResId;

    protected UserProfileDTO currentUserProfileDTO;

    @NonNull private final Map<Object, Boolean> expandedStatuses;
    @NonNull private final BehaviorSubject<UserBaseDTO> followRequestedBehavior;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendsSetAdapter(
            @NonNull Context context,
            @LayoutRes int markedLayoutResId,
            @LayoutRes int socialLayoutResId)
    {
        super(context, markedLayoutResId);
        this.markedLayoutResId = markedLayoutResId;
        this.socialLayoutResId = socialLayoutResId;
        this.expandedStatuses = new HashMap<>();
        this.followRequestedBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        FriendLeaderboardUserDTO item = getItem(position);
        if (item instanceof FriendLeaderboardMarkedUserDTO)
        {
            return VIEW_TYPE_MARK;
        }
        if (item instanceof FriendLeaderboardSocialUserDTO)
        {
            return VIEW_TYPE_SOCIAL;
        }
        throw new IllegalStateException("Unhandled class type " + item.getClass());
    }

    @LayoutRes public int getItemLayoutResId(int position)
    {
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_MARK:
                return markedLayoutResId;

            case VIEW_TYPE_SOCIAL:
                return socialLayoutResId;
        }
        throw new IllegalStateException("Unhandled item view type " + getItemViewType(position));
    }

    @NonNull public Observable<UserBaseDTO> getFollowRequestObservable()
    {
        return followRequestedBehavior.asObservable();
    }

    @NonNull @Override protected List<FriendLeaderboardUserDTO> makeItems()
    {
        Set<FriendLeaderboardUserDTO> set = new TreeSet<>(new FriendLeaderboardUserComparator());
        set.addAll(super.makeItems());
        markPositions(set);
        return new ArrayList<>(set);
    }


    private void markPositions(@NonNull Collection<? extends FriendLeaderboardUserDTO> friendLeaderboardUserDTOs)
    {
        int index = 1;
        for (FriendLeaderboardUserDTO dto : friendLeaderboardUserDTOs)
        {
            dto.setPosition(index++);
        }
    }

    @NonNull public FriendLeaderboardUserDTOFactory createItemFactory()
    {
        return new FriendLeaderboardUserDTOFactory()
        {
            @NonNull public FriendLeaderboardUserDTO create(@NonNull LeaderboardUserDTO leaderboardUserDTO)
            {
                return new SavingFriendLeaderboardMarkedUserDTO(leaderboardUserDTO);
            }

            @NonNull public FriendLeaderboardUserDTO create(@NonNull UserFriendsDTO userFriendsDTO)
            {
                return new FriendLeaderboardSocialUserDTO(userFriendsDTO);
            }
        };
    }

    private class SavingFriendLeaderboardMarkedUserDTO extends FriendLeaderboardMarkedUserDTO
    {
        //<editor-fold desc="Constructors">
        public SavingFriendLeaderboardMarkedUserDTO(@NonNull LeaderboardUserDTO leaderboardUserDTO)
        {
            this(expandedStatuses.get(leaderboardUserDTO.id), leaderboardUserDTO);
        }

        public SavingFriendLeaderboardMarkedUserDTO(@Nullable Boolean expanded, @NonNull LeaderboardUserDTO leaderboardUserDTO)
        {
            super(expanded == null ? false : expanded, leaderboardUserDTO);
        }
        //</editor-fold>

        @Override public void setExpanded(boolean expanded)
        {
            super.setExpanded(expanded);
            expandedStatuses.put(leaderboardUserDTO.id, expanded);
        }
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(getItemLayoutResId(position), parent, false);
        }

        FriendLeaderboardUserDTO item = getItem(position);

        if (convertView instanceof LeaderboardMarkUserItemView)
        {
            LeaderboardUserDTO leaderboardUserDTO =
                    ((FriendLeaderboardMarkedUserDTO) item).leaderboardUserDTO;
            ((FriendLeaderboardMarkedUserDTO) item).leaderboardUserDTO.setPosition(position); // HACK FIXME
            ((LeaderboardMarkUserItemView) convertView).display(leaderboardUserDTO);
            ((LeaderboardMarkUserItemView) convertView).linkWith(currentUserProfileDTO);
            ((LeaderboardMarkUserItemView) convertView).getFollowRequestedObservable()
                    .subscribe(followRequestedBehavior);
        }
        else if (convertView instanceof LeaderboardFriendsItemView)
        {
            ((LeaderboardFriendsItemView) convertView).display(((FriendLeaderboardSocialUserDTO) item).userFriendsDTO);
        }

        final ExpandingLayout expandingLayout = (ExpandingLayout) convertView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.expandWithNoAnimation(item.isExpanded());
            if (item.isExpanded() && convertView instanceof ExpandingLayout.OnExpandListener)
            {
                ((ExpandingLayout.OnExpandListener) convertView).onExpand(true);
            }
        }

        return convertView;
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        notifyDataSetChanged();
    }
}
