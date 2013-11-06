package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.adapters.billing.StoreItemAdapter;
import com.tradehero.th.adapters.billing.THSKUDetailsAdapter;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.List;

public class StoreScreenFragment extends DashboardFragment
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

    private void handlePositionClicked(int position)
    {
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                popBuyVirtualDollars();
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                popBuyFollowCredits();
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                popBuyStockAlerts();
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                popBuyResetPortfolio();
                break;

            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

    private void popBuyVirtualDollars()
    {
        popBuyDialog(THSKUDetails.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title);
    }

    private void popBuyFollowCredits()
    {
        popBuyDialog(THSKUDetails.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message);
    }

    private void popBuyStockAlerts()
    {
        popBuyDialog(THSKUDetails.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title);
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

        popBuyDialog(detailsAdapter, titleResId);
    }

    private void popBuyDialog(final THSKUDetailsAdapter detailsAdapter, int titleResId)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set dialog message
        alertDialogBuilder
                .setTitle(titleResId)
                .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (i > 0)
                        {
                            dialogItemClicked(dialogInterface, i, (THSKUDetails) detailsAdapter.getItem(i));
                            dialogInterface.cancel();
                        }
                        else
                        {
                            THToast.show("Only the message pressed");
                        }
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void dialogItemClicked(DialogInterface dialogInterface, int i, THSKUDetails item)
    {
        THToast.show("Sku clicked " + item.getProductIdentifier().identifier);
    }
}
