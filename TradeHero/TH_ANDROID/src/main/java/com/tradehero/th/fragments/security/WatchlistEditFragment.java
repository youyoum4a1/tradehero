package com.tradehero.th.fragments.security;

import android.app.ProgressDialog;
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
import com.localytics.android.LocalyticsSession;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.LocalyticsConstants;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 12/3/13 Time: 4:05 PM Copyright (c) TradeHero */
public class WatchlistEditFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = WatchlistEditFragment.class.getName() + ".securityKeyId";
    public static final String BUNDLE_KEY_TITLE = WatchlistEditFragment.class.getName() + ".title";
    public static final String BUNDLE_KEY_RETURN_FRAGMENT = WatchlistEditFragment.class.getName() + ".returnFragment";

    private ImageView securityLogo;
    private TextView securityTitle;
    private TextView securityDesc;
    private EditText watchPrice;
    private EditText watchQuantity;
    private SecurityId securityKeyId;
    private Button watchAction;
    private ProgressDialog progressBar;

    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> compactCacheFetchTask;
    private DTOCache.Listener<SecurityId, SecurityCompactDTO> compactCacheListener;

    private THCallback<WatchlistPositionDTO> watchlistUpdateCallback;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @Inject Lazy<WatchlistService> watchlistService;
    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createWatchlistUpdateCallback();
    }

    private void createWatchlistUpdateCallback()
    {
        watchlistUpdateCallback = new THCallback<WatchlistPositionDTO>()
        {
            @Override protected void finish()
            {
                ProgressDialog progressBarCopy = progressBar;
                if (progressBarCopy != null)
                {
                    progressBarCopy.dismiss();
                }
            }

            @Override protected void success(WatchlistPositionDTO watchlistPositionDTO, THResponse response)
            {
                if (watchlistPositionDTO == null)
                {
                    Timber.e(new IllegalArgumentException("watchlistPositionDTO cannot be null for key " + securityKeyId), "watchlistPositionDTO was null for key " + securityKeyId);
                }
                else if (watchlistPositionDTO.securityDTO == null)
                {
                    Timber.e(new IllegalArgumentException("watchlistPositionDTO.securityDTO cannot be null for key " + securityKeyId), "watchlistPositionDTO.securityDTO was null for key " + securityKeyId);
                }
                else
                {
                    SecurityId securityId = watchlistPositionDTO.securityDTO.getSecurityId();
                    watchlistPositionCache.get().put(securityId, watchlistPositionDTO);
                    if (isResumed())
                    {
                        SecurityIdList currentUserWatchlistSecurities =
                                userWatchlistPositionCache.get().get(currentUserId.toUserBaseKey());
                        if (currentUserWatchlistSecurities != null && !currentUserWatchlistSecurities.contains(securityId))
                        {
                            currentUserWatchlistSecurities.add(watchlistPositionDTO.securityDTO.getSecurityId());
                        }
                        Bundle args = getArguments();
                        if (args != null)
                        {
                            String returnFragment = args.getString(BUNDLE_KEY_RETURN_FRAGMENT);
                            if (returnFragment != null)
                            {
                                getNavigator().popFragment(returnFragment);
                                return;
                            }
                        }
                        getNavigator().popFragment();
                    }
                }
            }

            @Override protected void failure(THException ex)
            {
                Timber.e("Failed to update watchlist position", ex);
                THToast.show(ex);
            }
        };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.edit_watchlist_item_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        securityLogo = (ImageView) view.findViewById(R.id.edit_watchlist_item_security_logo);
        securityTitle = (TextView) view.findViewById(R.id.edit_watchlist_item_security_name);
        securityDesc = (TextView) view.findViewById(R.id.edit_watchlist_item_security_desc);

        watchPrice = (EditText) view.findViewById(R.id.edit_watchlist_item_security_price);
        watchQuantity = (EditText) view.findViewById(R.id.edit_watchlist_item_security_quantity);

        watchAction = (Button) view.findViewById(R.id.edit_watchlist_item_done);
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
                DeviceUtil.dismissKeyboard(getActivity(), getView());
                handleWatchButtonClicked();
            }
        };
    }

    private void handleWatchButtonClicked()
    {
        if (progressBar != null)
        {
            progressBar.show();
        }
        else
        {
            progressBar = ProgressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.watchlist_updating);
        }
        try
        {
            double price = Double.parseDouble(watchPrice.getText().toString());
            int quantity = Integer.parseInt(watchQuantity.getText().toString());
            if (quantity == 0)
            {
                 throw new Exception(getString(R.string.watchlist_quantity_should_not_be_zero));
            }
            // add new watchlist
            SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityKeyId);
            if (securityCompactDTO != null)
            {
                WatchlistPositionFormDTO watchPositionItemForm = new WatchlistPositionFormDTO(securityCompactDTO.id, price, quantity);

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
        catch (NumberFormatException ex)
        {
            THToast.show(getString(R.string.wrong_number_format));
            Timber.e("Parsing error", ex);
            progressBar.dismiss();
        }
        catch (Exception ex)
        {
            THToast.show(ex.getMessage());
            progressBar.dismiss();
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        Bundle argument = getArguments();

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);

        String title = argument.getString(BUNDLE_KEY_TITLE);
        if (title != null && !title.isEmpty())
        {
            setActionBarTitle(title);
        }
    }

    private void setActionBarTitle(String title)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(title);
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        linkWith(new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE)), true);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        progressBar = null;
        watchlistUpdateCallback = null;

        super.onDestroy();
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityKeyId = securityId;

        if (securityId != null)
        {
            if (watchlistPositionCache.get().get(securityId) != null)
            {
                setActionBarTitle(getString(R.string.watchlist_edit_title));
                localyticsSession.tagEvent(LocalyticsConstants.Watchlist_Edit);
            }
            else
            {
                setActionBarTitle(getString(R.string.watchlist_add_title));
                localyticsSession.tagEvent(LocalyticsConstants.Watchlist_Add);
            }

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
            securityTitle.setText(String.format("%s:%s", securityKeyId.exchange, securityKeyId.securitySymbol));
        }
    }

    private void querySecurity(SecurityId securityId, final boolean andDisplay)
    {
        if (progressBar != null)
        {
            progressBar.show();
        }
        else
        {
            progressBar = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait), getString(R.string.loading_loading), true);
        }

        compactCacheListener = new DTOCache.Listener<SecurityId, SecurityCompactDTO>()
        {
            @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value, boolean fromCache)
            {
                if (progressBar != null)
                {
                    progressBar.dismiss();
                }
                linkWith(value, andDisplay);
            }

            @Override public void onErrorThrown(SecurityId key, Throwable error)
            {
                if (progressBar != null)
                {
                    progressBar.dismiss();
                }
                THToast.show(R.string.error_fetch_security_info);
                Timber.e("Failed to fetch SecurityCompact for %s", key, error);
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
                if (securityCompactDTO.imageBlobUrl != null)
                {
                    picasso.get()
                            .load(securityCompactDTO.imageBlobUrl)
                            .transform(new WhiteToTransparentTransformation())
                            .into(securityLogo);
                }
                else
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
            }

            WatchlistPositionDTO watchListItem = watchlistPositionCache.get().get(securityCompactDTO.getSecurityId());
            if (watchPrice != null)
            {
                watchPrice.setText(
                        watchListItem != null ?
                                "" + watchListItem.watchlistPrice :
                                securityCompactDTO.lastPrice != null ? securityCompactDTO.lastPrice.toString() : "");
            }

            if (watchListItem != null)
            {
                watchQuantity.setText("" + (watchListItem.shares == null ? 1 : watchListItem.shares));
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
