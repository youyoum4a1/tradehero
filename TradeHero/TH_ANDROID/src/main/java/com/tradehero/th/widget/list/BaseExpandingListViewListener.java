package com.tradehero.th.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.tradehero.th.adapters.ExpandableItem;
import javax.inject.Inject;

public class BaseExpandingListViewListener extends BaseExpandingItemListener
        implements ExpandingListView.ExpandingListItemListener
{
    @Inject
    public BaseExpandingListViewListener()
    {
        super();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object o = parent.getItemAtPosition(parent.getPositionForView(view));
        if (o == null || !(o instanceof ExpandableItem))
        {
            return;
        }
        ExpandableItem viewObject = (ExpandableItem) o;
        if (!viewObject.isExpanded())
        {
            viewObject.setExpanded(true);
            expandView(view);
        }
        else
        {
            viewObject.setExpanded(false);
            collapseView(view);
        }
    }

    @Override public void onItemExpanded(AdapterView<?> parent, View view, int position, long id)
    {
        // nothing
    }

    @Override public void onItemCollapsed(AdapterView<?> parent, View view, int position, long id)
    {
        // nothing
    }
}
