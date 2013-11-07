package com.tradehero.th.billing.googleplay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import com.tradehero.common.billing.googleplay.SKUDetails;
import com.tradehero.th.R;
import com.tradehero.th.adapters.billing.SKUDetailsAdapter;
import com.tradehero.th.widget.billing.SKUDetailView;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 5:52 PM To change this template use File | Settings | File Templates. */
public class IABAlertUtils
{
    public static final String TAG = IABAlertUtils.class.getSimpleName();

    public static void popBillingUnavailable(final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.store_billing_unavailable_window_title)
                .setMessage(R.string.store_billing_unavailable_window_description)
                .setCancelable(true)
                .setNegativeButton(R.string.store_billing_unavailable_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.store_billing_unavailable_act, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
                        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Still cannot get it to go back to TradeHero with back button
                        context.startActivity(addAccountIntent);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void popWaitWhileLoading(final Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(R.string.store_billing_loading_window_title)
                .setMessage(R.string.store_billing_loading_window_description)
                .setCancelable(true)
                .setNegativeButton(R.string.store_billing_loading_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static <SKUDetailsType extends SKUDetails, SKUDetailViewType extends SKUDetailView<SKUDetailsType>> void popBuyDialog(
        final Context context, final SKUDetailsAdapter<SKUDetailsType, SKUDetailViewType> detailsAdapter,
        int titleResId, final OnDialogSKUDetailsClickListener<SKUDetailsType> clickListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog message
        alertDialogBuilder
                .setTitle(titleResId)
                .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (clickListener != null)
                        {
                            clickListener.onDialogSKUDetailsClicked(dialogInterface, i, (SKUDetailsType) detailsAdapter.getItem(i));
                        }
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true)
                .setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static interface OnDialogSKUDetailsClickListener<SKUDetailsType extends SKUDetails>
    {
        void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, SKUDetailsType skuDetails);
    }
}
