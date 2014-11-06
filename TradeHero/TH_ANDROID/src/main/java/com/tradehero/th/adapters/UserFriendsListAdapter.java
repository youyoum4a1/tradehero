package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserFriendsListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;
    private Context context;
    private LayoutInflater inflater;
    private List<UserProfileCompactDTO> userProfileCompactDTOs;

    public int friendsType = UserFriendsListFragment.TYPE_FRIENDS_FOLLOWS;

    public UserFriendsListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(List<UserProfileCompactDTO> list)
    {
        this.userProfileCompactDTOs = list;
    }

    public void addListData(List<UserProfileCompactDTO> list)
    {
        if (userProfileCompactDTOs == null) userProfileCompactDTOs = new ArrayList<>();
        userProfileCompactDTOs.addAll(list);
    }

    public void setFriendsType(int type)
    {
        this.friendsType = type;
    }

    @Override public int getCount()
    {
        return userProfileCompactDTOs == null ? 0 : userProfileCompactDTOs.size();
    }

    @Override public Object getItem(int i)
    {
        return userProfileCompactDTOs == null ? null : userProfileCompactDTOs.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        UserProfileCompactDTO item = (UserProfileCompactDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.user_friends_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.imgUserHead = (ImageView) convertView.findViewById(R.id.imgUserHead);
                holder.imgUserName = (TextView) convertView.findViewById(R.id.tvUserName);
                holder.tvUserExtraTitle = (TextView) convertView.findViewById(R.id.tvUserExtraTitle);
                holder.tvUserExtraValue = (TextView) convertView.findViewById(R.id.tvUserExtraValue);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            picasso.get()
                    .load(item.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(holder.imgUserHead);

            holder.imgUserName.setText(item.getDisplayName());

            double roi = 0;
            if (item instanceof UserFollowerDTO)
            {
                roi = ((UserFollowerDTO) item).roiSinceInception;
            }
            else
            {
                if (item != null && item.roiSinceInception != null)
                {
                    roi = item.roiSinceInception;
                }
            }

            THSignedNumber thRoiSinceInception = THSignedPercentage.builder(roi * 100)
                    .build();
            holder.tvUserExtraValue.setText(thRoiSinceInception.toString());
            holder.tvUserExtraValue.setTextColor(
                    context.getResources().getColor(thRoiSinceInception.getColorResId()));

            if (friendsType == UserFriendsListFragment.TYPE_FRIENDS_FOLLOWS)
            {
            }
            else if (friendsType == UserFriendsListFragment.TYPE_FRIENDS_HERO)
            {
            }
        }
        return convertView;
    }

    static class ViewHolder
    {
        public ImageView imgUserHead = null;
        public TextView imgUserName = null;
        public TextView tvUserExtraTitle = null;
        public TextView tvUserExtraValue = null;
    }
}
