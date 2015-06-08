package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import java.util.Comparator;

public class UserFollowerDTOSetAdapter extends ViewDTOSetAdapter<FollowerListItemView.DTO, FollowerListItemView>
{
    //<editor-fold desc="Constructors">
    public UserFollowerDTOSetAdapter(@NonNull Context context)
    {
        super(context,
                new Comparator<FollowerListItemView.DTO>()
                {
                    @Override public int compare(FollowerListItemView.DTO lhs, FollowerListItemView.DTO rhs)
                    {
                        return Integer.valueOf(lhs.userFollowerDTO.id).compareTo(rhs.userFollowerDTO.id);
                    }
                });
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
        return getItem(position).userFollowerDTO.id;
    }
}
