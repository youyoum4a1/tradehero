package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.portfolio.PortfolioListView;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:47 AM To change this template use File | Settings | File Templates. */
public class PortfolioListFragment extends DashboardFragment
{
    public static final String TAG = PortfolioListFragment.class.getName();

    private PortfolioListView ownPortfolios;
    private PortfolioListView otherPortfolios;

    @Inject Lazy<PortfolioService> portfolioService;
    private UserBaseDTO userBaseDTO;
    private OwnedPortfolioId ownedPortfolioId;
    private PortfolioCompactDTO portfolioCompactDTO;
    private PortfolioDTO portfolioDTO;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        view = inflater.inflate(R.layout.fragment_portfolios_list, container, false);
        initViews(view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            OwnedPortfolioId ownedPortfolioId = new OwnedPortfolioId(args);
            if (ownedPortfolioId != null)
            {
                linkWith(ownedPortfolioId, true);
            }
        }
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            ownPortfolios = (PortfolioListView) view.findViewById(R.id.own_portfolios_list);
            otherPortfolios = (PortfolioListView) view.findViewById(R.id.other_portfolios_list);
        }
    }

    public void linkWith(OwnedPortfolioId ownedPortfolioId, boolean andDisplay)
    {
        this.ownedPortfolioId = ownedPortfolioId;
        if (andDisplay)
        {
            display(); // TODO slim down
        }
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            display(); // TODO slim down
        }
    }

    public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        this.portfolioDTO = portfolioDTO;
        if (andDisplay)
        {
            display(); // TODO slim down
        }
    }

    public void display()
    {
        // TODO
    }

    public void displayOwnPortfolios()
    {
        if (ownPortfolios != null)
        {
            // TODO
        }
    }
}
