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
import com.tradehero.common.billing.samsung.BaseSamsungProductDetail;
import com.tradehero.th.R;
import com.tradehero.th.billing.googleplay.THIABProductDetail;
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
        ProductDetailDomainInformerType extends THProductDetailDomainInformer<
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

    protected Comparator<THProductDetailType> createProductDetailComparator()
    {
        return new THProductDetailDecreasingPriceComparator<>();
    }

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

    public AlertDialog popFailedToLoadRequiredInfo(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_load_info_error_window_title,
                R.string.store_billing_load_info_error_window_description,
                R.string.store_billing_load_info_error_cancel);
    }

    public AlertDialog popSKUAlreadyOwned(final Context context)
    {
        return popSKUAlreadyOwned(context, null, null);
    }

    public AlertDialog popSKUAlreadyOwned(
            final Context context,
            THProductDetailType skuDetails,
            DialogInterface.OnClickListener restoreClickListener)
    {
        return popWithOkCancelButton(context,
                skuDetails == null ?
                        context.getString(R.string.store_billing_sku_already_owned_window_title) :
                        String.format(context.getString(
                                        R.string.store_billing_sku_already_owned_name_window_title),
                                skuDetails.getDescription()
                        ),
                context.getString(R.string.store_billing_sku_already_owned_window_description),
                R.string.store_billing_sku_already_owned_ok,
                R.string.store_billing_sku_already_owned_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        sendSupportEmailPurchaseNotRestored(context);
                    }
                },
                restoreClickListener
        );
    }

    public void sendSupportEmailPurchaseNotRestored(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "My purchase is not being handled even after restart");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public AlertDialog popFailedToReport(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_report_api_error_window_title,
                R.string.store_billing_report_api_error_window_description,
                R.string.store_billing_report_api_error_cancel);
    }

    public AlertDialog popSendEmailSupportReportFailed(final Context context, final DialogInterface.OnClickListener okClickListener)
    {
        return popWithOkCancelButton(context,
                R.string.iap_send_support_email_report_fail_title,
                R.string.iap_send_support_email_report_fail_message,
                R.string.iap_send_support_email_report_fail_ok,
                R.string.iap_send_support_email_report_fail_cancel,
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
                }
        );
    }

    public void sendSupportEmailBillingUnknownError(final Context context, final Exception exception)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(
                VersionUtils.getExceptionStringsAndTraceParameters(context, exception));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "There was an unidentified error");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public AlertDialog popRemoteError(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_remote_error_window_title,
                R.string.store_billing_remote_error_window_description,
                R.string.store_billing_remote_error_cancel);
    }

    public AlertDialog popUserCancelled(final Context context)
    {
        return popWithOkCancelButton(context,
                R.string.store_billing_user_cancelled_window_title,
                R.string.store_billing_user_cancelled_window_description,
                R.string.store_billing_user_cancelled_ok,
                R.string.store_billing_user_cancelled_cancel,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                        sendSupportEmailCancelledPurchase(context);
                    }
                });
    }

    public void sendSupportEmailCancelledPurchase(final Context context)
    {
        Intent emailIntent = VersionUtils.getSupportEmailIntent(context, true);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "I cancelled the purchase");
        activityUtil.sendSupportEmail(context, emailIntent);
    }

    public AlertDialog popNoPurchaseToRestore(final Context context)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.iap_purchase_restored_none_title),
                context.getString(R.string.iap_purchase_restored_none_message),
                context.getString(R.string.iap_purchase_restored_none_cancel));
    }

    public AlertDialog popPurchasesRestored(final Context context, final int countOk)
    {
        return popWithNegativeButton(context,
                context.getString(R.string.iap_purchase_restored_title),
                context.getString(R.string.iap_purchase_restored_message, countOk),
                context.getString(R.string.iap_purchase_restored_cancel));
    }

    public AlertDialog popSendEmailSupportRestorePartiallyFailed(final Context context, final DialogInterface.OnClickListener clickListener, final int countOk, final int countFailed)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.iap_send_support_email_restore_fail_partial_title),
                context.getString(R.string.iap_send_support_email_restore_fail_partial_message, countOk, countFailed),
                R.string.iap_send_support_email_restore_fail_partial_ok,
                R.string.iap_send_support_email_restore_fail_partial_cancel,
                clickListener);
    }

    public AlertDialog popSendEmailSupportRestoreFailed(final Context context, int count, final DialogInterface.OnClickListener clickListener)
    {
        return popWithOkCancelButton(context,
                context.getString(R.string.iap_send_support_email_restore_fail_title),
                context.getString(R.string.iap_send_support_email_restore_fail_message, count),
                R.string.iap_send_support_email_restore_fail_ok,
                R.string.iap_send_support_email_restore_fail_cancel,
                clickListener);
    }


}
