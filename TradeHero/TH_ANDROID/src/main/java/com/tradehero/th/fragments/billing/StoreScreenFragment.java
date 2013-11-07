package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.googleplay.SKUDetails;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.adapters.billing.StoreItemAdapter;
import com.tradehero.th.adapters.billing.THSKUDetailsAdapter;
import com.tradehero.th.billing.googleplay.IABAlertUtils;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.List;

public class StoreScreenFragment extends DashboardFragment
    implements IABAlertUtils.OnDialogSKUDetailsClickListener<THSKUDetails>
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity(), getActivity().getLayoutInflater());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.store_option_menu_title); // Add the changing cute icon
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        storeItemAdapter.notifyDataSetChanged();
        if (!isBillingAvailable())
        {
            IABAlertUtils.popBillingUnavailable(getActivity());
        }
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
            listView.setAdapter(null);
        }
        listView = null;
        storeItemAdapter = null;
        super.onDestroyView();
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }

    private boolean isBillingAvailable()
    {
        return ((DashboardActivity) getActivity()).isBillingAvailable();
    }

    private boolean hadErrorLoadingInventory()
    {
        return ((DashboardActivity) getActivity()).hadErrorLoadingInventory();
    }

    private boolean isInventoryReady()
    {
        return ((DashboardActivity) getActivity()).isInventoryReady();
    }

    private void popErrorWhenLoading()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder
                .setTitle(R.string.store_billing_error_loading_window_title)
                .setMessage(R.string.store_billing_error_loading_window_description)
                .setCancelable(true)
                .setPositiveButton(R.string.store_billing_error_loading_act, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ((DashboardActivity) getActivity()).launchSkuInventorySequence();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void handlePositionClicked(int position)
    {
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                conditionalPopBuyVirtualDollars();
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                conditionalPopBuyFollowCredits();
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                conditionalPopBuyStockAlerts();
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                conditionalPopBuyResetPortfolio();
                break;

            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

    private boolean popErrorConditional()
    {
        if (!isBillingAvailable())
        {
            IABAlertUtils.popBillingUnavailable(getActivity());
        }
        else if (hadErrorLoadingInventory())
        {
            popErrorWhenLoading();
        }
        else if (!isInventoryReady())
        {
            IABAlertUtils.popWaitWhileLoading(getActivity());
        }
        else
        {
            // All clear
            return false;
        }
        return true;
    }

    private void conditionalPopBuyVirtualDollars()
    {
        if (!popErrorConditional())
        {
            popBuyVirtualDollars();
        }
    }

    private void popBuyVirtualDollars()
    {
        popBuyDialog(THSKUDetails.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title);
    }

    private void conditionalPopBuyFollowCredits()
    {
        if (!popErrorConditional())
        {
            popBuyFollowCredits();
        }
    }

    private void popBuyFollowCredits()
    {
        popBuyDialog(THSKUDetails.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message);
    }

    private void conditionalPopBuyStockAlerts()
    {
        if (!popErrorConditional())
        {
            popBuyStockAlerts();
        }
    }

    private void popBuyStockAlerts()
    {
        popBuyDialog(THSKUDetails.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title);
    }

    private void conditionalPopBuyResetPortfolio()
    {
        if (!popErrorConditional())
        {
            popBuyResetPortfolio();
        }
    }

    private void popBuyResetPortfolio()
    {
        popBuyDialog(THSKUDetails.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title);
    }

    private void popBuyDialog(String skuDomain, int titleResId)
    {
        final THSKUDetailsAdapter detailsAdapter = new THSKUDetailsAdapter(getActivity(), getActivity().getLayoutInflater(), skuDomain);
        List<THSKUDetails> desiredSkuDetails = ((DashboardActivity) getActivity()).getDetailsOfDomain(skuDomain);
        detailsAdapter.setItems(desiredSkuDetails);

        IABAlertUtils.popBuyDialog(getActivity(), detailsAdapter, titleResId, this);
    }

    //<editor-fold desc="IABAlertUtils.OnDialogSKUDetailsClickListener">
    @Override public void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, THSKUDetails skuDetails)
    {
        THToast.show("Sku clicked " + skuDetails.getProductIdentifier().identifier);
        ((DashboardActivity) getActivity()).launchPurchaseSequence(skuDetails, "From store");
    }
    //</editor-fold>
}
