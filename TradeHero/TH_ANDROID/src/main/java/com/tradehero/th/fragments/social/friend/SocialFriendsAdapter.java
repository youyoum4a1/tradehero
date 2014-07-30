package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SocialFriendsAdapter extends ArrayDTOAdapterNew<SocialFriendListItemDTO, SocialFriendItemView>
{
    private int mLayoutItemResId;
    private int mLayoutHeaderResId;
    @Nullable private SocialFriendUserView.OnElementClickListener elementClickedListener;

    //<editor-fold desc="Constructors">
    public SocialFriendsAdapter(Context context, List<SocialFriendListItemDTO> objects, int layoutItemResId, int layoutHeaderResId)
    {
        super(context, 0);
        addAll(objects);
        this.mLayoutItemResId = layoutItemResId;
        this.mLayoutHeaderResId = layoutHeaderResId;
    }
    //</editor-fold>

    @Override
    public SocialFriendItemView getView(int position, View convertView, ViewGroup parent)
    {
        SocialFriendItemView itemView = super.getView(position, convertView, parent);
        if (convertView instanceof SocialFriendUserView)
        {
            ((SocialFriendUserView) convertView).setOnElementClickedListener(createUserClickedListener());
        }
        return itemView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItem(position) instanceof SocialFriendListItemUserDTO;
    }

    @Override public int getViewResId(int position)
    {
        SocialFriendListItemDTO item = getItem(position);
        if (item instanceof SocialFriendListItemHeaderDTO)
        {
            return mLayoutHeaderResId;
        }
        else if (item instanceof SocialFriendListItemUserDTO)
        {
            return mLayoutItemResId;
        }
        throw new IllegalArgumentException("Unhandled item type " + item.getClass());
    }

    public void setOnElementClickedListener(
            @Nullable SocialFriendUserView.OnElementClickListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void handleFollowEvent(@NotNull UserFriendsDTO userFriendsDTO)
    {
        SocialFriendUserView.OnElementClickListener listenerCopy = elementClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onFollowButtonClick(userFriendsDTO);
        }
    }

    protected void handleInviteEvent(@NotNull UserFriendsDTO userFriendsDTO)
    {
        SocialFriendUserView.OnElementClickListener listenerCopy = elementClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onInviteButtonClick(userFriendsDTO);
        }
    }

    protected SocialFriendUserView.OnElementClickListener createUserClickedListener()
    {
        return new SocialElementClickListener();
    }

    protected class SocialElementClickListener implements SocialFriendUserView.OnElementClickListener
    {
        @Override
        public void onFollowButtonClick(@NotNull UserFriendsDTO userFriendsDTO)
        {
            handleFollowEvent(userFriendsDTO);
        }

        @Override
        public void onInviteButtonClick(@NotNull UserFriendsDTO userFriendsDTO)
        {
            handleInviteEvent(userFriendsDTO);
        }

        @Override
        public void onCheckBoxClick(@NotNull UserFriendsDTO userFriendsDTO)
        {
            Timber.d("onCheckBoxClicked " + userFriendsDTO);
        }
    }
}
