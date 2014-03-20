package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.ProductDetailAdapter;
import com.tradehero.th.fragments.billing.ProductDetailView;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BillingAlertDialogUtil<
        ProductIdentifierType extends ProductIdentifier,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        ProductDetailDomainInformerType extends ProductDetailDomainInformer<
                ProductIdentifierType,
                ProductDetailType>,
        ProductDetailViewType extends ProductDetailView<
                ProductIdentifierType,
                ProductDetailType>,
        ProductDetailAdapterType extends ProductDetailAdapter<
                ProductIdentifierType,
                ProductDetailType,
                ProductDetailViewType>>
        extends AlertDialogUtil
{
    protected LocalyticsSession localyticsSession;

    public BillingAlertDialogUtil(LocalyticsSession localyticsSession)
    {
        super();
        this.localyticsSession = localyticsSession;
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

    abstract public void goToCreateAccount(final Context context);
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
            OnDialogProductDetailClickListener<ProductDetailType> clickListener,
            ProductIdentifierDomain skuDomain,
            int titleResId)
    {
        return popBuyDialog(requestCode, activity, domainInformer, clickListener, skuDomain, titleResId, getEnabledItems());
    }

    public AlertDialog popBuyDialog(
            int requestCode,
            Activity activity,
            ProductDetailDomainInformerType domainInformer,
            OnDialogProductDetailClickListener<ProductDetailType> clickListener,
            ProductIdentifierDomain skuDomain,
            int titleResId,
            Map<ProductIdentifier, Boolean> enabledItems)
    {
        final ProductDetailAdapterType detailAdapter = createProductDetailAdapter(activity, activity.getLayoutInflater(), skuDomain);
        detailAdapter.setEnabledItems(enabledItems);
        detailAdapter.setProductDetailComparator(createProductDetailComparator());
        List<ProductDetailType> desiredSkuDetails = domainInformer.getDetailsOfDomain(skuDomain);
        detailAdapter.setItems(desiredSkuDetails);

        localyticsSession.tagEvent(skuDomain.localyticsShowTag);

        return popBuyDialog(requestCode, activity, detailAdapter, titleResId, clickListener);
    }

    abstract protected ProductDetailAdapterType createProductDetailAdapter(
            Activity activity,
            LayoutInflater layoutInflater,
            ProductIdentifierDomain skuDomain);
    abstract protected Comparator<ProductDetailType> createProductDetailComparator();

    public AlertDialog popBuyDialog(
            final int requestCode,
            final Context context,
            final ProductDetailAdapterType detailsAdapter,
            int titleResId,
            final OnDialogProductDetailClickListener<ProductDetailType> clickListener)
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
                                    (ProductDetailType) detailsAdapter.getItem(i)
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
}
