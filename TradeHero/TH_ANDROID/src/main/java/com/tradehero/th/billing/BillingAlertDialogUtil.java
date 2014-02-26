package com.tradehero.th.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
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
    public BillingAlertDialogUtil()
    {
        super();
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
            Activity activity,
            ProductDetailDomainInformerType domainInformer,
            OnDialogProductDetailClickListener<ProductDetailType> clickListener,
            String skuDomain,
            int titleResId,
            Runnable runOnPurchaseComplete)
    {
        return popBuyDialog(activity, domainInformer, clickListener, skuDomain, titleResId, runOnPurchaseComplete, getEnabledItems());
    }

    public AlertDialog popBuyDialog(
            Activity activity,
            ProductDetailDomainInformerType domainInformer,
            OnDialogProductDetailClickListener<ProductDetailType> clickListener,
            String skuDomain,
            int titleResId,
            Runnable runOnPurchaseComplete,
            Map<ProductIdentifier, Boolean> enabledItems)
    {
        final ProductDetailAdapterType detailAdapter = createProductDetailAdapter(activity, activity.getLayoutInflater(), skuDomain);
        detailAdapter.setEnabledItems(enabledItems);
        detailAdapter.setProductDetailComparator(createProductDetailComparator());
        List<ProductDetailType> desiredSkuDetails = domainInformer.getDetailsOfDomain(skuDomain);
        detailAdapter.setItems(desiredSkuDetails);

        return popBuyDialog(activity, detailAdapter, titleResId, clickListener, runOnPurchaseComplete);
    }

    abstract protected ProductDetailAdapterType createProductDetailAdapter(
            Activity activity,
            LayoutInflater layoutInflater,
            String skuDomain);
    abstract protected Comparator<ProductDetailType> createProductDetailComparator();

    public AlertDialog popBuyDialog(
            final Context context,
            final ProductDetailAdapterType detailsAdapter,
            int titleResId,
            final OnDialogProductDetailClickListener<ProductDetailType> clickListener,
            final Runnable runOnPurchaseComplete)
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
                                    dialogInterface,
                                    i,
                                    (ProductDetailType) detailsAdapter.getItem(i),
                                    runOnPurchaseComplete);
                        }
                        dialogInterface.cancel();
                    }
                })
                .setCancelable(true);
        //.setNegativeButton(R.string.store_buy_virtual_dollar_window_button_cancel, new DialogInterface.OnClickListener()
        //{
        //    public void onClick(DialogInterface dialog, int id)
        //    {
        //        dialog.cancel();
        //    }
        //});
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }

    public static interface OnDialogProductDetailClickListener<
            ProductDetailType extends ProductDetail>
    {
        void onDialogProductDetailClicked(DialogInterface dialogInterface, int position,
                ProductDetailType productDetail, Runnable runOnPurchaseComplete);
    }
    //</editor-fold>

    public AlertDialog popFailedToReport(final Context context)
    {
        return popWithNegativeButton(context, R.string.store_billing_report_api_error_window_title,
                R.string.store_billing_report_api_error_window_description,
                R.string.store_billing_report_api_error_cancel);
    }
}
