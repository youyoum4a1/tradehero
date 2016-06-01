package com.ayondo.academy.fragments.security;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.watchlist.WatchlistPositionDTO;
import com.ayondo.academy.api.watchlist.WatchlistPositionFormDTO;
import com.ayondo.academy.fragments.base.DashboardFragment;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.models.number.THSignedNumber;
import com.ayondo.academy.network.service.WatchlistServiceWrapper;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import com.ayondo.academy.persistence.watchlist.WatchlistPositionCacheRx;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.rx.view.DismissDialogAction0;
import com.ayondo.academy.utils.DeviceUtil;
import com.ayondo.academy.utils.SecurityUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dagger.Lazy;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import timber.log.Timber;

public class WatchlistEditFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = WatchlistEditFragment.class.getName() + ".securityKeyId";

    @Bind(R.id.edit_watchlist_item_security_logo) ImageView securityLogo;
    @Bind(R.id.edit_watchlist_item_security_name) TextView securityTitle;
    @Bind(R.id.edit_watchlist_item_security_desc) TextView securityDesc;
    @Bind(R.id.edit_watchlist_item_security_currency) TextView watchCurrency;
    @Bind(R.id.edit_watchlist_item_security_price) TextView watchPrice;
    @Bind(R.id.edit_watchlist_item_done) TextView doneButton;
    @Bind(R.id.edit_watchlist_item_delete) TextView deleteButton;

    private SecurityId securityKeyId;
    private WatchlistPositionDTO watchlistPositionDTO;
    private SecurityCompactDTO securityCompactDTO;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject WatchlistPositionCacheRx watchlistPositionCache;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject Lazy<Picasso> picasso;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @NonNull public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityKeyId = getSecurityId(getArguments());
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.edit_watchlist_item_layout, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        securityTitle.setText(SecurityUtils.getDisplayableSecurityName(securityKeyId));
        fetchRequisite();
    }

    @Override public void onStop()
    {
        DeviceUtil.dismissKeyboard(watchPrice);
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        picasso.get().cancelRequest(securityLogo);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected void fetchRequisite()
    {
        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        watchlistPositionCache.get(securityKeyId)
                                .map(new PairGetSecond<SecurityId, WatchlistPositionDTO>())
                                .startWith(Observable.<WatchlistPositionDTO>just(null))
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(
                                        new Action1<WatchlistPositionDTO>()
                                        {
                                            @Override public void call(@Nullable WatchlistPositionDTO watchlistPositionDTO)
                                            {
                                                display(watchlistPositionDTO);
                                            }
                                        }),
                        securityCompactCache.getOne(securityKeyId)
                                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<SecurityCompactDTO>()
                                {
                                    @Override public void call(@NonNull SecurityCompactDTO securityCompactDTO)
                                    {
                                        display(securityCompactDTO);
                                    }
                                }),
                        new Func2<WatchlistPositionDTO, SecurityCompactDTO, Pair<WatchlistPositionDTO, SecurityCompactDTO>>()
                        {
                            @Override public Pair<WatchlistPositionDTO, SecurityCompactDTO> call(
                                    @Nullable WatchlistPositionDTO watchlistPositionDTO,
                                    @NonNull SecurityCompactDTO securityCompactDTO)
                            {
                                return Pair.create(watchlistPositionDTO, securityCompactDTO);
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Pair<WatchlistPositionDTO, SecurityCompactDTO>>()
                                {
                                    @Override public void call(Pair<WatchlistPositionDTO, SecurityCompactDTO> pair)
                                    {
                                        display(pair.first, pair.second);
                                    }
                                },
                                new ToastOnErrorAction1())
        );
    }

    protected void display(@Nullable WatchlistPositionDTO watchlistPositionDTO)
    {
        this.watchlistPositionDTO = watchlistPositionDTO;
        deleteButton.setEnabled(watchlistPositionDTO != null);
        setActionBarTitle(watchlistPositionDTO != null
                ? R.string.watchlist_edit_title
                : R.string.watchlist_add_title);
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(watchlistPositionDTO != null ? AnalyticsConstants.Watchlist_Edit : AnalyticsConstants.Watchlist_Add));
    }

    protected void display(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        doneButton.setEnabled(true);
        securityDesc.setText(securityCompactDTO.name);

        if (securityCompactDTO.imageBlobUrl != null)
        {
            picasso.get()
                    .load(securityCompactDTO.imageBlobUrl)
                    .transform(new WhiteToTransparentTransformation())
                    .into(securityLogo);
        }
        else
        {
            securityLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
            securityLogo.setVisibility(View.VISIBLE);
        }

        watchCurrency.setText(getString(R.string.watchlist_average_price_header_with_currency,
                securityCompactDTO.currencyDisplay));
    }

    protected void display(@Nullable WatchlistPositionDTO watchlistPositionDTO, @NonNull SecurityCompactDTO securityCompactDTO)
    {
        Double price = (watchlistPositionDTO != null && watchlistPositionDTO.watchlistPriceRefCcy != null)
                ? watchlistPositionDTO.watchlistPriceRefCcy
                : securityCompactDTO.lastPrice != null
                        ? securityCompactDTO.lastPrice
                        : null;
        THSignedNumber number = price != null
                ? THSignedNumber.builder(price).build()
                : null;
        watchPrice.setText(number != null ? number.toString() : "");
    }

    @NonNull private ProgressDialog showUpdatingProgress()
    {
        return ProgressDialog.show(
                getActivity(),
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.watchlist_updating),
                true);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.edit_watchlist_item_done) void handleDoneButtonClicked(View view)
    {
        DeviceUtil.dismissKeyboard(watchPrice);
        doneButton.setEnabled(false);
        deleteButton.setEnabled(false);
        final ProgressDialog progressDialog = showUpdatingProgress();
        try
        {
            double price = Double.parseDouble(watchPrice.getText().toString());
            // add new watchlist
            WatchlistPositionFormDTO watchPositionItemForm = new WatchlistPositionFormDTO(securityCompactDTO.id, price, 1);

            Observable<WatchlistPositionDTO> updateObservable;
            if (watchlistPositionDTO != null)
            {
                updateObservable = watchlistServiceWrapper.updateWatchlistEntryRx(
                        watchlistPositionDTO.getPositionCompactId(),
                        watchPositionItemForm);
            }
            else
            {
                updateObservable = watchlistServiceWrapper.createWatchlistEntryRx(watchPositionItemForm);
            }
            onDestroyViewSubscriptions.add(AppObservable.bindSupportFragment(this, updateObservable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                    .subscribe(new WatchlistEditObserver()));
        } catch (NumberFormatException ex)
        {
            THToast.show(getString(R.string.wrong_number_format));
            Timber.e("Parsing error", ex);
        } catch (Exception ex)
        {
            THToast.show(ex.getMessage());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.edit_watchlist_item_delete) void handleButtonDeleteClicked(View view)
    {
        DeviceUtil.dismissKeyboard(watchPrice);
        doneButton.setEnabled(false);
        deleteButton.setEnabled(false);
        ProgressDialog progressDialog = showUpdatingProgress();
        onDestroyViewSubscriptions.add(watchlistServiceWrapper.deleteWatchlistRx(watchlistPositionDTO.getPositionCompactId())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .subscribe(new WatchlistEditObserver()));
    }

    protected class WatchlistEditObserver implements Observer<WatchlistPositionDTO>
    {
        @Override public void onNext(WatchlistPositionDTO args)
        {
            navigator.get().popFragment();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            display(watchlistPositionDTO);
            if (securityCompactDTO != null)
            {
                display(securityCompactDTO);
            }
            Timber.e(e, "Failed to update watchlist position");
            THToast.show(new THException(e));
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnTextChanged(value = R.id.edit_watchlist_item_security_price, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void handleNumberUpdated(Editable s)
    {
        try
        {
            double price = Double.parseDouble(s.toString());
            doneButton.setEnabled(true);
        } catch (NumberFormatException e)
        {
            doneButton.setEnabled(false);
        }
    }
}
