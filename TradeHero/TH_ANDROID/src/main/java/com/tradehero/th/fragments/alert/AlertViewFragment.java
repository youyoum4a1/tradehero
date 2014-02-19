package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCache;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/28/14 Time: 12:45 PM Copyright (c) TradeHero
 */
public class AlertViewFragment extends BasePurchaseManagerFragment
{
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = AlertViewFragment.class.getName() + ".alertId";

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.target_price_label) TextView targetPriceLabel;
    @InjectView(R.id.current_price) TextView currentPrice;
    @InjectView(R.id.active_until) TextView activeUntil;
    @InjectView(R.id.alert_toggle) Switch alertToggle;

    private StickyListHeadersListView priceChangeHistoryList;

    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<AlertCache> alertCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<PrettyTime> prettyTime;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected CurrentUserId currentUserId;

    private View headerView;
    private AlertDTO alertDTO;
    private SecurityCompactDTO securityCompactDTO;
    private AlertEventAdapter alertEventAdapter;
    private AlertId alertId;
    private ActionBar actionBar;
    private ProgressDialog progressDialog;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        priceChangeHistoryList = (StickyListHeadersListView) inflater.inflate(R.layout.alert_view_fragment, container, false);
        headerView = inflater.inflate(R.layout.alert_security_profile, null);
        ButterKnife.inject(this, headerView);
        return priceChangeHistoryList;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        priceChangeHistoryList.addHeaderView(headerView);
        alertEventAdapter = new AlertEventAdapter(getActivity(), getActivity().getLayoutInflater(),
                R.layout.alert_event_item_view);
        priceChangeHistoryList.setAdapter(alertEventAdapter);
    }

    @Override public void onDestroyView()
    {
        priceChangeHistoryList.removeHeaderView(headerView);
        alertToggle.setOnCheckedChangeListener(null);
        super.onDestroyView();
    }

    @Override protected void initViews(View view)
    {

    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            alertId = new AlertId(args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE));
            linkWith(alertId, true);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        actionBar = getSherlockActivity().getSupportActionBar();
        inflater.inflate(R.menu.alert_view_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_edit:
                if (alertId != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putBundle(AlertEditFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
                    bundle.putBundle(AlertEditFragment.BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
                    getDashboardNavigator().pushFragment(AlertEditFragment.class, bundle, Navigator.PUSH_UP_FROM_BOTTOM);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void linkWith(AlertId alertId, boolean andDisplay)
    {
        if (alertId != null)
        {

            AlertDTO cachedAlertDTO = alertCache.get().get(alertId);
            if (cachedAlertDTO != null)
            {
                linkWith(cachedAlertDTO, true);
            }
            else
            {
                progressDialog = ProgressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
            }
            alertServiceWrapper.get().getAlert(alertId, alertCallback);
        }
    }

    private void linkWith(AlertDTO alertDTO, boolean andDisplay)
    {
        this.alertDTO = alertDTO;

        alertEventAdapter.setItems(alertDTO.alertEvents);
        alertEventAdapter.notifyDataSetChanged();

        if (alertDTO.security != null)
        {
            linkWith(alertDTO.security, andDisplay);
        }

        if (andDisplay)
        {
            displayCurrentPrice();

            displayTargetPrice();

            displayToggle();

            displayActiveUntil();
        }
    }

    private void displayToggle()
    {
        alertToggle.setChecked(alertDTO.active);
        alertToggle.setOnCheckedChangeListener(alertToggleCheckedChangeListener);
        alertToggle.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());

                if (alertToggle.isChecked() && userProfileDTO.alertCount >= userProfileDTO.getUserAlertPlansAlertCount())
                {
                    userInteractor.conditionalPopBuyStockAlerts();
                    alertToggle.setChecked(false);
                }
            }
        });
    }

    private void linkWith(SecurityCompactDTO security, boolean andDisplay)
    {
        this.securityCompactDTO = security;

        if (andDisplay)
        {
            displayStockLogo();

            displayStockSymbol();

            displayCompanyName();
        }
    }

    private void displayActiveUntil()
    {
        if (!alertToggle.isChecked())
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
            targetPriceLabel.setText(getString(R.string.stock_alert_target_price));
        }
        else
        {
            THSignedNumber thPriceMovement = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, alertDTO.priceMovement * 100);
            targetPrice.setText(thPriceMovement.toString(0));
            targetPriceLabel.setText(getString(R.string.stock_alert_percentage_movement));
        }
    }

    private void displayCurrentPrice()
    {
        THSignedNumber thCurrentPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.security.lastPrice, false);
        currentPrice.setText(thCurrentPrice.toString());
    }

    private void displayCompanyName()
    {
        companyName.setText(securityCompactDTO.name);
    }

    private void displayStockSymbol()
    {
        stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
        if (actionBar != null) actionBar.setTitle(securityCompactDTO.getExchangeSymbol());
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
        @Override protected void finish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
        }

        @Override protected void success(AlertDTO alertDTO, THResponse thResponse)
        {
            if (alertDTO != null)
            {
                alertCache.get().put(alertId, alertDTO);
                linkWith(alertDTO, true);
            }
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }
    };


    private CompoundButton.OnCheckedChangeListener alertToggleCheckedChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            handleAlertToggleChanged(isChecked);
        }
    };

    private void handleAlertToggleChanged(boolean alertActive)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());

        if (userProfileDTO != null)
        {
            progressDialog = ProgressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);

            if (alertDTO != null)
            {
                AlertFormDTO alertFormDTO = new AlertFormDTO();
                alertFormDTO.securityId = alertDTO.security.id;
                alertFormDTO.targetPrice = alertDTO.targetPrice;
                alertFormDTO.upOrDown = alertDTO.upOrDown;
                alertFormDTO.priceMovement = alertDTO.priceMovement;
                alertFormDTO.active = alertActive;
                alertServiceWrapper.get().updateAlert(alertId, alertFormDTO, alertUpdateCallback);
            }
        }
    }

    private THCallback<AlertCompactDTO> alertUpdateCallback = new THCallback<AlertCompactDTO>()
    {
        @Override protected void finish()
        {
            progressDialog.hide();
        }

        @Override protected void success(AlertCompactDTO alertCompactDTO, THResponse thResponse)
        {
            alertCompactCache.get().put(alertId, alertCompactDTO);
            displayActiveUntil();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }
    };

    //region TabBarInformer
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //endregion
}
