package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.DTOSetAdapter;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class LeaderboardFriendsSetAdapter extends DTOSetAdapter<FriendLeaderboardUserDTO>
{
    public static final int VIEW_TYPE_MARK = 0;
    public static final int VIEW_TYPE_SOCIAL = 1;

    @LayoutRes private final int markedLayoutResId;
    @LayoutRes private final int socialLayoutResId;

    protected UserProfileDTO currentUserProfileDTO;
    @Nullable protected BaseLeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener;

    @NonNull private Map<Object, Boolean> expandedStatues;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendsSetAdapter(
            @NonNull Context context,
            @LayoutRes int markedLayoutResId,
            @LayoutRes int socialLayoutResId)
    {
        super(context, new FriendLeaderboardUserComparator());
        this.markedLayoutResId = markedLayoutResId;
        this.socialLayoutResId = socialLayoutResId;
        this.expandedStatues = new HashMap<>();
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

    public void set(@NonNull LeaderboardFriendsDTO leaderboardFriendsDTO)
    {
        clear();
        notifyDataSetChanged();
        Observable.from(leaderboardFriendsDTO.leaderboard.users)
                .subscribeOn(Schedulers.computation())
                .map(this::createUserDTOFrom)
                .toList()
                .doOnNext(this::markPositions)
                .concatWith(Observable.from(leaderboardFriendsDTO.socialFriends)
                        .map(this::createUserDTOFrom)
                        .toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        friendLeaderboardMarkedUserDTOs -> {
                            appendHead(friendLeaderboardMarkedUserDTOs);
                            notifyDataSetChanged();
                        }, throwable -> {
                            Timber.e(throwable, "Failed setting leaderboardFriendsDTO");
                        });
    }

    private void markPositions(@NonNull List<? extends FriendLeaderboardUserDTO> friendLeaderboardUserDTOs)
    {
        int index = 1;
        for (FriendLeaderboardUserDTO dto : friendLeaderboardUserDTOs)
        {
            dto.setPosition(index++);
        }
    }

    private FriendLeaderboardUserDTO createUserDTOFrom(@NonNull LeaderboardUserDTO stocksLeaderboardUserDTO)
    {
        return new SavingFriendLeaderboardMarkedUserDTO(stocksLeaderboardUserDTO);
    }

    private class SavingFriendLeaderboardMarkedUserDTO extends FriendLeaderboardMarkedUserDTO
    {
        public SavingFriendLeaderboardMarkedUserDTO(@NonNull LeaderboardUserDTO stocksLeaderboardUserDTO)
        {
            this(expandedStatues.get(stocksLeaderboardUserDTO.id), stocksLeaderboardUserDTO);
        }

        public SavingFriendLeaderboardMarkedUserDTO(@Nullable Boolean expanded, @NonNull LeaderboardUserDTO stocksLeaderboardUserDTO)
        {
            super(expanded == null ? false : expanded, stocksLeaderboardUserDTO);
        }

        @Override public void setExpanded(boolean expanded)
        {
            super.setExpanded(expanded);
            LeaderboardFriendsSetAdapter.this.expandedStatues.put(stocksLeaderboardUserDTO.id, expanded);
        }
    }

    private FriendLeaderboardUserDTO createUserDTOFrom(@NonNull UserFriendsDTO userFriendsDTO)
    {
        return new FriendLeaderboardSocialUserDTO(userFriendsDTO);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(getItemLayoutResId(position), parent, false);
        }

        FriendLeaderboardUserDTO item = getItem(position);

        if (convertView instanceof BaseLeaderboardMarkUserItemView)
        {
            LeaderboardUserDTO stocksLeaderboardUserDTO =
                    ((FriendLeaderboardMarkedUserDTO) item).stocksLeaderboardUserDTO;
            ((FriendLeaderboardMarkedUserDTO) item).stocksLeaderboardUserDTO.setPosition(position); // HACK FIXME
            ((BaseLeaderboardMarkUserItemView) convertView).display(stocksLeaderboardUserDTO);
            ((BaseLeaderboardMarkUserItemView) convertView).linkWith(currentUserProfileDTO, true);
            ((BaseLeaderboardMarkUserItemView) convertView).setFollowRequestedListener(this::notifyFollowRequested);
        }
        else if (convertView instanceof LeaderboardFriendsItemView)
        {
            ((LeaderboardFriendsItemView) convertView).display(((FriendLeaderboardSocialUserDTO) item).userFriendsDTO);
        }

        final View expandingLayout = convertView.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);
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

    protected void notifyFollowRequested(@NonNull UserBaseDTO userBaseDTO)
    {
        BaseLeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }

    public void setFollowRequestedListener(@Nullable BaseLeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }
}
