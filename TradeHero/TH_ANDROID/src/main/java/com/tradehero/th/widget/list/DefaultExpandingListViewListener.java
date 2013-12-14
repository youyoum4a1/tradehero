package com.tradehero.th.widget.list;

import android.view.View;
import android.widget.AdapterView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableItem;
import com.tradehero.th.adapters.ExpandableListItem;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/31/13 Time: 4:15 PM Copyright (c) TradeHero */
public class DefaultExpandingListViewListener implements ExpandingListView.ExpandingListItemListener
{
    @Inject
    public DefaultExpandingListViewListener()
    {
        super();
    }

    private void expandView(View view)
    {
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.VISIBLE);
    }

    private void collapseView(View view)
    {
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        expandingLayout.setVisibility(View.GONE);
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

    @Override public void onItemDidExpand(AdapterView<?> parent, View view, int position, long id)
    {
        // nothing
    }

    @Override public void onItemDidCollapse(AdapterView<?> parent, View view, int position, long id)
    {
        // nothing
    }
}
