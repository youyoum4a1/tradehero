package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardFriendsDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class LeaderboardFriendsListAdapter extends ArrayAdapter<FriendLeaderboardUserDTO>
{
    public static final int VIEW_TYPE_MARK = 0;
    public static final int VIEW_TYPE_SOCIAL = 1;

    private final int markedLayoutResId;
    private final int socialLayoutResId;

    protected UserProfileDTO currentUserProfileDTO;
    protected LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener;

    public LeaderboardFriendsListAdapter(Context context, int markedLayoutResId, int socialLayoutResId)
    {
        super(context, 0);
        this.markedLayoutResId = markedLayoutResId;
        this.socialLayoutResId = socialLayoutResId;
    }

    @Override public int getViewTypeCount()
    {
        return 2;
    }

    @Override public int getItemViewType(int position)
    {
        @NotNull FriendLeaderboardUserDTO item = getItem(position);
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

    public int getItemLayoutResId(int position)
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

    public void add(@NotNull LeaderboardFriendsDTO leaderboardFriendsDTO)
    {
        addAll(leaderboardFriendsDTO.leaderboard.users, (LeaderboardUserDTO) null);
        addAll(leaderboardFriendsDTO.socialFriends, (UserFriendsDTO) null);
    }

    public void addAll(@NotNull Collection<? extends LeaderboardUserDTO> collection, LeaderboardUserDTO typeQualifier)
    {
        List<FriendLeaderboardUserDTO> created = new ArrayList<>();
        for (LeaderboardUserDTO leaderboardUserDTO : collection)
        {
            created.add(new FriendLeaderboardMarkedUserDTO(leaderboardUserDTO));
        }
        super.addAll(created);
    }

    public void addAll(@NotNull Collection<? extends UserFriendsDTO> collection, UserFriendsDTO typeQualifier)
    {
        List<FriendLeaderboardUserDTO> created = new ArrayList<>();
        for (UserFriendsDTO userFriendsDTO : collection)
        {
            created.add(new FriendLeaderboardSocialUserDTO(userFriendsDTO));
        }
        super.addAll(created);
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
            ((FriendLeaderboardMarkedUserDTO) item).leaderboardUserDTO.setPosition(position); // HACK
            ((LeaderboardMarkUserItemView) convertView).display(((FriendLeaderboardMarkedUserDTO) item).leaderboardUserDTO);
            ((LeaderboardMarkUserItemView) convertView).linkWith(currentUserProfileDTO, true);
            ((LeaderboardMarkUserItemView) convertView).setFollowRequestedListener(createChildFollowRequestedListener());
            final View expandingLayout = convertView.findViewById(R.id.expanding_layout);
            if (expandingLayout != null)
            {
                expandingLayout.setVisibility(((FriendLeaderboardMarkedUserDTO) item).leaderboardUserDTO.isExpanded() ? View.VISIBLE : View.GONE);
            }
        }
        else if (convertView instanceof LeaderboardFriendsItemView)
        {
            ((LeaderboardFriendsItemView) convertView).display(((FriendLeaderboardSocialUserDTO) item).userFriendsDTO);
        }

        return convertView;
    }

    public void setCurrentUserProfileDTO(UserProfileDTO currentUserProfileDTO)
    {
        this.currentUserProfileDTO = currentUserProfileDTO;
        notifyDataSetChanged();
    }

    protected LeaderboardMarkUserItemView.OnFollowRequestedListener createChildFollowRequestedListener()
    {
        return new LeaderboardMarkUserItemView.OnFollowRequestedListener()
        {
            @Override public void onFollowRequested(UserBaseDTO userBaseDTO)
            {
                notifyFollowRequested(userBaseDTO);
            }
        };
    }

    protected void notifyFollowRequested(UserBaseDTO userBaseDTO)
    {
        LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListenerCopy = followRequestedListener;
        if (followRequestedListenerCopy != null)
        {
            followRequestedListenerCopy.onFollowRequested(userBaseDTO);
        }
    }

    public void setFollowRequestedListener(LeaderboardMarkUserItemView.OnFollowRequestedListener followRequestedListener)
    {
        this.followRequestedListener = followRequestedListener;
    }
}
