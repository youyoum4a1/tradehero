package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 4:05 PM Copyright (c) TradeHero */
public class AddToWatchListFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = AddToWatchListFragment.class.getName() + ".securityId";
    private static final String TAG = DashboardFragment.class.getName();

    private ImageView securityLogo;
    private TextView securityTitle;
    private TextView securityDesc;
    private EditText watchPrice;
    private EditText watchQuantity;
    private SecurityId securityId;
    private Button watchAction;

    private DTOCache.GetOrFetchTask<SecurityCompactDTO> compactCacheFetchTask;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.add_to_watch_list_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        securityLogo = (ImageView) view.findViewById(R.id.add_to_watch_list_security_logo);
        securityTitle = (TextView) view.findViewById(R.id.add_to_watch_list_security_name);
        securityDesc = (TextView) view.findViewById(R.id.add_to_watch_list_security_desc);

        watchPrice = (EditText) view.findViewById(R.id.add_to_watch_list_security_price);
        watchQuantity = (EditText) view.findViewById(R.id.add_to_watch_list_security_quantity);

        watchAction = (Button) view.findViewById(R.id.add_to_watch_list_done);
        if (watchAction != null)
        {
            watchAction.setOnClickListener(createOnWatchButtonClickedListener());
        }
    }

    private View.OnClickListener createOnWatchButtonClickedListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {

            }
        };
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.add_to_watch_list));
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle), true);
            }
        }
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (securityId != null)
        {
            querySecurity(securityId, andDisplay);
        }

        if (andDisplay)
        {
            displaySecurityTitle();
        }
    }

    private void displaySecurityTitle()
    {
        if (securityTitle != null)
        {
            securityTitle.setText(String.format("%s:%s", securityId.securitySymbol, securityId.exchange));
        }
    }

    private void querySecurity(SecurityId securityId, final boolean andDisplay)
    {
        DTOCache.Listener<SecurityId, SecurityCompactDTO> compactCacheListener = new DTOCache.Listener<SecurityId, SecurityCompactDTO>()
        {
            @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value)
            {
                linkWith(value, andDisplay);
            }

            @Override public void onErrorThrown(SecurityId key, Throwable error)
            {
                THToast.show(R.string.error_fetch_security_info);
                THLog.e(TAG, "Failed to fetch SecurityCompact for " + key, error);
            }
        };

        if (compactCacheFetchTask != null)
        {
            compactCacheFetchTask.cancel(true);
        }
        compactCacheFetchTask = securityCompactCache.get().getOrFetch(securityId, compactCacheListener);
        compactCacheFetchTask.execute();
    }

    private void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            if (securityDesc != null)
            {
                securityDesc.setText(securityCompactDTO.name);
            }

            if (securityLogo != null)
            {
                int exchangeId = securityCompactDTO.getExchangeLogoId();
                if (exchangeId != 0)
                {
                    securityLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
                }
            }

            if (watchPrice != null && securityCompactDTO.lastPrice != null)
            {
                watchPrice.setText(securityCompactDTO.lastPrice.toString());
            }
        }
    }

    //<editor-fold desc="TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
