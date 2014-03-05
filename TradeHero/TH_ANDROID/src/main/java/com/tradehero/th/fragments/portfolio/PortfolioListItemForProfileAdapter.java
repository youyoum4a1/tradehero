package com.tradehero.th.fragments.portfolio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.timeline.TimelineProfileClickListener;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by xavier on 3/5/14.
 */
public class PortfolioListItemForProfileAdapter extends PortfolioListItemAdapter
    implements PullToRefreshListView.OnRefreshListener<StickyListHeadersListView>,
        StickyListHeadersAdapter
{
    public static final String TAG = PortfolioListItemForProfileAdapter.class.getSimpleName();
    private PortfolioListRefreshRequestListener portfolioListRefreshRequestListener;
    private TimelineProfileClickListener profileClickListener;

    public PortfolioListItemForProfileAdapter(Context context, LayoutInflater inflater, int portfolioLayoutResourceId, int otherHeaderResId)
    {
        super(context, inflater, portfolioLayoutResourceId, otherHeaderResId);
    }

    public void setProfileClickListener(TimelineProfileClickListener profileClickListener)
    {
        this.profileClickListener = profileClickListener;
    }

    public void setPortfolioListRefreshRequestListener(PortfolioListRefreshRequestListener portfolioListRefreshRequestListener)
    {
        this.portfolioListRefreshRequestListener = portfolioListRefreshRequestListener;
    }

    protected void notifyRefreshRequested()
    {
        PortfolioListRefreshRequestListener listenerCopy = portfolioListRefreshRequestListener;
        if (listenerCopy != null)
        {
            listenerCopy.onPortfolioRefreshRequested();
        }
    }

    @Override public void onRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView)
    {
        switch (refreshView.getCurrentMode())
        {
            case PULL_FROM_START:
            case PULL_FROM_END:
                notifyRefreshRequested();
                break;
        }
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.user_profile_detail_bottom_buttons_2_0, parent, false);
            mapHeaderButtons(convertView);
        }

        return convertView;
    }

    private void mapHeaderButtons(View view)
    {
        if (view != null)
        {
            View button = view.findViewById(R.id.btn_profile_timeline);
            if (button != null)
            {
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View view)
                    {
                        if (profileClickListener != null)
                        {
                            profileClickListener.onTimelineRequested();
                        }
                    }
                });
            }
            button = view.findViewById(R.id.btn_profile_portfolios);
            if (button != null)
            {
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View view)
                    {
                        if (profileClickListener != null)
                        {
                            profileClickListener.onPortfolioListRequested();
                        }
                    }
                });
            }
            button = view.findViewById(R.id.btn_profile_stats);
            if (button != null)
            {
                button.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View view)
                    {
                        if (profileClickListener != null)
                        {
                            profileClickListener.onStatsRequested();
                        }
                    }
                });
            }
        }
    }

    @Override public long getHeaderId(int position)
    {
        return 0;
    }

    public static interface PortfolioListRefreshRequestListener
    {
        void onPortfolioRefreshRequested();
    }
}
