package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 21/10/13
 * Header displayed on aPortfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    @Inject Lazy<PortfolioCompactCache> portfolioCache;
    private PortfolioCompactDTO portfolio;

    private TextView totalValueTextView;
    private TextView cashValueTextView;
    private DTOCache.Listener<PortfolioId, PortfolioCompactDTO> portfolioCacheListener;
    private DTOCache.GetOrFetchTask<PortfolioId, PortfolioCompactDTO> fetchPortfolioTask;

    //<editor-fold desc="Description">
    public CurrentUserPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public CurrentUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CurrentUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        portfolioCacheListener = new DTOCache.Listener<PortfolioId, PortfolioCompactDTO>()
        {
            @Override public void onDTOReceived(PortfolioId key, PortfolioCompactDTO value)
            {
                display(value);
            }

            @Override public void onErrorThrown(PortfolioId key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };

        if (fetchPortfolioTask != null)
        {
            fetchPortfolioTask.setListener(null);
        }
    }

    private void display(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolio = portfolioCompactDTO;

        if (portfolio != null)
        {
            if (totalValueTextView != null)
            {
                String valueString = String.format("US$ %,.0f", portfolio.totalValue);
                totalValueTextView.setText(valueString);
            }

            if (cashValueTextView != null)
            {
                String cashString = String.format("US$ %,.0f", portfolio.cashBalance);
                cashValueTextView.setText(cashString);
            }
        }
    }

    private void initViews()
    {
        totalValueTextView = (TextView) findViewById(R.id.header_portfolio_total_value);
        cashValueTextView = (TextView) findViewById(R.id.header_portfolio_cash_value);
    }

    @Override public void bindOwnedPortfolioId(OwnedPortfolioId id)
    {
        fetchPortfolioTask = this.portfolioCache.get().getOrFetch(id.getPortfolioId(), false, portfolioCacheListener);
        fetchPortfolioTask.execute();
    }

    @Override public void setFollowRequestedListener(OnFollowRequestedListener followRequestedListener)
    {
        // Nothing to do
    }

    @Override public void setTimelineRequestedListener(OnTimelineRequestedListener timelineRequestedListener)
    {
        // Nothing to do
    }
}
