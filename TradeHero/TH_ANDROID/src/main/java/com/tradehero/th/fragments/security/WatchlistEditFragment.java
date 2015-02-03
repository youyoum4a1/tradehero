package com.tradehero.th.fragments.security;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCacheRx;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WatchlistEditFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = WatchlistEditFragment.class.getName() + ".securityKeyId";

    private ImageView securityLogo;
    private TextView securityTitle;
    private TextView securityDesc;
    private EditText watchPrice;
    private EditText watchQuantity;
    @NonNull private SecurityId securityKeyId;
    private SecurityCompactDTO securityCompactDTO;
    private TextView watchAction;
    private TextView deleteButton;
    @Nullable private ProgressDialog progressBar;

    @Nullable private Subscription updateSubscription;
    @Nullable private Subscription deleteSubscription;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject Lazy<WatchlistPositionCacheRx> watchlistPositionCache;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @NonNull public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE));
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.edit_watchlist_item_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(@NonNull View view)
    {
        securityLogo = (ImageView) view.findViewById(R.id.edit_watchlist_item_security_logo);
        securityTitle = (TextView) view.findViewById(R.id.edit_watchlist_item_security_name);
        securityDesc = (TextView) view.findViewById(R.id.edit_watchlist_item_security_desc);

        watchPrice = (EditText) view.findViewById(R.id.edit_watchlist_item_security_price);
        watchQuantity = (EditText) view.findViewById(R.id.edit_watchlist_item_security_quantity);

        watchAction = (TextView) view.findViewById(R.id.edit_watchlist_item_done);
        if (watchAction != null)
        {
            watchAction.setEnabled(false);
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
        if (securityKeyId != null)
        {
            WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().getCachedValue(securityKeyId);
            deleteButton.setEnabled(watchlistPositionDTO != null);
        }
    }

    @NonNull private View.OnClickListener createOnWatchButtonClickedListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                DeviceUtil.dismissKeyboard(getView());
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
            WatchlistPositionFormDTO watchPositionItemForm = new WatchlistPositionFormDTO(securityCompactDTO.id, price, quantity);

            WatchlistPositionDTO existingWatchlistPosition = watchlistPositionCache.get().getCachedValue(securityCompactDTO.getSecurityId());
            unsubscribe(updateSubscription);
            if (existingWatchlistPosition != null)
            {
                updateSubscription = AppObservable.bindFragment(
                        this,
                        watchlistServiceWrapper.updateWatchlistEntryRx(
                        existingWatchlistPosition.getPositionCompactId(),
                        watchPositionItemForm))
                        .subscribe(createWatchlistUpdateObserver());
            }
            else
            {
                updateSubscription = AppObservable.bindFragment(
                        this,
                        watchlistServiceWrapper.createWatchlistEntryRx(
                        watchPositionItemForm))
                        .subscribe(createWatchlistUpdateObserver());
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

    @NonNull private View.OnClickListener createOnDeleteButtonClickedListener()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                DeviceUtil.dismissKeyboard(getView());
                handleButtonDeleteClicked();
            }
        };
    }

    private void handleButtonDeleteClicked()
    {
        WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().getCachedValue(securityKeyId);
        if (watchlistPositionDTO != null)
        {
            showProgressBar();
            unsubscribe(deleteSubscription);
            deleteSubscription = AppObservable.bindFragment(
                    this,
                    watchlistServiceWrapper.deleteWatchlistRx(watchlistPositionDTO.getPositionCompactId()))
                    .subscribe(createWatchlistDeleteObserver());
        }
        else
        {
            THToast.show(R.string.error_fetch_portfolio_watchlist);
        }

    }


    @Override public void onResume()
    {
        super.onResume();
        linkWith(getSecurityId(getArguments()), true);
    }

    @Override public void onDestroyView()
    {
        unsubscribe(updateSubscription);
        updateSubscription = null;
        unsubscribe(deleteSubscription);
        deleteSubscription = null;
        dismissProgress();
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        unsubscribe(updateSubscription);
        updateSubscription = null;
        unsubscribe(deleteSubscription);
        deleteSubscription = null;
        super.onSaveInstanceState(outState);
    }

    private void linkWith(@NonNull SecurityId securityId, boolean andDisplay)
    {
        this.securityKeyId = securityId;

        // TODO change the test to something passed in the args bundle
        if (watchlistPositionCache.get().get(securityId) != null)
        {
            setActionBarTitle(getString(R.string.watchlist_edit_title));
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_Edit));
        }
        else
        {
            setActionBarTitle(getString(R.string.watchlist_add_title));
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Watchlist_Add));
        }
        querySecurity(securityId, andDisplay);

        if (andDisplay)
        {
            displaySecurityTitle();
            checkDeleteButtonEnable();
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
        progressBar = null;
    }

    private void querySecurity(@NonNull SecurityId securityId, final boolean andDisplay)
    {
        if (progressBar != null)
        {
            progressBar.show();
        }
        else
        {
            progressBar = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait), getString(R.string.loading_loading), true);
        }

        AppObservable.bindFragment(this, securityCompactCache.get(securityId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSecurityCompactCacheObserver());
    }

    private void linkWith(@NonNull SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        watchAction.setEnabled(true);
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

            WatchlistPositionDTO watchListItem = watchlistPositionCache.get().getCachedValue(securityCompactDTO.getSecurityId());
            if (watchPrice != null)
            {
                watchPrice.setText(
                        watchListItem != null ?
                                "" + watchListItem.watchlistPriceRefCcy :
                                securityCompactDTO.lastPrice != null ? securityCompactDTO.lastPrice.toString() : "");
            }

            if (watchListItem != null)
            {
                watchQuantity.setText("" + (watchListItem.shares == null ? 1 : watchListItem.shares));
            }
        }
    }

    @NonNull protected Observer<WatchlistPositionDTO> createWatchlistUpdateObserver()
    {
        return new WatchlistEditTHObserver();
    }

    @NonNull protected Observer<WatchlistPositionDTO> createWatchlistDeleteObserver()
    {
        return new WatchlistDeletedTHObserver();
    }

    //TODO this extends is better? maybe not alex
    protected class WatchlistDeletedTHObserver extends WatchlistEditTHObserver
    {
        @Override public void onNext(WatchlistPositionDTO args)
        {
            if (isResumed())
            {
                navigator.get().popFragment();
            }
            else
            {
                dismissProgress();
            }
        }
    }

    protected class WatchlistEditTHObserver implements Observer<WatchlistPositionDTO>
    {
        @Override public void onNext(WatchlistPositionDTO args)
        {
            if (navigator != null)
            {
                navigator.get().popFragment();
            }
            dismissProgress();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Failed to update watchlist position");
            THToast.show(new THException(e));
            dismissProgress();
        }
    }

    @NonNull protected Observer<Pair<SecurityId, SecurityCompactDTO>> createSecurityCompactCacheObserver()
    {
        return new WatchlistEditSecurityCompactCacheObserver();
    }

    protected class WatchlistEditSecurityCompactCacheObserver implements Observer<Pair<SecurityId, SecurityCompactDTO>>
    {
        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
        {
            dismissProgress();
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            dismissProgress();
            THToast.show(R.string.error_fetch_security_info);
            Timber.e("Failed to fetch SecurityCompact for", e);
        }
    }
}
