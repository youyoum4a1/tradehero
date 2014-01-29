package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/28/14 Time: 12:45 PM Copyright (c) TradeHero
 */
public class AlertViewFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = AlertViewFragment.class.getName() + ".alertId";

    @InjectView(R.id.stock_logo) ImageView stockLogo;
    @InjectView(R.id.stock_symbol) TextView stockSymbol;
    @InjectView(R.id.company_name) TextView companyName;
    @InjectView(R.id.target_price) TextView targetPrice;
    @InjectView(R.id.current_price) TextView currentPrice;
    @InjectView(R.id.active_until) TextView activeUntil;
    @InjectView(R.id.alert_toggle) Switch alertToggle;

    private StickyListHeadersListView priceChangeHistoryList;

    @Inject protected Lazy<AlertCompactCache> alertCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Lazy<Picasso> picasso;
    @Inject Lazy<PrettyTime> prettyTime;

    private View headerView;
    private AlertDTO alertDTO;
    private SecurityCompactDTO securityCompactDTO;
    private AlertEventAdapter alertEventAdapter;
    private AlertId alertId;

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
        super.onDestroyView();
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
                    bundle.putBundle(AlertEditFragment.BUNDLE_KEY_ALERT_ID_BUNDLE, alertId.getArgs());
                    getNavigator().pushFragment(AlertEditFragment.class, bundle, Navigator.PUSH_UP_FROM_BOTTOM);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            displayActiveUntil();
        }
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
        THSignedNumber thTargetPrice = new THSignedNumber(THSignedNumber.TYPE_MONEY, alertDTO.targetPrice, false);
        targetPrice.setText(thTargetPrice.toString());
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
        getView().post(new Runnable()
        {
            @Override public void run()
            {
                getSherlockActivity().getSupportActionBar().setTitle(securityCompactDTO.getExchangeSymbol());
            }
        });
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

    //region TabBarInformer
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //endregion
}
