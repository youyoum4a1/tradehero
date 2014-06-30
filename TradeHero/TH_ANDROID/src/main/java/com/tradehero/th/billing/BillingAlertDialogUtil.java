package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.thm.R;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.VersionUtils;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class BillingAlertDialogUtil<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>,
        ProductDetailDomainInformerType extends ProductDetailDomainInformer<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                THProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                THProductDetailType,
                ProductDetailViewType>>
        extends AlertDialogUtil
{
    protected LocalyticsSession localyticsSession;
    public ActivityUtil activityUtil;

    public BillingAlertDialogUtil(LocalyticsSession localyticsSession, ActivityUtil activityUtil)
    {
        super();
        this.localyticsSession = localyticsSession;
        this.activityUtil = activityUtil;
    }

    public AlertDialog popWaitWhileLoading(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_loading_window_title,
                R.string.store_billing_loading_window_description,
                R.string.store_billing_loading_cancel);
    }

    //<editor-fold desc="Billing Available">
    public AlertDialog popBillingUnavailable(final Context context, String storeName)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.store_billing_unavailable_window_title),
                context.getString(R.string.store_billing_unavailable_window_description, storeName),
                R.string.store_billing_unavailable_act,
                R.string.store_billing_unavailable_cancel,
                new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        goToCreateAccount(context);
                    }
                });
    }

    public void goToCreateAccount(final Context context)
    {
        Intent addAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        addAccountIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Still cannot get it to go back to TradeHero with back button
        context.startActivity(addAccountIntent);
    }
    //</editor-fold>

    //<editor-fold desc="Product Detail Presentation">
    /**
     * By default, product identifiers that are not mentioned in the list are enabled.
     * @return
     */
    abstract public HashMap<ProductIdentifier, Boolean> getEnabledItems();

    public AlertDialog popBuyDialog(
            int requestCode,
            Activity activity,
            ProductDetailDomainInformerType domainInformer,
            OnDialogProductDetailClickListener<THProductDetailType> clickListener,
            ProductIdentifierDomain skuDomain,
            int titleResId)
    {
        return popBuyDialog(requestCode, activity, domainInformer, clickListener, skuDomain, titleResId, getEnabledItems());
    }

    public AlertDialog popBuyDialog(
            int requestCode,
            Activity activity,
            ProductDetailDomainInformerType domainInformer,
            OnDialogProductDetailClickListener<THProductDetailType> clickListener,
            ProductIdentifierDomain skuDomain,
            int titleResId,
            Map<ProductIdentifier, Boolean> enabledItems)
    {
        final ProductDetailAdapterType detailAdapter = createProductDetailAdapter(activity, activity.getLayoutInflater(), skuDomain);
        detailAdapter.setEnabledItems(enabledItems);
        detailAdapter.setProductDetailComparator(createProductDetailComparator());
        List<THProductDetailType> desiredSkuDetails = domainInformer.getDetailsOfDomain(skuDomain);
        detailAdapter.setItems(desiredSkuDetails);

        localyticsSession.tagEvent(skuDomain.localyticsShowTag);

        return popBuyDialog(requestCode, activity, detailAdapter, titleResId, clickListener);
    }

    abstract protected ProductDetailAdapterType createProductDetailAdapter(
            Activity activity,
            LayoutInflater layoutInflater,
            ProductIdentifierDomain skuDomain);
    abstract protected Comparator<THProductDetailType> createProductDetailComparator();

    // TODO use parent's popWithNegativeButton
    public AlertDialog popBuyDialog(
            final int requestCode,
            final Context context,
            final ProductDetailAdapterType detailsAdapter,
            int titleResId,
            final OnDialogProductDetailClickListener<THProductDetailType> clickListener)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setTitle(titleResId)
                .setIcon(R.drawable.th_app_logo)
                .setSingleChoiceItems(detailsAdapter, 0, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (clickListener != null)
                        {
                            clickListener.onDialogProductDetailClicked(
                                    requestCode,
                                    dialogInterface,
                                    i,
                                    (THProductDetailType) detailsAdapter.getItem(i)
                            );
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
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    public static interface OnDialogProductDetailClickListener<ProductDetailType extends ProductDetail>
    {
        void onDialogProductDetailClicked(
                int requestCode,
                DialogInterface dialogInterface,
                int position,
                ProductDetailType productDetail);
    }
    //</editor-fold>

    public AlertDialog popFailedToReport(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_report_api_error_window_title,
                R.string.store_billing_report_api_error_window_description,
                R.string.store_billing_report_api_error_cancel);
    }

    public AlertDialog popSendEmailSupportReportFailed(final Context context, final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context,
                R.string.google_play_send_support_email_report_fail_title,
                R.string.google_play_send_support_email_report_fail_message,
                R.string.google_play_send_support_email_report_fail_ok,
                R.string.google_play_send_support_email_report_fail_cancel,
                okClickListener);
    }

    public AlertDialog popUnknownError(final Context context, final Exception exception)
    {
        return popWithOkCancelButton(context,
                R.string.store_billing_unknown_error_window_title,
                R.string.store_billing_unknown_error_window_description,
                R.string.store_billing_unknown_error_ok,
                R.string.store_billing_unknown_error_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        sendSupportEmailBillingUnknownError(context, exception);
                    }
                });
    }

    public void sendSupportEmailBillingUnknownError(final Context context, final Exception exception)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                VersionUtils.getExceptionStringsAndTraceParameters(context, exception));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an unidentified error");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

}
