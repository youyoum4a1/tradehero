package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.IABAlertSKUUtils;
import com.tradehero.th.billing.googleplay.IABAlertUtils;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.lang.ref.WeakReference;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:05 AM To change this template use File | Settings | File Templates. */
abstract public class BasePurchaseManagerFragment extends DashboardFragment
        implements IABAlertUtils.OnDialogSKUDetailsClickListener<THSKUDetails>,
        THIABActorUser, THIABPurchaseHandler
{
    public static final String TAG = BasePurchaseManagerFragment.class.getSimpleName();

    protected WeakReference<THIABActor> billingActor = new WeakReference<>(null);
    protected int requestCode = (int) (Math.random() * Integer.MAX_VALUE);
    protected THSKUDetails skuDetails;

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setBillingActor((THIABActor) getActivity());
    }

    protected boolean isBillingAvailable()
    {
        return getBillingActor().isBillingAvailable();
    }

    protected boolean hadErrorLoadingInventory()
    {
        return getBillingActor().hadErrorLoadingInventory();
    }

    protected boolean isInventoryReady()
    {
        return getBillingActor().isInventoryReady();
    }

    //<editor-fold desc="THIABActorUser">
    public THIABActor getBillingActor()
    {
        return billingActor.get();
    }

    public void setBillingActor(THIABActor billingActor)
    {
        this.billingActor = new WeakReference<>(billingActor);
    }
    //</editor-fold>

    protected boolean popErrorConditional()
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

    protected void popErrorWhenLoading()
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
                        getBillingActor().launchSkuInventorySequence();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    protected void conditionalPopBuyVirtualDollars()
    {
        if (!popErrorConditional())
        {
            popBuyVirtualDollars();
        }
    }

    protected void popBuyVirtualDollars()
    {
        popBuyDialog(THSKUDetails.DOMAIN_VIRTUAL_DOLLAR, R.string.store_buy_virtual_dollar_window_title);
    }

    protected void conditionalPopBuyFollowCredits()
    {
        if (!popErrorConditional())
        {
            popBuyFollowCredits();
        }
    }

    protected void popBuyFollowCredits()
    {
        popBuyDialog(THSKUDetails.DOMAIN_FOLLOW_CREDITS, R.string.store_buy_follow_credits_window_message);
    }

    protected void conditionalPopBuyStockAlerts()
    {
        if (!popErrorConditional())
        {
            popBuyStockAlerts();
        }
    }

    protected void popBuyStockAlerts()
    {
        popBuyDialog(THSKUDetails.DOMAIN_STOCK_ALERTS, R.string.store_buy_stock_alerts_window_title);
    }

    protected void conditionalPopBuyResetPortfolio()
    {
        if (!popErrorConditional())
        {
            popBuyResetPortfolio();
        }
    }

    protected void popBuyResetPortfolio()
    {
        popBuyDialog(THSKUDetails.DOMAIN_RESET_PORTFOLIO, R.string.store_buy_reset_portfolio_window_title);
    }

    protected void popBuyDialog(String skuDomain, int titleResId)
    {
        IABAlertSKUUtils.popBuyDialog(getActivity(), getBillingActor(), this, skuDomain, titleResId);
    }

    //<editor-fold desc="THIABPurchaseHandler">
    @Override public void handlePurchaseReceived(int requestCode, SKUPurchase purchase)
    {
        if (this.requestCode != requestCode)
        {
            THLog.d(TAG, "handlePurchaseReceived. Received requestCode " + requestCode + ", when in fact it expects " + this.requestCode);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReceived. Received requestCode " + requestCode + ", purchase " + purchase);
        }
    }

    @Override public void handlePurchaseException(int requestCode, IABException exception)
    {
        if (this.requestCode != requestCode)
        {
            THLog.d(TAG, "handlePurchaseException. Received requestCode " + requestCode + ", when in fact it expects " + this.requestCode);
        }
        else if (exception instanceof IABVerificationFailedException)
        {
            IABAlertUtils.popVerificationFailed(getActivity());
        }
        else if (exception instanceof IABUserCancelledException)
        {
            IABAlertUtils.popUserCancelled(getActivity());
        }
        if (exception instanceof IABBadResponseException)
        {
            IABAlertUtils.popBadResponse(getActivity());
        }
        else if (exception instanceof IABRemoteException)
        {
            IABAlertUtils.popRemoteError(getActivity());
        }
        else if (exception instanceof IABAlreadyOwnedException)
        {
            IABAlertUtils.popSKUAlreadyOwned(getActivity(), skuDetails);
        }
        else if (exception instanceof IABSendIntentException)
        {
            IABAlertUtils.popSendIntent(getActivity());
        }
        else
        {
            IABAlertUtils.popUnknownError(getActivity());
        }
    }
    //</editor-fold>

    //<editor-fold desc="IABAlertUtils.OnDialogSKUDetailsClickListener">
    @Override public void onDialogSKUDetailsClicked(DialogInterface dialogInterface, int position, THSKUDetails skuDetails)
    {
        //THToast.show("Sku clicked " + skuDetails.getProductIdentifier().identifier);
        THIABActor actor = getBillingActor();
        if (actor != null)
        {
            this.requestCode = actor.launchPurchaseSequence(this, skuDetails, "From store");
            this.skuDetails = skuDetails;
        }
        else
        {
            THLog.d(TAG, "IABActor was null");
        }
    }
    //</editor-fold>
}
