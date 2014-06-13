package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.squareup.picasso.Picasso;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import javax.inject.Inject;
import retrofit.Callback;
import timber.log.Timber;

abstract public class BaseAlertEditFragment extends BasePurchaseManagerFragment
{
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

    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<AlertCompactListCache> alertCompactListCache;
    @Inject protected SecurityCompactCache securityCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Picasso picasso;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected SecurityAlertCountingHelper securityAlertCountingHelper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject ResideMenu resideMenu;

    protected SecurityId securityId;
    protected AlertDTO alertDTO;
    protected SecurityCompactDTO securityCompactDTO;
    protected DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> securityCompactCacheFetchTask;
    protected ProgressDialog progressDialog;

    protected Callback<AlertCompactDTO> createAlertUpdateCallback()
    {
        return new AlertCreateCallback();
    }

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

    protected CompoundButton.OnCheckedChangeListener createPercentageCheckedChangeListener()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                handlePercentageCheckedChange(isChecked);
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
        View view = inflater.inflate(R.layout.alert_edit_fragment, container, false);
        initViews(view);
        resideMenu.addIgnoredView(targetPriceSeekBar);
        resideMenu.addIgnoredView(percentageSeekBar);
        return view;
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.alert_edit_menu, menu);
        getSherlockActivity().getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        displayActionBarTitle();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        alertToggle.setVisibility(View.GONE);
        targetPercentageChangeToggle.setOnCheckedChangeListener(createPercentageCheckedChangeListener());
        targetPriceToggle.setOnCheckedChangeListener(createTargetPriceCheckedChangeListener());
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
        targetPriceToggle.setOnCheckedChangeListener(null);
        targetPercentageChangeToggle.setOnCheckedChangeListener(null);
        percentageSeekBar.setOnSeekBarChangeListener(null);
        targetPriceSeekBar.setOnSeekBarChangeListener(null);
        detachSecurityCompactCacheFetchTask();
        resideMenu.removeIgnoredView(targetPriceSeekBar);
        resideMenu.removeIgnoredView(percentageSeekBar);
        super.onDestroyView();
    }

    protected void detachSecurityCompactCacheFetchTask()
    {
        if (securityCompactCacheFetchTask != null)
        {
            securityCompactCacheFetchTask.setListener(null);
        }
        securityCompactCacheFetchTask = null;
    }

    protected void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        progressDialog = progressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
        detachSecurityCompactCacheFetchTask();
        securityCompactCacheFetchTask = securityCompactCache.getOrFetch(securityId, true, createSecurityCompactCacheListener());
        securityCompactCacheFetchTask.execute();
    }

    protected AlertFormDTO getFormDTO()
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
        AlertFormDTO alertFormDTO = getFormDTO();
        if (alertFormDTO == null)
        {
            THToast.show(R.string.error_alert_insufficient_info);
        }
        else if (alertsAreFree())
        {
            saveAlert();
        }
        else if (securityAlertCountingHelper.getAlertSlots(currentUserId.toUserBaseKey()).freeAlertSlots <= 0)
        {
            popPurchase();
        }
        else
        {
            saveAlert();
        }
    }

    protected void popPurchase()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS);
    }

    @Override public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest uiBillingRequest = super.getShowProductDetailRequest(domain);
        uiBillingRequest.startWithProgressDialog = true;
        uiBillingRequest.popIfBillingNotAvailable = true;
        uiBillingRequest.popIfProductIdentifierFetchFailed = true;
        uiBillingRequest.popIfInventoryFetchFailed = true;
        uiBillingRequest.popIfPurchaseFailed = true;
        uiBillingRequest.purchaseReportedListener = new PurchaseReporter.OnPurchaseReportedListener()
        {
            @Override public void onPurchaseReported(int requestCode, ProductPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                saveAlert();
            }

            @Override public void onPurchaseReportFailed(int requestCode, ProductPurchase reportedPurchase, BillingException error)
            {
            }
        };
        return uiBillingRequest;
    }

    protected void saveAlert()
    {
        AlertFormDTO alertFormDTO = getFormDTO();
        if (alertFormDTO == null)
        {
            THToast.show(R.string.error_alert_insufficient_info);
        }
        else if (alertFormDTO.active) // TODO decide whether we need to submit even when it is inactive
        {
            progressDialog = progressDialogUtil.create(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);
            saveAlertProper(alertFormDTO);
        }
    }

    abstract protected void saveAlertProper(AlertFormDTO alertFormDTO);

    protected void linkWith(AlertDTO alertDTO, boolean andDisplay)
    {
        this.alertDTO = alertDTO;

        if (alertDTO != null && alertDTO.security != null)
        {
            linkWith(alertDTO.security, andDisplay);
        }

        if (andDisplay)
        {
            updateSwitchVisibility();
            displayTargetPrice();
            displayActiveUntil();
            displayPriceChangeSeekBar();
        }
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
        if (alertDTO == null)
        {
            return; // TODO better than that
        }
        THSignedNumber thTargetPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.targetPrice, THSignedNumber.WITHOUT_SIGN);
        targetPrice.setText(thTargetPrice.toString());

        if (securityCompactDTO != null && securityCompactDTO.lastPrice != null)
        {
            targetPriceSeekBar.setProgress((int) (50.0 * alertDTO.targetPrice / securityCompactDTO.lastPrice));
        }
        updateTargetPriceChangeValues(targetPriceToggle.isChecked());
    }

    protected void linkWith(SecurityCompactDTO security, boolean andDisplay)
    {
        this.securityCompactDTO = security;

        if (andDisplay)
        {
            displayStockLogo();
            displayCurrentPrice();
            displayAsOfDate();
            displayStockSymbol();
            displayCompanyName();
        }
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
        if (alertDTO == null)
        {
            // TODO decide what to do
        }
        else if (alertDTO.priceMovement == null)
        {
            THSignedNumber thTargetPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.targetPrice, THSignedNumber.WITHOUT_SIGN);
            targetPrice.setText(thTargetPrice.toString());
            targetPriceLabel.setText(getString(R.string.stock_alert_target_price));
        }
        else
        {
            THSignedNumber thPriceMovement = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, alertDTO.priceMovement * 100);
            targetPrice.setText(thPriceMovement.toString(0));
            targetPriceLabel.setText(getString(R.string.stock_alert_percentage_movement));
        }
    }

    protected Spanned getFormattedTargetPriceChange(String targetPriceString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.stock_alert_target_price_change_format), targetPriceString));
    }

    protected Spanned getFormattedPercentageChange(String percentageString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.stock_alert_percentage_change_format), percentageString));
    }

    protected void displayCurrentPrice()
    {
        if (currentPrice != null)
        {
            THSignedNumber thCurrentPrice = null;
            if (securityCompactDTO != null)
            {
                thCurrentPrice = new THSignedNumber(
                        THSignedNumber.TYPE_MONEY,
                        securityCompactDTO.lastPrice,
                        THSignedNumber.WITHOUT_SIGN,
                        securityCompactDTO.currencyDisplay);
            }
            currentPrice.setText(thCurrentPrice == null ? "-" : thCurrentPrice.toString());
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
    protected void handlePercentageCheckedChange(boolean isChecked)
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
        THSignedNumber thPercentageChange = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, (double) getSeekingMovementPercentage(), THSignedNumber.WITH_SIGN);
        percentageChange.setText(getFormattedPercentageChange(isChecked ? thPercentageChange.toString(0) : "-"));

        if (securityCompactDTO != null && securityCompactDTO.lastPrice != null)
        {
            THSignedNumber thPercentageChangePriceValue = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    getSeekingMovementPrice(),
                    THSignedNumber.WITHOUT_SIGN,
                    securityCompactDTO.currencyDisplay
            );
            percentageChangePriceValue.setText(getFormattedPercentageChangeTargetValue(isChecked ? thPercentageChangePriceValue.toString() : "-"));
        }

        percentageSeekBar.setEnabled(targetPercentageChangeToggle.isChecked());
    }

    protected double getSeekingMovementPrice()
    {
        if (securityCompactDTO == null || securityCompactDTO.lastPrice == null)
        {
            return 0;
        }
        return securityCompactDTO.lastPrice * ( 1 + ((double) getSeekingMovementPercentage()) / 100 );
    }

    protected int getSeekingMovementPercentage()
    {
        if (percentageSeekBar == null)
        {
            return 0;
        }
        return percentageSeekBar.getProgress() - 50;
    }

    protected Spanned getFormattedPercentageChangeTargetValue(String percentageChangeTargetValueString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.stock_alert_percentage_change_target_value_format), percentageChangeTargetValueString));
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
        if (seekingTargetPrice != null)
        {
            THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, seekingTargetPrice, THSignedNumber.WITHOUT_SIGN);
            targetPriceChange.setText(getFormattedTargetPriceChange(handlerEnabled ? thSignedNumber.toString() : "-"));
            targetPriceSeekBar.setEnabled(targetPriceToggle.isChecked());
        }
    }

    protected Double getSeekingTargetPrice()
    {
        if (securityCompactDTO == null || targetPriceSeekBar == null)
        {
            return null;
        }
        return (securityCompactDTO.lastPrice * 2) * targetPriceSeekBar.getProgress() / targetPriceSeekBar.getMax();
    }
    //endregion

    protected class AlertCreateCallback extends THCallback<AlertCompactDTO>
    {
        @Override protected void finish()
        {
            progressDialog.hide();
        }

        @Override protected void success(AlertCompactDTO alertCompactDTO, THResponse thResponse)
        {
            getNavigator().popFragment();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected DTOCache.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener()
    {
        return new BaseAlertEditSecurityCompactCacheListener();
    }

    protected class BaseAlertEditSecurityCompactCacheListener implements DTOCache.Listener<SecurityId, SecurityCompactDTO>
    {
        @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value,
                boolean fromCache)
        {
            hideDialog();
            linkWith(value, true);
        }

        @Override public void onErrorThrown(SecurityId key, Throwable error)
        {
            hideDialog();
            THToast.show(new THException(error));
        }

        private void hideDialog()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
        }
    }
}
