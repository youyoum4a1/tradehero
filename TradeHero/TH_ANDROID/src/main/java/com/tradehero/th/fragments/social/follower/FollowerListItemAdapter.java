package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;

public class FollowerListItemAdapter extends BaseAdapter
{
    @NonNull protected final Context context;
    protected final LayoutInflater inflater;
    @LayoutRes protected final int followerResId;
    protected FollowerSummaryDTO followerSummaryDTO;

    //<editor-fold desc="Constructors">
    public FollowerListItemAdapter(@NonNull Context context, @LayoutRes int followerResId)
    {
        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.followerResId = followerResId;
    }
    //</editor-fold>

    public void setFollowerSummaryDTO(FollowerSummaryDTO followerSummaryDTO)
    {
        this.followerSummaryDTO = followerSummaryDTO;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getViewTypeCount()
    {
        return 1;
    }

    public int getFollowerRealCount()
    {
        return (followerSummaryDTO == null || followerSummaryDTO.userFollowers == null) ?
                0 :
                followerSummaryDTO.userFollowers.size();
    }

    @Override public int getCount()
    {
        return getFollowerRealCount();
    }

    @Override public long getItemId(int position)
    {
        Object item = getItem(position);
        return item == null ? 0 : item.hashCode();
    }

    @Override public Object getItem(int position)
    {
        return getFollowerForPosition(position);
    }

    public UserFollowerDTO getFollowerForPosition(int position)
    {
        return (followerSummaryDTO == null || followerSummaryDTO.userFollowers == null) ?
                null :
                followerSummaryDTO.userFollowers.get(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (!(convertView instanceof FollowerListItemView))
        {
            convertView = inflater.inflate(followerResId, parent, false);
        }
        ((FollowerListItemView) convertView).display((UserFollowerDTO) getItem(position));
        return convertView;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return true;
    }
}
