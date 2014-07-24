package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.AbstractArrayAdapter;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.List;
import timber.log.Timber;

public class SocialFriendsAdapter extends AbstractArrayAdapter<SocialFriendListItemDTO>
{
    private Context mContext;
    private int mLayoutResourceId;
    private SocialFriendItemView.OnElementClickListener elementClickedListener;

    //<editor-fold desc="Constructors">
    public SocialFriendsAdapter(Context context, List<SocialFriendListItemDTO> objects, int layoutResourceId)
    {
        super(context, 0, objects);
        this.mContext = context;
        this.mLayoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override
    public SocialFriendItemView getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(getViewResId(position), parent, false);
        }
        SocialFriendItemView dtoView = (SocialFriendItemView) convertView;
        SocialFriendListItemDTO item = getItem(position);
        dtoView.display(item);
        dtoView.setOnElementClickedListener(elementClickedListener);
        return dtoView;
    }

    @Override
    protected String getItemNameForFilter(SocialFriendListItemDTO item)
    {
        if (item instanceof SocialFriendListItemUserDTO)
        {
            return ((SocialFriendListItemUserDTO) item).userFriendsDTO.name;
        }
        else if(item instanceof SocialFriendListItemHeaderDTO)
        {
            return ((SocialFriendListItemHeaderDTO) item).name;
        }
        throw new IllegalArgumentException("Unhandled element type");
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return getItem(position) instanceof SocialFriendListItemUserDTO;
    }

    protected int getViewResId(int position)
    {
        return mLayoutResourceId;
    }

    public void setOnElementClickedListener(
            SocialFriendItemView.OnElementClickListener elementClickedListener)
    {
        this.elementClickedListener = elementClickedListener;
    }

    protected void handleFollowEvent(UserFriendsDTO userFriendsDTO)
    {
    }

    protected void handleInviteEvent(UserFriendsDTO userFriendsDTO)
    {
    }

    protected SocialFriendItemView.OnElementClickListener createUserClickedListener()
    {
        return new SocialElementClickListener();
    }

    protected class SocialElementClickListener implements SocialFriendItemView.OnElementClickListener
    {
        @Override
        public void onFollowButtonClick(UserFriendsDTO userFriendsDTO)
        {
            handleFollowEvent(userFriendsDTO);
        }

        @Override
        public void onInviteButtonClick(UserFriendsDTO userFriendsDTO)
        {
            handleInviteEvent(userFriendsDTO);
        }

        @Override
        public void onCheckBoxClick(UserFriendsDTO userFriendsDTO)
        {
            Timber.d("onCheckBoxClicked " + userFriendsDTO);
        }
    }
}
