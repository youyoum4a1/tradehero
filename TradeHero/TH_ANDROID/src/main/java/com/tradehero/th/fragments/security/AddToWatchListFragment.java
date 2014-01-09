package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 4:05 PM Copyright (c) TradeHero */
public class AddToWatchListFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = AddToWatchListFragment.class.getName() + ".securityKeyId";
    private static final String TAG = DashboardFragment.class.getName();

    private ImageView securityLogo;
    private TextView securityTitle;
    private TextView securityDesc;
    private EditText watchPrice;
    private EditText watchQuantity;
    private SecurityId securityKeyId;
    private Button watchAction;
    private ProgressBar progressBar;

    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> compactCacheFetchTask;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<WatchlistService> watchlistService;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.add_to_watch_list_layout, container, false);
        initViews(view);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
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
                handleWatchButtonClicked();
            }
        };
    }

    private void handleWatchButtonClicked()
    {
        progressBar.setVisibility(View.VISIBLE);
        try
        {
            double price = Double.parseDouble(watchPrice.getText().toString());
            int quantity = Integer.parseInt(watchQuantity.getText().toString());
            // add new watchlist
            SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityKeyId);
            if (securityCompactDTO != null)
            {
                WatchlistPositionFormDTO watchPositionItemForm = new WatchlistPositionFormDTO(securityCompactDTO.id, price, quantity);
                THCallback<WatchlistPositionDTO> watchlistUpdateCallback = new THCallback<WatchlistPositionDTO>()
                {
                    @Override protected void finish()
                    {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override protected void success(WatchlistPositionDTO watchlistPositionDTO, THResponse response)
                    {
                        // update cache
                        watchlistPositionCache.get().put(watchlistPositionDTO.securityDTO.getSecurityId(), watchlistPositionDTO);
                        getNavigator().popFragment();
                    }

                    @Override protected void failure(THException ex)
                    {
                        THToast.show(ex);
                    }
                };

                WatchlistPositionDTO existingWatchlistPosition = watchlistPositionCache.get().get(securityCompactDTO.getSecurityId());
                if (existingWatchlistPosition != null)
                {
                    watchlistService.get().updateWatchlistEntry(existingWatchlistPosition.id, watchPositionItemForm, watchlistUpdateCallback);
                }
                else
                {
                    watchlistService.get().createWatchlistEntry(watchPositionItemForm, watchlistUpdateCallback);
                }
            }
        }
        catch (Exception ex)
        {
            // most likely number exception when parsing text to number
            THLog.e(TAG, "Parsing error", ex);
        }
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
        this.securityKeyId = securityId;

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
            securityTitle.setText(String.format("%s:%s", securityKeyId.securitySymbol, securityKeyId.exchange));
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
                    securityLogo.setVisibility(View.VISIBLE);
                }
                else
                {
                    securityLogo.setVisibility(View.GONE);
                }
            }

            WatchlistPositionDTO watchListItem = watchlistPositionCache.get().get(securityCompactDTO.getSecurityId());
            if (watchPrice != null)
            {
                watchPrice.setText(
                        watchListItem != null ?
                                "" + watchListItem.watchlistPrice :
                                securityCompactDTO.lastPrice != null ? securityCompactDTO.lastPrice.toString() : "");
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
