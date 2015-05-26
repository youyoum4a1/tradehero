package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsWeiboDTO;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangyx on 5/21/15.
 */
public class RecyclerSocialFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private WeakReference<Activity> activityReference;
    private List<SocialFriendListItemDTO> items;
    private OnElementClickListener onElementClickListener;
    private NameFilter nameFilter;

    public RecyclerSocialFriendsAdapter(Activity activity, List<SocialFriendListItemDTO> items) {
        activityReference = new WeakReference<>(activity);
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Activity activity = activityReference.get();
        if (activity == null) {
            return null;
        }
        View view;
        switch (viewType) {
            case TYPE_HEADER:
                view = activity.getLayoutInflater().inflate(R.layout.social_friends_item_header, parent, false);
                return new HeaderViewHolder(view);
            case TYPE_ITEM:
            default:
                view = activity.getLayoutInflater().inflate(R.layout.social_friends_item, parent, false);
                return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        SocialFriendListItemDTO item = items.get(position);
        switch (viewType) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.headLine.setText(((SocialFriendListItemHeaderDTO) item).header);
                break;
            case TYPE_ITEM:
                SocialFriendListItemUserDTO userItem = (SocialFriendListItemUserDTO) item;
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                ImageLoader.getInstance().displayImage(userItem.userFriendsDTO.getProfilePictureURL(),
                        itemViewHolder.friendLogo,
                        UniversalImageLoader.getAvatarImageLoaderOptions(false));
                itemViewHolder.friendTitle.setText(userItem.userFriendsDTO.name);
                displayActionButton(itemViewHolder, userItem);
                break;
        }
    }

    private void displayActionButton(final ItemViewHolder itemViewHolder, final SocialFriendListItemUserDTO userItem) {
        if (userItem.userFriendsDTO.isTradeHeroUser()) {
            itemViewHolder.actionBtn.setVisibility(View.VISIBLE);
            itemViewHolder.actionBtn.setText(R.string.follow);
            itemViewHolder.actionBtn.setBackgroundResource(R.drawable.basic_green_selector_round_corner);
            itemViewHolder.actionBtn.setEnabled(true);
            itemViewHolder.actionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callOnElementClickListener(true, userItem.userFriendsDTO);
                }
            });
            itemViewHolder.actionCb.setVisibility(View.GONE);
            itemViewHolder.itemView.setOnClickListener(null);

        } else {
            itemViewHolder.actionCb.setBackgroundResource(userItem.isSelected ?
                    R.drawable.register_duihao : R.drawable.register_duihao_cancel);
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userItem.isSelected = !userItem.isSelected;
                    itemViewHolder.actionCb.setBackgroundResource(userItem.isSelected ?
                            R.drawable.register_duihao : R.drawable.register_duihao_cancel);
                    callOnElementClickListener(false, userItem.userFriendsDTO);
                }
            });
            if (isNeedCheckBoxShow(userItem)) {
                itemViewHolder.actionBtn.setVisibility(View.GONE);
                itemViewHolder.actionCb.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.actionBtn.setVisibility(View.VISIBLE);
                itemViewHolder.actionCb.setVisibility(View.GONE);
            }
        }
    }

    private void callOnElementClickListener(boolean isFollow, UserFriendsDTO dto) {
        if (onElementClickListener == null) {
            return;
        }
        if (isFollow) {
            onElementClickListener.onFollowButtonClick(dto);
        } else {
            onElementClickListener.onCheckBoxClick(dto);
        }
    }

    private boolean isNeedCheckBoxShow(SocialFriendListItemUserDTO userItem) {
        return userItem.userFriendsDTO instanceof UserFriendsWeiboDTO
                && (!userItem.userFriendsDTO.isTradeHeroUser());
    }

    @Override
    public int getItemViewType(int position) {
        if (items == null) {
            return TYPE_ITEM;
        }
        SocialFriendListItemDTO item = items.get(position);
        if (item instanceof SocialFriendListItemHeaderDTO) {
            return TYPE_HEADER;
        }

        if (item instanceof SocialFriendListItemUserDTO) {
            return TYPE_ITEM;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }

    public void setOnElementClickListener(OnElementClickListener listener) {
        onElementClickListener = listener;
    }

    public void clear() {
        if (items != null) {
            items.clear();
        }
    }

    public void setItems(List<SocialFriendListItemDTO> values) {
        items = values;
        notifyDataSetChanged();
    }

    public void filter(String pattern) {
        if (nameFilter == null) {
            nameFilter = new NameFilter(items);
        }
        nameFilter.filter(pattern);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView friendLogo;
        TextView friendTitle;
        TextView actionBtn;
        ImageView actionCb;

        public ItemViewHolder(View view) {
            super(view);
            friendLogo = (ImageView) view.findViewById(R.id.social_item_logo);
            friendTitle = (TextView) view.findViewById(R.id.social_item_title);
            actionBtn = (TextView) view.findViewById(R.id.social_item_action_btn);
            actionCb = (ImageView) view.findViewById(R.id.social_item_action_cb);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headLine;

        public HeaderViewHolder(View view) {
            super(view);
            headLine = (TextView) view.findViewById(R.id.social_friend_headline);
        }
    }

    class NameFilter extends Filter
    {
        private List<SocialFriendListItemDTO> originalItems;
        public NameFilter(List<SocialFriendListItemDTO> items) {
            originalItems = items;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence)
        {
            FilterResults filterResults = new FilterResults();
            List<SocialFriendListItemDTO> mFilteredArrayList = new ArrayList<>();
            List<SocialFriendListItemDTO> copyList = new ArrayList<>(originalItems);
            int sizeList = copyList.size();
            for (int i = 0; i < sizeList; i++)
            {
                SocialFriendListItemDTO dto = copyList.get(i);
                if (dto.toString().toLowerCase().contains(charSequence.toString().toLowerCase())
                        || (dto instanceof SocialFriendListItemHeaderDTO))
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
            setItems((List<SocialFriendListItemDTO>) results.values);
        }
    }



    public interface OnElementClickListener {
        void onFollowButtonClick(@NotNull UserFriendsDTO userFriendsDTO);

        void onCheckBoxClick(@NotNull UserFriendsDTO userFriendsDTO);
    }
}
