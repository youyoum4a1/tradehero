package com.tradehero.th.widget.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;

/**
 * Listens for item clicks and expands or collapses the selected view depending on
 * its current state.
 * Created by julien on 24/10/13
 */
public class ExpandingListView extends ListView
{
    private ExpandingListItemListener expandingListItemListener;

    //<editor-fold desc="Constructors">
    public ExpandingListView(Context context)
    {
        super(context);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ExpandingListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    private void init()
    {
        super.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent, View view, int position, long id)
            {
                if (expandingListItemListener != null)
                    expandingListItemListener.onItemClick(parent, view, position, id);

                Object o = getItemAtPosition(getPositionForView(view));
                if (o == null || !(o instanceof ExpandableListItem))
                    return;

                ExpandableListItem viewObject = (ExpandableListItem)o;
                if (!viewObject.isExpanded())
                {
                    expandView(view);
                    if (expandingListItemListener != null)
                        expandingListItemListener.onItemDidExpand(parent, view, position, id);
                }
                else
                {
                    collapseView(view);
                    if (expandingListItemListener != null)
                        expandingListItemListener.onItemDidCollapse(parent, view, position, id);
                }
            }
        });
    }

    @Override public void setOnItemClickListener(OnItemClickListener listener)
    {
        throw new IllegalArgumentException("You are trying to override the default listener");
    }

    private void expandView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem) getItemAtPosition(getPositionForView(view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(View.VISIBLE);
            viewObject.setExpanded(true);
        }
    }

    private void collapseView(View view)
    {
        ExpandableListItem viewObject = (ExpandableListItem) getItemAtPosition(getPositionForView(view));

        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        if (expandingLayout != null)
        {
            expandingLayout.setVisibility(View.GONE);
            viewObject.setExpanded(false);
        }
    }

    public ExpandingListItemListener getExpandingListItemListener()
    {
        return expandingListItemListener;
    }

    public void setExpandingListItemListener(ExpandingListItemListener expandingListItemListener)
    {
        this.expandingListItemListener = expandingListItemListener;
    }

    public static interface ExpandingListItemListener
    {
        public void onItemClick (AdapterView<?> parent, View view, int position, long id);
        public void onItemDidExpand(AdapterView<?> parent, View view, int position, long id);
        public void onItemDidCollapse(AdapterView<?> parent, View view, int position, long id);
    }
}
