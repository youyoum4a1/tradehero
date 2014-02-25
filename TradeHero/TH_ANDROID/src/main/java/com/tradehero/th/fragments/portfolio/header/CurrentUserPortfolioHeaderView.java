package com.tradehero.th.fragments.portfolio.header;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by julien on 21/10/13
 * Header displayed on aPortfolio owned by the authenticated user.
 */
public class CurrentUserPortfolioHeaderView extends LinearLayout implements PortfolioHeaderView
{
    private OwnedPortfolioId ownedPortfolioId;

    @Inject PortfolioCompactCache portfolioCompactCache;
    private PortfolioCompactDTO portfolioCompactDTO;

    @Inject PortfolioCache portfolioCache;
    private PortfolioDTO portfolioDTO;

    private TextView totalValueTextView;
    private TextView cashValueTextView;
    private DTOCache.Listener<PortfolioId, PortfolioCompactDTO> portfolioCompactCacheListener;
    private DTOCache.GetOrFetchTask<PortfolioId, PortfolioCompactDTO> fetchPortfolioCompactTask;
    private DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> portfolioCacheListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> fetchPortfolioTask;

    private boolean isAttached = false;

    //<editor-fold desc="Constructors">
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

    private void initViews()
    {
        totalValueTextView = (TextView) findViewById(R.id.header_portfolio_total_value);
        cashValueTextView = (TextView) findViewById(R.id.header_portfolio_cash_value);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        this.isAttached = true;

        portfolioCompactCacheListener = new DTOCache.Listener<PortfolioId, PortfolioCompactDTO>()
        {
            @Override public void onDTOReceived(PortfolioId key, PortfolioCompactDTO value, boolean fromCache)
            {
                display(value);
            }

            @Override public void onErrorThrown(PortfolioId key, Throwable error)
            {
                THToast.show(R.string.error_fetch_portfolio_info);
            }
        };
        portfolioCacheListener = new DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>()
        {
            @Override
            public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value, boolean fromCache)
            {
                display(value);
            }

            @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
            {
                THToast.show(R.string.error_fetch_portfolio_info);
            }
        };

        conditionalStartPortfolioCompactTask();
        conditionalStartPortfolioTask();
    }

    @Override protected void onDetachedFromWindow()
    {
        isAttached = false;
        portfolioCompactDTO = null;
        portfolioDTO = null;

        detachPortfolioCompactTask();
        portfolioCompactCacheListener = null;
        detachPortfolioTask();
        portfolioCacheListener = null;
        super.onDetachedFromWindow();
    }

    protected void detachPortfolioCompactTask()
    {
        if (fetchPortfolioCompactTask != null)
        {
            fetchPortfolioCompactTask.setListener(null);
        }
        fetchPortfolioCompactTask = null;
    }

    protected void detachPortfolioTask()
    {
        if (fetchPortfolioTask != null)
        {
            fetchPortfolioTask.setListener(null);
        }
        fetchPortfolioTask = null;
    }

    @Override public void bindOwnedPortfolioId(OwnedPortfolioId id)
    {
        this.ownedPortfolioId = id;

        detachPortfolioCompactTask();
        fetchPortfolioCompactTask = this.portfolioCompactCache.getOrFetch(id.getPortfolioId(), false, portfolioCompactCacheListener);

        detachPortfolioTask();
        fetchPortfolioTask = this.portfolioCache.getOrFetch(id, false, portfolioCacheListener);

        if (isAttached)
        {
            fetchPortfolioCompactTask.execute();
            fetchPortfolioTask.execute();
        }
    }

    protected void conditionalStartPortfolioCompactTask()
    {
        if (fetchPortfolioCompactTask != null)
        {
            fetchPortfolioCompactTask.setListener(portfolioCompactCacheListener);
            if (fetchPortfolioCompactTask.getStatus() == AsyncTask.Status.PENDING)
            {
                fetchPortfolioCompactTask.execute();
            }
        }
    }

    protected void conditionalStartPortfolioTask()
    {
        if (fetchPortfolioTask != null)
        {
            fetchPortfolioTask.setListener(portfolioCacheListener);
            if (fetchPortfolioTask.getStatus() == AsyncTask.Status.FINISHED)
            {
                fetchPortfolioTask.execute();
            }
        }
    }

    private void display(PortfolioCompactDTO portfolioCompactDTO)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;

        displayTotalValueTextView();
        displayCashValueTextView();
    }

    private void display(PortfolioDTO portfolioDTO)
    {
        this.portfolioDTO = portfolioDTO;

        displayTotalValueTextView();
        displayCashValueTextView();
    }

    private void displayTotalValueTextView()
    {
        if (totalValueTextView != null)
        {
            if (this.portfolioCompactDTO != null && this.portfolioDTO != null)
            {
                String valueString = String.format("%s %,.0f", this.portfolioDTO.getNiceCurrency(), this.portfolioCompactDTO.totalValue);
                totalValueTextView.setText(valueString);
            }
        }
    }

    private void displayCashValueTextView()
    {
        if (cashValueTextView != null)
        {
            if (this.portfolioCompactDTO != null && this.portfolioDTO != null)
            {
                String cashString = String.format("%s %,.0f", this.portfolioDTO.getNiceCurrency(), this.portfolioCompactDTO.cashBalance);
                cashValueTextView.setText(cashString);
            }
        }
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
