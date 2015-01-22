package com.tradehero.th.fragments.alert;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class AlertViewFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_ALERT_ID_BUNDLE = AlertViewFragment.class.getName() + ".alertId";

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.target_price_label) TextView targetPriceLabel;
    @InjectView(R.id.current_price) TextView currentPrice;
    @InjectView(R.id.active_until) TextView activeUntil;
    @InjectView(R.id.alert_toggle) Switch alertToggle;

    private StickyListHeadersListView priceChangeHistoryList;

    @Inject protected Lazy<AlertCacheRx> alertCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected ProgressDialogUtil progressDialogUtil;
    @Inject protected Lazy<UserProfileCacheRx> userProfileCache;
    @Inject protected CurrentUserId currentUserId;

    private View headerView;
    @Nullable protected Subscription alertCacheSubscription;
    @Nullable protected Subscription updateAlertSubscription;
    protected AlertDTO alertDTO;
    private SecurityCompactDTO securityCompactDTO;
    private AlertEventAdapter alertEventAdapter;
    private AlertId alertId;
    private ProgressDialog progressDialog;

    private CompoundButton.OnCheckedChangeListener alertToggleCheckedChangeListener;

    public static void putAlertId(@NonNull Bundle args, @NonNull AlertId alertId)
    {
        args.putBundle(BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
    }

    @NonNull public static AlertId getAlertId(@NonNull Bundle args)
    {
        return new AlertId(args.getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alertToggleCheckedChangeListener = (buttonView, isChecked) -> handleAlertToggleChanged(isChecked);
    }

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
        alertEventAdapter = new AlertEventAdapter(getActivity(),
                R.layout.alert_event_item_view);
        priceChangeHistoryList.setAdapter(alertEventAdapter);
        priceChangeHistoryList.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(getAlertId(getArguments()));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.alert_view_menu, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.alert_menu_edit:
                Bundle bundle = new Bundle();
                OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
                if (applicablePortfolioId != null)
                {
                    AlertEditFragment.putApplicablePortfolioId(bundle, applicablePortfolioId);
                }
                AlertEditFragment.putAlertId(bundle, alertId);
                navigator.get().pushFragment(AlertEditFragment.class, bundle, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        unsubscribe(alertCacheSubscription);
        alertCacheSubscription = null;
        unsubscribe(updateAlertSubscription);
        updateAlertSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        priceChangeHistoryList.removeHeaderView(headerView);
        priceChangeHistoryList.setOnScrollListener(null);
        alertToggle.setOnCheckedChangeListener(null);
        alertToggle.setOnClickListener(null);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        alertToggleCheckedChangeListener = null;
        super.onDestroy();
    }

    private void linkWith(AlertId alertId)
    {
        this.alertId = alertId;
        if (alertId != null)
        {
            progressDialog = progressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
            progressDialog.setCanceledOnTouchOutside(true);
            unsubscribe(alertCacheSubscription);
            alertCacheSubscription = AndroidObservable.bindFragment(this,
                    alertCache.get().get(alertId))
                    .map(pair -> pair.second)
                    .finallyDo(this::hideProgressDialog)
                    .subscribe(
                            this::linkWith,
                            e -> {
                                THToast.show(R.string.error_fetch_alert);
                                Timber.e(e, "Failed fetching alert");
                            });
        }
    }

    private void linkWith(AlertDTO alertDTO)
    {
        this.alertDTO = alertDTO;

        alertEventAdapter.clear();
        if (alertDTO.alertEvents != null)
        {
            alertEventAdapter.addAll(alertDTO.alertEvents);
        }
        alertEventAdapter.notifyDataSetChanged();

        if (alertDTO.security != null)
        {
            linkWith(alertDTO.security);
        }

        displayCurrentPrice();
        displayTargetPrice();
        displayToggle();
        displayActiveUntil();
    }

    private void displayToggle()
    {
        alertToggle.setChecked(alertDTO.active);
        alertToggle.setOnCheckedChangeListener(alertToggleCheckedChangeListener);
        alertToggle.setOnClickListener(v -> {
            UserProfileDTO userProfileDTO = userProfileCache.get().getCachedValue(currentUserId.toUserBaseKey());

            if (alertToggle.isChecked()
                    && userProfileDTO != null
                    && userProfileDTO.alertCount >= userProfileDTO.getUserAlertPlansAlertCount())
            {
                // TODO
                //userInteractor.conditionalPopBuyStockAlerts();
                alertToggle.setChecked(false);
            }
        });
    }

    private void linkWith(SecurityCompactDTO security)
    {
        this.securityCompactDTO = security;

        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
    }

    private void displayActiveUntil()
    {
        if (!alertToggle.isChecked())
        {
            activeUntil.setText("-");
        }
        else if (alertDTO.activeUntilDate != null)
        {
            activeUntil.setText(DateUtils.getDisplayableDate(getResources(), alertDTO.activeUntilDate));
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
            THSignedMoney
                    .builder(alertDTO.targetPrice)
                    .withOutSign()
                    .build()
                    .into(targetPrice);
            targetPriceLabel.setText(getString(R.string.stock_alert_target_price));
        }
        else
        {
            THSignedPercentage
                    .builder(alertDTO.priceMovement * 100)
                    .build()
                    .into(targetPrice);
            targetPriceLabel.setText(getString(R.string.stock_alert_percentage_movement));
        }
    }

    private void displayCurrentPrice()
    {
        if (alertDTO.security != null)
        {
            THSignedMoney
                    .builder(alertDTO.security.lastPrice)
                    .withOutSign()
                    .build()
                    .into(currentPrice);
        }
    }

    private void displayCompanyName()
    {
        companyName.setText(securityCompactDTO.name);
    }

    private void displayStockSymbol()
    {
        stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
        setActionBarTitle(securityCompactDTO.getExchangeSymbol());
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

    private void handleAlertToggleChanged(boolean alertActive)
    {
        progressDialog = progressDialogUtil.show(getActivity(), R.string.loading_loading, R.string.alert_dialog_please_wait);
        progressDialog.setCanceledOnTouchOutside(true);

        if (alertDTO != null)
        {
            AlertFormDTO alertFormDTO = new AlertFormDTO();
            alertFormDTO.securityId = alertDTO.security.id;
            alertFormDTO.targetPrice = alertDTO.targetPrice;
            alertFormDTO.upOrDown = alertDTO.upOrDown;
            alertFormDTO.priceMovement = alertDTO.priceMovement;
            alertFormDTO.active = alertActive;
            unsubscribe(updateAlertSubscription);
            updateAlertSubscription = AndroidObservable.bindFragment(
                    this,
                    alertServiceWrapper.get().updateAlertRx(alertId, alertFormDTO))
                    .finallyDo(this::hideProgressDialog)
                    .subscribe(
                            this::handleAlertUpdated,
                            e -> THToast.show(new THException(e)));
        }
    }

    public void handleAlertUpdated(@NonNull AlertCompactDTO alertCompactDTO)
    {
        displayActiveUntil();
    }

    protected void hideProgressDialog()
    {
        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }
}
