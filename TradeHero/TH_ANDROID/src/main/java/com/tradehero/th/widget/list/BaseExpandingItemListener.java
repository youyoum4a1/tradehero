package com.ayondo.academy.widget.list;

import android.view.View;
import com.ayondo.academy.R;
import com.ayondo.academy.fragments.leaderboard.ExpandingLayout;

public class BaseExpandingItemListener
    implements View.OnClickListener
{
    protected void expandView(View view)
    {
        if (view != null)
        {
            final View expandingLayout = view.findViewById(R.id.expanding_layout);
            if (expandingLayout != null)
            {
                if (expandingLayout instanceof ExpandingLayout)
                {
                    ((ExpandingLayout)expandingLayout).expand(true);
                }
                else
                {
                    expandingLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void collapseView(View view)
    {
        if (view != null)
        {
            final View expandingLayout = view.findViewById(R.id.expanding_layout);
            if (expandingLayout != null)
            {
                if (expandingLayout instanceof ExpandingLayout)
                {
                    ((ExpandingLayout)expandingLayout).expand(false);
                }
                else
                {
                    expandingLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override public void onClick(View view)
    {
        final View expandingLayout = view.findViewById(R.id.expanding_layout);
        if (expandingLayout.getVisibility() == View.GONE)
        {
            expandView(view);
        }
        else if (expandingLayout.getVisibility() == View.VISIBLE)
        {
            collapseView(view);
        }
    }
}
