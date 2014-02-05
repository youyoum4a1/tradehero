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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/28/14 Time: 5:18 PM Copyright (c) TradeHero
 */
public class AlertEditFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = AlertEditFragment.class.getName() + ".alertId";
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = AlertEditFragment.class.getName() + ".securityId";

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.target_price_label) TextView targetPriceLabel;
    @InjectView(R.id.current_price) TextView currentPrice;
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
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private AlertId alertId;
    private SecurityId securityId;

    private AlertDTO alertDTO;
    private SecurityCompactDTO securityCompactDTO;

    private ProgressDialog progressDialog;
    private DTOCache.GetOrFetchTask<SecurityId, SecurityCompactDTO> securityCompactCacheFetchTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.alert_edit_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.alert_edit_menu, menu);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.edit_alert);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        alertToggle.setVisibility(View.GONE);
        targetPercentageChangeToggle.setOnCheckedChangeListener(percentageCheckedChangeListener);
        targetPriceToggle.setOnCheckedChangeListener(targetPriceCheckedChangeListener);
    }

    @Override public void onDestroyView()
    {
        targetPriceToggle.setOnCheckedChangeListener(null);
        targetPercentageChangeToggle.setOnCheckedChangeListener(null);
        percentageSeekBar.setOnSeekBarChangeListener(null);
        targetPriceSeekBar.setOnSeekBarChangeListener(null);
        securityCompactCacheFetchTask = null;
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle alertBundle = args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE);
            if (alertBundle != null)
            {
                alertId = new AlertId(args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE));
                linkWith(alertId, true);
            }

            Bundle securityBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityBundle != null)
            {
                SecurityId securityId = new SecurityId(securityBundle);
                linkWith(securityId, true);
            }
        }
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        progressDialog = ProgressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.please_wait);
        securityCompactCacheFetchTask = securityCompactCache.get().getOrFetch(securityId, true, securityCompactCallback);
        securityCompactCacheFetchTask.execute();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_save:
                saveAlert();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAlert()
    {
        AlertFormDTO alertFormDTO = new AlertFormDTO();
        alertFormDTO.active = targetPriceToggle.isChecked() || targetPercentageChangeToggle.isChecked();
        if (alertFormDTO.active)
        {
            alertFormDTO.securityId = securityCompactDTO.id;
            alertFormDTO.targetPrice = targetPriceToggle.isChecked() ? getSeekingTargetPrice() : alertDTO.targetPrice;
            alertFormDTO.priceMovement = targetPercentageChangeToggle.isChecked() ? getSeekingMovementPercentage() / 100.0 : null;

            if (targetPriceToggle.isChecked())
            {
                alertFormDTO.upOrDown = getSeekingTargetPrice() > securityCompactDTO.lastPrice;
            }
            progressDialog = ProgressDialogUtil.create(getActivity(), getString(R.string.loading_loading), getString(R.string.please_wait));
            progressDialog.show();
            alertServiceWrapper.get().updateAlert(
                    alertId != null ? alertId : new AlertId(currentUserBaseKeyHolder.getCurrentUserBaseKey().key, 0),
                    alertFormDTO,
                    alertUpdateCallback);
        }
    }

    private void linkWith(AlertId alertId, boolean andDisplay)
    {
        if (alertId != null)
        {
            alertServiceWrapper.get().getAlert(alertId, alertCallback);
        }
    }

    private void linkWith(AlertDTO alertDTO, boolean andDisplay)
    {
        this.alertDTO = alertDTO;

        if (alertDTO.security != null)
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

    private void updateSwitchVisibility()
    {
        if (alertDTO.priceMovement != null)
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

    private void displayPriceChangeSeekBar()
    {
        displayTargetPriceHandler();

        displayTargetPricePercentageHandler();

        percentageSeekBar.setOnSeekBarChangeListener(percentageSeekBarChangeListener);
        targetPriceSeekBar.setOnSeekBarChangeListener(priceSeekBarChangeListener);
    }

    private void displayTargetPricePercentageHandler()
    {
        if (alertDTO.priceMovement != null)
        {
            percentageSeekBar.setProgress(50 + (int) Math.round(alertDTO.priceMovement * 100.0));
        }
        else
        {
            percentageSeekBar.setProgress(50);
        }
        updatePercentageChangeValues(targetPercentageChangeToggle.isChecked());
    }

    private void displayTargetPriceHandler()
    {
        THSignedNumber thTargetPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.targetPrice, false);
        targetPrice.setText(thTargetPrice.toString());

        if (securityCompactDTO != null && securityCompactDTO.lastPrice != null)
        {
            targetPriceSeekBar.setProgress((int) (50.0 * alertDTO.targetPrice / securityCompactDTO.lastPrice));
        }
        updateTargetPriceChangeValues(targetPriceToggle.isChecked());
    }

    private void linkWith(SecurityCompactDTO security, boolean andDisplay)
    {
        this.securityCompactDTO = security;

        if (andDisplay)
        {
            displayStockLogo();

            displayCurrentPrice();

            displayStockSymbol();

            displayCompanyName();
        }
    }

    private void displayActiveUntil()
    {
        if (!alertDTO.active)
        {
            activeUntil.setText("-");
        }
        else if (alertDTO.activeUntilDate != null)
        {
            activeUntil.setText(DateUtils.getDisplayableDate(getActivity(), alertDTO.activeUntilDate));
        }
        else
        {
            activeUntil.setText("");
        }
    }

    private void displayTargetPrice()
    {
        if (alertDTO.priceMovement == null)
        {
            THSignedNumber thTargetPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.targetPrice, false);
            targetPrice.setText(thTargetPrice.toString());
            targetPriceLabel.setText(getString(R.string.target_price));
        }
        else
        {
            THSignedNumber thPriceMovement = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, alertDTO.priceMovement * 100);
            targetPrice.setText(thPriceMovement.toString(0));
            targetPriceLabel.setText(getString(R.string.percentage_movement));
        }
    }

    private Spanned getFormattedTargetPriceChange(String targetPriceString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.target_price_change_format), targetPriceString));
    }

    private Spanned getFormattedPercentageChange(String percentageString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.percentage_change_format), percentageString));
    }

    private void displayCurrentPrice()
    {
        THSignedNumber thCurrentPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, securityCompactDTO.lastPrice, false);
        currentPrice.setText(thCurrentPrice.toString());
    }

    private void displayCompanyName()
    {
        companyName.setText(securityCompactDTO.name);
    }

    private void displayStockSymbol()
    {
        stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
    }

    private void displayStockLogo()
    {
        if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
        {
            picasso.get()
                    .load(securityCompactDTO.imageBlobUrl)
                    .transform(new WhiteToTransparentTransformation())
                    .into(stockLogo);
        }
        else if (securityCompactDTO != null)
        {
            picasso.get()
                    .load(securityCompactDTO.getExchangeLogoId())
                    .into(stockLogo);
        }
        else
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }

    private THCallback<AlertDTO> alertCallback = new THCallback<AlertDTO>()
    {
        @Override protected void success(AlertDTO alertDTO, THResponse thResponse)
        {
            if (alertDTO != null)
            {
                linkWith(alertDTO, true);
            }
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }
    };

    //region handling percentage changes

    private SeekBar.OnSeekBarChangeListener percentageSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
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

    private CompoundButton.OnCheckedChangeListener percentageCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            handlePercentageCheckedChange(isChecked);
        }
    };

    private void handlePercentageCheckedChange(boolean isChecked)
    {
        if (isChecked && targetPriceToggle.isChecked())
        {
            targetPriceToggle.setChecked(false);
        }

        percentageSeekBar.setEnabled(isChecked);

        updatePercentageChangeValues(isChecked);
    }

    private void updatePercentageChangeValues(boolean isChecked)
    {
        THSignedNumber thPercentageChange = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, (double) getSeekingMovementPercentage(), true);
        percentageChange.setText(getFormattedPercentageChange(isChecked ? thPercentageChange.toString(0) : "-"));

        if (securityCompactDTO != null && securityCompactDTO.lastPrice != null)
        {
            THSignedNumber thPercentageChangePriceValue = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    getSeekingMovementPrice(),
                    false
            );
            percentageChangePriceValue.setText(getFormattedPercentageChangeTargetValue(isChecked ? thPercentageChangePriceValue.toString() : "-"));
        }

        percentageSeekBar.setEnabled(targetPercentageChangeToggle.isChecked());
    }

    private double getSeekingMovementPrice()
    {
        return securityCompactDTO.lastPrice + securityCompactDTO.lastPrice * percentageSeekBar.getProgress() / 100;
    }

    private int getSeekingMovementPercentage()
    {
        return percentageSeekBar.getProgress() - 50;
    }

    private Spanned getFormattedPercentageChangeTargetValue(String percentageChangeTargetValueString)
    {
        return Html.fromHtml(
                String.format(getString(R.string.percentage_change_target_value_format), percentageChangeTargetValueString));
    }
    //endregion

    //region Handling target price changes
    private CompoundButton.OnCheckedChangeListener targetPriceCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            handleTargetPriceCheckedChange(isChecked);
        }
    };

    private void handleTargetPriceCheckedChange(boolean isChecked)
    {
        if (isChecked && targetPercentageChangeToggle.isChecked())
        {
            targetPercentageChangeToggle.setChecked(false);
        }
        targetPriceSeekBar.setEnabled(isChecked);

        updateTargetPriceChangeValues(isChecked);
    }

    private void updateTargetPriceChangeValues(boolean handlerEnabled)
    {
        THSignedNumber thSignedNumber = new THSignedNumber(THSignedNumber.TYPE_MONEY, getSeekingTargetPrice(), false);
        targetPriceChange.setText(getFormattedTargetPriceChange(handlerEnabled ? thSignedNumber.toString() : "-"));

        targetPriceSeekBar.setEnabled(targetPriceToggle.isChecked());
    }

    private Double getSeekingTargetPrice()
    {
        return (securityCompactDTO.lastPrice * 2) * targetPriceSeekBar.getProgress() / targetPriceSeekBar.getMax();
    }

    private SeekBar.OnSeekBarChangeListener priceSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener()
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
    //endregion

    private THCallback<AlertCompactDTO> alertUpdateCallback = new THCallback<AlertCompactDTO>()
    {
        @Override protected void finish()
        {
            progressDialog.hide();
        }

        @Override protected void success(AlertCompactDTO alertCompactDTO, THResponse thResponse)
        {
            alertCompactCache.get().put(alertCompactDTO.getAlertId(currentUserBaseKeyHolder.getCurrentUserBaseKey().key), alertCompactDTO);
            getNavigator().popFragment();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }
    };

    private DTOCache.Listener<SecurityId, SecurityCompactDTO> securityCompactCallback = new DTOCache.Listener<SecurityId, SecurityCompactDTO>()
    {
        @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value, boolean fromCache)
        {
            hideDialog();
            securityCompactCache.get().put(key, value);
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
    };

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
