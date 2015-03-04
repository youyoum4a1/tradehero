package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import com.special.residemenu.ResideMenu;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.NotifyingStickyScrollView;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingInteractorRx;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.alert.AlertSlotDTO;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.ReplaceWith;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction1;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import rx.Notification;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

abstract public class BaseAlertEditFragment extends DashboardFragment
{
    @InjectView(R.id.alert_scroll_view) NotifyingStickyScrollView scrollView;

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.target_price_label) TextView targetPriceLabel;
    @InjectView(R.id.current_price) TextView currentPrice;
    @InjectView(R.id.as_of_date) TextView asOfDate;
    @InjectView(R.id.active_until) TextView activeUntil;
    @InjectView(R.id.alert_toggle) Switch alertToggle;

    @InjectView(R.id.alert_edit_percentage_change) TextView percentageChange;
    @InjectView(R.id.alert_edit_target_price_change) TextView targetPriceChange;
    @InjectView(R.id.alert_edit_percentage_change_actual_value) TextView percentageChangePriceValue;

    @InjectView(R.id.alert_edit_toggle_percentage_change) Switch targetPercentageChangeToggle;
    @InjectView(R.id.alert_edit_toggle_target_price) Switch targetPriceToggle;

    @InjectView(R.id.alert_edit_price_changer_target_price_seek_bar) SeekBar targetPriceSeekBar;
    @InjectView(R.id.alert_edit_price_changer_percentage_seek_bar) SeekBar percentageSeekBar;

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Picasso picasso;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SecurityAlertCountingHelper securityAlertCountingHelper;
    @Inject ResideMenu resideMenu;

    protected SecurityId securityId;
    protected AlertDTO alertDTO;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject protected THBillingInteractorRx userInteractorRx;

    protected CompoundButton.OnCheckedChangeListener createTargetPriceCheckedChangeListener()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                handleTargetPriceCheckedChange(isChecked);
            }
        };
    }

    protected SeekBar.OnSeekBarChangeListener createPriceSeekBarChangeListener()
    {
        return new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updateTargetPriceChangeValues(true);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        };
    }

    protected SeekBar.OnSeekBarChangeListener createPercentageSeekBarChangeListener()
    {
        return new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                updatePercentageChangeValues(true);
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.alert_edit_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener.get());
        alertToggle.setVisibility(View.GONE);
        targetPriceToggle.setOnCheckedChangeListener(createTargetPriceCheckedChangeListener());
        resideMenu.addIgnoredView(targetPriceSeekBar);
        resideMenu.addIgnoredView(percentageSeekBar);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.alert_edit_menu, menu);
        displayActionBarTitle();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_save:
                conditionalSaveAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        scrollView.setOnScrollChangedListener(null);
        targetPriceToggle.setOnCheckedChangeListener(null);
        percentageSeekBar.setOnSeekBarChangeListener(null);
        targetPriceSeekBar.setOnSeekBarChangeListener(null);
        resideMenu.removeIgnoredView(targetPriceSeekBar);
        resideMenu.removeIgnoredView(percentageSeekBar);
        super.onDestroyView();
    }

    protected void linkWith(@NonNull SecurityId securityId)
    {
        this.securityId = securityId;
        fetchSecurityCompact();
    }

    protected void fetchSecurityCompact()
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.alert_dialog_please_wait),
                true);
        onStopSubscriptions.add(AppObservable.bindFragment(this, securityCompactCache.get(securityId))
                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                .doOnEach(new DismissDialogAction1<Notification<? super SecurityCompactDTO>>(progressDialog))
                .subscribe(
                        new Action1<SecurityCompactDTO>()
                        {
                            @Override public void call(SecurityCompactDTO compactDTO)
                            {
                                linkWith(compactDTO);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    @Nullable protected AlertFormDTO getFormDTO()
    {
        if (targetPriceToggle == null || targetPercentageChangeToggle == null || securityCompactDTO == null || securityCompactDTO.lastPrice == null)
        {
            Timber.d("securityCompact %s", securityCompactDTO);
            return null;
        }

        AlertFormDTO alertFormDTO = new AlertFormDTO();
        alertFormDTO.active = targetPriceToggle.isChecked() || targetPercentageChangeToggle.isChecked();
        if (alertFormDTO.active)
        {
            alertFormDTO.securityId = securityCompactDTO.id;
            alertFormDTO.targetPrice = targetPriceToggle.isChecked() ? getSeekingTargetPrice() : securityCompactDTO.lastPrice;
            alertFormDTO.priceMovement = targetPercentageChangeToggle.isChecked() ? getSeekingMovementPercentage() / 100.0 : null;

            if (targetPriceToggle.isChecked())
            {
                alertFormDTO.upOrDown = getSeekingTargetPrice() > securityCompactDTO.lastPrice;
            }
        }
        return alertFormDTO;
    }

    protected void conditionalSaveAlert()
    {
        final AlertFormDTO alertFormDTO = getFormDTO();
        if (alertFormDTO == null)
        {
            THToast.show(R.string.error_alert_insufficient_info);
        }
        else
        {
            final ProgressDialog progressDialog = ProgressDialog.show(
                    getActivity(),
                    getString(R.string.loading_loading),
                    getString(R.string.alert_dialog_please_wait),
                    true);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    securityAlertCountingHelper.getAlertSlots(currentUserId.toUserBaseKey())
                            .take(1)
                            .flatMap(new Func1<AlertSlotDTO, Observable<? extends AlertSlotDTO>>()
                            {
                                @Override public Observable<? extends AlertSlotDTO> call(AlertSlotDTO alertSlotDTO)
                                {
                                    return BaseAlertEditFragment.this.conditionalPopPurchaseRx(alertSlotDTO);
                                }
                            })
                            .flatMap(new Func1<AlertSlotDTO, Observable<? extends AlertCompactDTO>>()
                            {
                                @Override public Observable<? extends AlertCompactDTO> call(AlertSlotDTO alertSlot)
                                {
                                    return BaseAlertEditFragment.this.saveAlertRx(alertFormDTO);
                                }
                            }))
                    .doOnEach(new DismissDialogAction1<Notification<? super AlertCompactDTO>>(progressDialog))
                    .subscribe(
                            new Action1<AlertCompactDTO>()
                            {
                                @Override public void call(AlertCompactDTO t1)
                                {
                                    BaseAlertEditFragment.this.handleAlertUpdated(t1);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable t1)
                                {
                                    BaseAlertEditFragment.this.handleAlertUpdateFailed(t1);
                                }
                            }));
        }
    }

    @NonNull protected Observable<AlertSlotDTO> conditionalPopPurchaseRx(@NonNull AlertSlotDTO alertSlot)
    {
        if (alertSlot.freeAlertSlots <= 0)
        {
            //noinspection unchecked
            return userInteractorRx.purchaseAndClear(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)
                    .map(new ReplaceWith<>(alertSlot));
        }
        return Observable.just(alertSlot);
    }

    @NonNull protected Observable<AlertCompactDTO> saveAlertRx(@NonNull AlertFormDTO alertFormDTO)
    {
        if (alertFormDTO.active) // TODO decide whether we need to submit even when it is inactive
        {
            return saveAlertProperRx(alertFormDTO);
        }
        else
        {
            THToast.show(R.string.error_alert_save_inactive);
            return Observable.error(new IllegalArgumentException(getString(R.string.error_alert_save_inactive)));
        }
    }

    @NonNull abstract protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO);

    protected void linkWith(AlertDTO alertDTO)
    {
        this.alertDTO = alertDTO;

        if (alertDTO != null && alertDTO.security != null)
        {
            linkWith(alertDTO.security);
        }

        updateSwitchVisibility();
        displayTargetPrice();
        displayActiveUntil();
        displayPriceChangeSeekBar();
    }

    protected void updateSwitchVisibility()
    {
        if (alertDTO == null || alertDTO.priceMovement != null)
        {
            // force to call the callback onCheckedChange ...
            targetPriceToggle.setChecked(false);
            targetPercentageChangeToggle.setChecked(true);
        }
        else
        {
            targetPriceToggle.setChecked(true);
            targetPercentageChangeToggle.setChecked(false);
        }
    }

    abstract protected void displayActionBarTitle();

    protected void displayPriceChangeSeekBar()
    {
        displayTargetPriceHandler();

        displayTargetPricePercentageHandler();

        percentageSeekBar.setOnSeekBarChangeListener(createPercentageSeekBarChangeListener());
        targetPriceSeekBar.setOnSeekBarChangeListener(createPriceSeekBarChangeListener());
    }

    protected void displayTargetPricePercentageHandler()
    {
        if (alertDTO == null)
        {
            // TODO decide
        }
        else if (alertDTO.priceMovement != null)
        {
            percentageSeekBar.setProgress(50 + (int) Math.round(alertDTO.priceMovement * 100.0));
        }
        else
        {
            percentageSeekBar.setProgress(50);
        }
        updatePercentageChangeValues(targetPercentageChangeToggle.isChecked());
    }

    protected void displayTargetPriceHandler()
    {
        if (alertDTO == null || securityCompactDTO == null)
        {
            return; // TODO better than that
        }
        THSignedMoney.builder(alertDTO.targetPrice)
                .withOutSign()
                .currency(securityCompactDTO.currencyDisplay)
                .build()
                .into(targetPrice);
        if (securityCompactDTO.lastPrice != null)
        {
            targetPriceSeekBar.setProgress((int) (50.0 * alertDTO.targetPrice / securityCompactDTO.lastPrice));
        }
        updateTargetPriceChangeValues(targetPriceToggle.isChecked());
    }

    protected void linkWith(SecurityCompactDTO security)
    {
        this.securityCompactDTO = security;

        displayStockLogo();
        displayCurrentPrice();
        displayAsOfDate();
        displayStockSymbol();
        displayCompanyName();
    }

    protected void displayActiveUntil()
    {
        if (activeUntil != null)
        {
            if (alertDTO == null)
            {
                activeUntil.setText(R.string.na);
            }
            else if (!alertDTO.active)
            {
                activeUntil.setText("-");
            }
            else if (alertDTO.activeUntilDate != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.stock_alert_price_info_as_of_date_format));
                activeUntil.setText(sdf.format(alertDTO.activeUntilDate));
            }
            else
            {
                activeUntil.setText("");
            }
        }
    }

    protected void displayTargetPrice()
    {
        if (alertDTO == null || securityCompactDTO == null)
        {
            // TODO decide what to do
        }
        else if (alertDTO.priceMovement == null)
        {
            THSignedMoney.builder(alertDTO.targetPrice)
                    .withOutSign()
                    .currency(securityCompactDTO.currencyDisplay)
                    .build()
                    .into(targetPrice);
            targetPriceLabel.setText(getString(R.string.stock_alert_target_price));
        }
        else
        {
            THSignedPercentage.builder(alertDTO.priceMovement * 100)
                    .build()
                    .into(targetPrice);
            targetPriceLabel.setText(getString(R.string.stock_alert_percentage_movement));
        }
    }

    @StringRes protected int getFormattedTargetPriceChange()
    {
        return R.string.stock_alert_target_price_change_format;
    }

    @StringRes protected int getPercentageChangeFormatResId()
    {
        return R.string.stock_alert_percentage_change_format;
    }

    protected void displayCurrentPrice()
    {
        if (currentPrice != null)
        {
            if (securityCompactDTO != null)
            {
                THSignedMoney.builder(securityCompactDTO.lastPrice)
                        .withOutSign()
                        .currency(securityCompactDTO.currencyDisplay)
                        .build()
                        .into(currentPrice);
            }
            else
            {
                currentPrice.setText("-");
            }
        }
    }

    protected void displayAsOfDate()
    {
        if (asOfDate != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.lastPriceDateAndTimeUtc != null)
            {
                SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.stock_alert_price_info_as_of_date_format));
                asOfDate.setText(getString(R.string.stock_alert_price_info_as_of_date, sdf.format(securityCompactDTO.lastPriceDateAndTimeUtc)));
            }
            else
            {
                asOfDate.setText("");
            }
        }
    }

    protected void displayCompanyName()
    {
        if (companyName != null)
        {
            if (securityCompactDTO != null)
            {
                companyName.setText(securityCompactDTO.name);
            }
            else
            {
                companyName.setText(R.string.na);
            }
        }
    }

    protected void displayStockSymbol()
    {
        if (stockSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                stockSymbol.setText(R.string.na);
            }
        }
    }

    protected void displayStockLogo()
    {
        if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
        {
            picasso
                    .load(securityCompactDTO.imageBlobUrl)
                    .transform(new WhiteToTransparentTransformation())
                    .into(stockLogo);
        }
        else if (securityCompactDTO != null)
        {
            picasso
                    .load(securityCompactDTO.getExchangeLogoId())
                    .into(stockLogo);
        }
        else
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }

    //region Handling percentage changes
    @SuppressWarnings("UnusedDeclaration")
    @OnCheckedChanged(R.id.alert_edit_toggle_percentage_change)
    protected void handlePercentageCheckedChange(CompoundButton button, boolean isChecked)
    {
        if (isChecked && targetPriceToggle.isChecked())
        {
            targetPriceToggle.setChecked(false);
        }

        percentageSeekBar.setEnabled(isChecked);

        updatePercentageChangeValues(isChecked);
    }

    protected void updatePercentageChangeValues(boolean isChecked)
    {
        if (isChecked)
        {
            THSignedPercentage.builder((double) getSeekingMovementPercentage())
                    .withSign()
                    .format(getString(getPercentageChangeFormatResId()))
                    .boldValue()
                    .build()
                    .into(percentageChange);
        }
        else
        {
            percentageChange.setText(getString(getPercentageChangeFormatResId(), "-"));
        }

        if (securityCompactDTO != null && securityCompactDTO.lastPrice != null)
        {
            if (isChecked)
            {
                THSignedMoney.builder(getSeekingMovementPrice())
                        .withOutSign()
                        .boldValue()
                        .currency(securityCompactDTO.currencyDisplay)
                        .boldValue()
                        .format(getString(getFormattedPercentageChangeTargetValue()))
                        .build()
                        .into(percentageChangePriceValue);
            }
            else
            {
                percentageChangePriceValue.setText(getString(getFormattedPercentageChangeTargetValue(), "-"));
            }
        }

        percentageSeekBar.setEnabled(targetPercentageChangeToggle.isChecked());
    }

    protected double getSeekingMovementPrice()
    {
        if (securityCompactDTO == null || securityCompactDTO.lastPrice == null)
        {
            return 0;
        }
        return securityCompactDTO.lastPrice * (1 + ((double) getSeekingMovementPercentage()) / 100);
    }

    protected int getSeekingMovementPercentage()
    {
        if (percentageSeekBar == null)
        {
            return 0;
        }
        return percentageSeekBar.getProgress() - 50;
    }

    @StringRes protected int getFormattedPercentageChangeTargetValue()
    {
        return R.string.stock_alert_percentage_change_target_value_format;
    }
    //endregion

    //region Handling target price changes
    protected void handleTargetPriceCheckedChange(boolean isChecked)
    {
        if (isChecked && targetPercentageChangeToggle.isChecked())
        {
            targetPercentageChangeToggle.setChecked(false);
        }
        targetPriceSeekBar.setEnabled(isChecked);

        updateTargetPriceChangeValues(isChecked);
    }

    protected void updateTargetPriceChangeValues(boolean handlerEnabled)
    {
        Double seekingTargetPrice = getSeekingTargetPrice();
        if (seekingTargetPrice != null && securityCompactDTO != null)
        {
            if (handlerEnabled)
            {
                THSignedMoney.builder(seekingTargetPrice)
                        .withOutSign()
                        .currency(securityCompactDTO.currencyDisplay)
                        .boldValue()
                        .format(getString(getFormattedTargetPriceChange()))
                        .build()
                        .into(targetPriceChange);
            }
            else
            {
                targetPriceChange.setText(getString(getFormattedTargetPriceChange(), "-"));
            }
            targetPriceSeekBar.setEnabled(targetPriceToggle.isChecked());
        }
    }

    protected Double getSeekingTargetPrice()
    {
        if (securityCompactDTO == null || securityCompactDTO.lastPrice == null || targetPriceSeekBar == null)
        {
            return null;
        }
        return (securityCompactDTO.lastPrice * 2) * targetPriceSeekBar.getProgress() / targetPriceSeekBar.getMax();
    }
    //endregion

    protected void handleAlertUpdated(@NonNull AlertCompactDTO alertCompactDTO)
    {
        navigator.get().popFragment();
    }

    protected void handleAlertUpdateFailed(@NonNull Throwable e)
    {
        THToast.show(new THException(e));
    }
}
