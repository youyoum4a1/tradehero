
package com.tradehero.th.fragments.trending;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.users.UserBaseKey;

public class PeopleItemViewAdapter extends ArrayDTOAdapterNew<UserBaseKey, SearchPeopleItemView>
{
    //<editor-fold desc="Constructors">
    public PeopleItemViewAdapter(Context context, int peopleItemLayoutResId)
    {
        super(context, peopleItemLayoutResId);
    }
    //</editor-fold>
}