package com.tradehero.th.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.tradehero.th.adapters.ExpandableItem;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/24/14 Time: 12:57 PM Copyright (c) TradeHero
 */
public class SingleExpandingListViewListener extends BaseExpandingListViewListener
{
    @Inject public SingleExpandingListViewListener()
    {
        super();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // collapse all visible item within the view
        for (int i = parent.getFirstVisiblePosition(); i <= parent.getLastVisiblePosition(); ++i)
        {
            View child = parent.getChildAt(i);
            Object o = parent.getItemAtPosition(i);
            if (o != null  && o instanceof ExpandableItem)
            {
                ((ExpandableItem) o).setExpanded(false);
                collapseView(child);
            }
        }

        super.onItemClick(parent, view, position, id);
    }
}
