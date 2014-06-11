package com.tradehero.th.fragments.security;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import timber.log.Timber;

public class WatchlistEditFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = WatchlistEditFragment.class.getName() + ".securityKeyId";

    private ImageView securityLogo;
    private TextView securityTitle;
    private TextView securityDesc;
    private EditText watchPrice;
    private EditText watchQuantity;
    private SecurityId securityKeyId;
    private TextView watchAction;
    private TextView deleteButton;
    private ProgressDialog progressBar;

    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> securityCompactCacheFetchTask;

    private MiddleCallback<WatchlistPositionDTO> middleCallbackUpdate;
    private MiddleCallback<WatchlistPositionDTO> middleCallbackDelete;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject Lazy<UserWatchlistPositionCache> userWatchlistPositionCache;
    @Inject Lazy<WatchlistServiceWrapper> watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCacheLazy;

    public static void putSecurityId(Bundle args, SecurityId securityId)
    {
        args.putBundle(WatchlistEditFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
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

        watchAction = (TextView) view.findViewById(R.id.edit_watchlist_item_done);
        if (watchAction != null)
        {
            watchAction.setOnClickListener(createOnWatchButtonClickedListener());
        }
        deleteButton = (TextView) view.findViewById(R.id.edit_watchlist_item_delete);
        if (deleteButton != null)
        {
            deleteButton.setOnClickListener(createOnDeleteButtonClickedListener());
        }

        checkDeleteButtonEnable();
    }

    private void checkDeleteButtonEnable()
    {
        WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().get(securityKeyId);
        if (watchlistPositionDTO == null)
        {
            //deleteButton.setEnabled(false);
            Timber.d("checkDeleteButtonEnable watchlistPositionDTO:%s ",watchlistPositionDTO);
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
        showProgressBar();
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
                detachMiddleCallbackUpdate();
                if (existingWatchlistPosition != null)
                {
                    middleCallbackUpdate = watchlistServiceWrapper.get().updateWatchlistEntry(
                            existingWatchlistPosition.getPositionCompactId(),
                            watchPositionItemForm,
                            createWatchlistUpdateCallback());
                }
                else
                {
                    middleCallbackUpdate = watchlistServiceWrapper.get().createWatchlistEntry(
                            watchPositionItemForm,
                            createWatchlistUpdateCallback());
                }
            }
            else
            {
                Timber.e(new Exception("SecurityCompactDTO from cache was null"),"");
                dismissProgress();
            }
        }
        catch (NumberFormatException ex)
        {
            THToast.show(getString(R.string.wrong_number_format));
            Timber.e("Parsing error", ex);
            dismissProgress();
        }
        catch (Exception ex)
        {
            THToast.show(ex.getMessage());
            dismissProgress();
        }
    }

    private void showProgressBar()
    {
        if (progressBar != null)
        {
            progressBar.show();
        }
        else
        {
            progressBar = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.watchlist_updating);
        }
    }

    private View.OnClickListener createOnDeleteButtonClickedListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                DeviceUtil.dismissKeyboard(getActivity(), getView());
                handleButtonDeleteClicked();
            }
        };
    }

    private void handleButtonDeleteClicked()
    {
        WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().get(securityKeyId);
        if (watchlistPositionDTO != null)
        {
            showProgressBar();
            detachMiddleCallbackDelete();
            middleCallbackDelete = watchlistServiceWrapper.get().deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), createWatchlistDeleteCallback());
        }
        else
        {
            THToast.show(R.string.error_fetch_portfolio_watchlist);
        }

    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args.containsKey(BUNDLE_KEY_SECURITY_ID_BUNDLE))
        {
            linkWith(new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE)), true);
        }
    }

    @Override public void onDestroyView()
    {
        detachSecurityCompactFetchTask();
        detachMiddleCallbackUpdate();
        detachMiddleCallbackDelete();
        super.onDestroyView();
    }

    protected void detachSecurityCompactFetchTask()
    {
        if (securityCompactCacheFetchTask != null)
        {
            securityCompactCacheFetchTask.setListener(null);
        }
        securityCompactCacheFetchTask = null;
    }

    protected void detachMiddleCallbackUpdate()
    {
        if (middleCallbackUpdate != null)
        {
            middleCallbackUpdate.setPrimaryCallback(null);
        }
        middleCallbackUpdate = null;
    }

    protected void detachMiddleCallbackDelete()
    {
        if (middleCallbackDelete != null)
        {
            middleCallbackDelete.setPrimaryCallback(null);
        }
        middleCallbackDelete = null;
    }

    @Override public void onDestroy()
    {
        progressBar = null;
        super.onDestroy();
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityKeyId = securityId;

        // TODO change the test to something passed in the args bundle
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

        if (andDisplay)
        {
            displaySecurityTitle();
        }
    }

    private void displaySecurityTitle()
    {
        if (securityTitle != null)
        {
            securityTitle.setText(SecurityUtils.getDisplayableSecurityName(securityKeyId));
        }
    }

    private void dismissProgress()
    {
        ProgressDialog progressBarCopy = progressBar;
        if (progressBarCopy != null)
        {
            progressBarCopy.dismiss();
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

        detachSecurityCompactFetchTask();
        securityCompactCacheFetchTask = securityCompactCache.get().getOrFetch(securityId, createSecurityCompactCacheListener(andDisplay));
        securityCompactCacheFetchTask.execute();
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

    protected Callback<WatchlistPositionDTO> createWatchlistUpdateCallback()
    {
        return new WatchlistEditTHCallback();
    }

    protected Callback<WatchlistPositionDTO> createWatchlistDeleteCallback()
    {
        return new WatchlistDeletedTHCallback();
    }

    protected DTOCache.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener(boolean andDisplay)
    {
        return new WatchlistEditSecurityCompactCacheListener(andDisplay);
    }

    //TODO this extends is better? maybe not alex
    protected class WatchlistDeletedTHCallback extends WatchlistEditTHCallback
    {
        @Override protected void success(WatchlistPositionDTO watchlistPositionDTO,
                THResponse response)
        {
            watchlistPositionCache.get().invalidate(watchlistPositionDTO.securityDTO.getSecurityId());
            portfolioCompactListCacheLazy.get().invalidate(currentUserId.toUserBaseKey());
            if (isResumed())
            {
                SecurityIdList currentUserWatchlistSecurities =
                        userWatchlistPositionCache.get().get(currentUserId.toUserBaseKey());
                if (currentUserWatchlistSecurities != null
                        && currentUserWatchlistSecurities.contains(securityKeyId))
                {
                    currentUserWatchlistSecurities.remove(securityKeyId);
                }
                getNavigator().popFragment();
            }
            else
            {
                dismissProgress();
            }
        }
    }

    protected class WatchlistEditTHCallback extends THCallback<WatchlistPositionDTO>
    {
        @Override protected void finish()
        {
            dismissProgress();
        }

        @Override protected void success(WatchlistPositionDTO watchlistPositionDTO, THResponse response)
        {
            //TODO we need a cacheUtil control cache invalidate
            watchlistPositionCache.get().put(securityKeyId, watchlistPositionDTO);
            portfolioCompactListCacheLazy.get().invalidate(currentUserId.toUserBaseKey());
            if (isResumed())
            {
                SecurityIdList currentUserWatchlistSecurities =
                        userWatchlistPositionCache.get().get(currentUserId.toUserBaseKey());
                if (currentUserWatchlistSecurities != null && !currentUserWatchlistSecurities.contains(securityKeyId))
                {
                    currentUserWatchlistSecurities.add(securityKeyId);
                }

                getNavigator().popFragment();
            }
            else
            {
                dismissProgress();
            }
        }

        @Override protected void failure(THException ex)
        {
            Timber.e(ex, "Failed to update watchlist position");
            THToast.show(ex);
            dismissProgress();
        }
    }

    protected class WatchlistEditSecurityCompactCacheListener implements DTOCache.Listener<SecurityId, SecurityCompactDTO>
    {
        private final boolean andDisplay;

        public WatchlistEditSecurityCompactCacheListener(boolean andDisplay)
        {
            super();
            this.andDisplay = andDisplay;
        }

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
    }
}
