package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SocialFriendsAdapter extends ArrayDTOAdapterNew<SocialFriendListItemDTO, SocialFriendItemView>
{
    protected NameFilter filterToUse;
    private int mLayoutItemResId;
    private int mLayoutHeaderResId;
    @Nullable private SocialFriendUserView.OnElementClickListener elementClickedListener;

    private List<SocialFriendListItemDTO> mFilteredArrayList;
    private List<SocialFriendListItemDTO> mArrayList;

    //<editor-fold desc="Constructors">
    public SocialFriendsAdapter(Context context, List<SocialFriendListItemDTO> objects, int layoutItemResId, int layoutHeaderResId)
    {
        super(context, 0);
        addAll(objects);
        mArrayList = new ArrayList<>();
        mArrayList.addAll(objects);
        this.mLayoutItemResId = layoutItemResId;
        this.mLayoutHeaderResId = layoutHeaderResId;
    }
    //</editor-fold>

    @Override
    public SocialFriendItemView getView(int position, View convertView, ViewGroup parent)
    {
        SocialFriendItemView itemView = super.getView(position, convertView, parent);
        if (itemView instanceof SocialFriendUserView)
        {
            ((SocialFriendUserView) itemView).setOnElementClickedListener(createUserClickedListener());
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

    protected void handleCheckBoxEvent(@NotNull UserFriendsDTO userFriendsDTO)
    {
        SocialFriendUserView.OnElementClickListener listenerCopy = elementClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCheckBoxClick(userFriendsDTO);
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
            handleCheckBoxEvent(userFriendsDTO);
        }
    }

    public void setItemsToShow(List<SocialFriendListItemDTO> showItems)
    {
        super.clear();
        super.addAll(showItems);
    }

    public Filter getFilter()
    {
        if (filterToUse == null)
        {
            filterToUse = new NameFilter();
        }
        return filterToUse;
    }

    class NameFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            FilterResults filterResults = new FilterResults();
            mFilteredArrayList = new ArrayList<>();
            for (Iterator<SocialFriendListItemDTO> iterator = mArrayList.iterator(); iterator.hasNext();)
            {
                SocialFriendListItemDTO dto = iterator.next();
                if (dto.toString().contains(charSequence))
                {
                    mFilteredArrayList.add(dto);
                }
            }
            filterResults.values = mFilteredArrayList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence arg0, FilterResults results)
        {
            setItemsToShow((List<SocialFriendListItemDTO>) results.values);
            notifyDataSetChanged();
        }
    }
}
