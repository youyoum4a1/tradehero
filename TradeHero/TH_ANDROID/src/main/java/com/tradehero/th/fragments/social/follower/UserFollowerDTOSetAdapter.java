package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.support.annotation.LayoutRes;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.social.UserFollowerDTO;
import org.jetbrains.annotations.NotNull;

public class UserFollowerDTOSetAdapter extends ViewDTOSetAdapter<UserFollowerDTO, FollowerListItemView>
{
    //<editor-fold desc="Constructors">
    public UserFollowerDTOSetAdapter(@NotNull Context context)
    {
        super(context,
                (lhs, rhs) -> Integer.valueOf(lhs.id).compareTo(rhs.id));
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return R.layout.follower_list_item_revenue;
    }
}
