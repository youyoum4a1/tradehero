
package com.tradehero.th.fragments.trending;

import android.content.Context;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.List;

public class PeopleItemViewAdapter extends ArrayDTOAdapterNew<UserBaseKey, SearchPeopleItemView>
{
    private Integer lastPageLoaded;

    //<editor-fold desc="Constructors">
    public PeopleItemViewAdapter(Context context, int peopleItemLayoutResId)
    {
        super(context, peopleItemLayoutResId);
    }
    //</editor-fold>

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public long getItemId(int position)
    {
        return getItem(position).key;
    }

    public Integer getLastPageLoaded()
    {
        return lastPageLoaded;
    }

    public void addPage(int page, List<UserBaseKey> userBaseKeys)
    {
        this.lastPageLoaded = page;
        addAll(userBaseKeys);
    }

    @Override public void clear()
    {
        super.clear();
        this.lastPageLoaded = null;
    }
}