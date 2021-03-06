package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.social.UserFollowerDTO;
import android.support.annotation.NonNull;

public class UserFollowerDTOSetAdapter extends ViewDTOSetAdapter<UserFollowerDTO, FollowerListItemView>
{
    //<editor-fold desc="Constructors">
    public UserFollowerDTOSetAdapter(@NonNull Context context)
    {
        super(context,
                (lhs, rhs) -> Integer.valueOf(lhs.id).compareTo(rhs.id));
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return R.layout.follower_list_item_revenue;
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).id;
    }
}
