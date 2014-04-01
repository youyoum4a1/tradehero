package com.tradehero.th.billing.samsung;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.BillingAlertDialogUtil;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCache;
import com.tradehero.th.fragments.billing.samsung.THSamsungSKUDetailAdapter;
import com.tradehero.th.fragments.billing.samsung.THSamsungStoreProductDetailView;
import com.tradehero.th.utils.ActivityUtil;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/7/13 Time: 5:52 PM To change this template use File | Settings | File Templates. */
public class THSamsungAlertDialogUtil extends BillingAlertDialogUtil<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungLogicHolder,
        THSamsungStoreProductDetailView,
        THSamsungSKUDetailAdapter>
{
    public static final String TAG = THSamsungAlertDialogUtil.class.getSimpleName();

    protected THSamsungPurchaseCache thSamsungPurchaseCache;
    protected SamsungStoreUtils samsungStoreUtils;

    @Inject public THSamsungAlertDialogUtil(LocalyticsSession localyticsSession,
            ActivityUtil activityUtil, THSamsungPurchaseCache thSamsungPurchaseCache,
            SamsungStoreUtils samsungStoreUtils)
    {
        super(localyticsSession, activityUtil);
        this.thSamsungPurchaseCache = thSamsungPurchaseCache;
        this.samsungStoreUtils = samsungStoreUtils;
    }

    //<editor-fold desc="SKU related">
    @Override protected THSamsungSKUDetailAdapter createProductDetailAdapter(Activity activity,
            LayoutInflater layoutInflater, ProductIdentifierDomain skuDomain)
    {
        return new THSamsungSKUDetailAdapter(activity, layoutInflater, skuDomain);
    }

    @Override public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();

        for (THSamsungPurchase key : thSamsungPurchaseCache.getValues())
        {
            Timber.d("Disabling %s", key);
            enabledItems.put(key.getProductIdentifier(), false);
        }

        if (enabledItems.size() == 0)
        {
            enabledItems = null;
        }
        return enabledItems;
    }
    //</editor-fold>

    //<editor-fold desc="Restore Related">
    @Deprecated // TODO user list of exceptions
    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<? extends ProductPurchase> restored, List<? extends ProductPurchase> restoreFailed, final DialogInterface.OnClickListener clickListener)
    {
        return handlePurchaseRestoreFinished(context, restored, restoreFailed, clickListener, false);
    }

    @Deprecated // TODO user list of exceptions
    public AlertDialog handlePurchaseRestoreFinished(final Context context, List<? extends ProductPurchase> restored, List<? extends ProductPurchase> restoreFailed, final DialogInterface.OnClickListener clickListener, boolean verbose)
    {
        int countOk = (restored == null ? 0 : restored.size());
        int countRestoreFailed = (restoreFailed == null ? 0 : restoreFailed.size());

        AlertDialog alertDialog = null;
        if (countRestoreFailed == 0 && countOk > 0)
        {
            alertDialog = popPurchasesRestored(context, countOk);
        }
        else if (countRestoreFailed > 0 && countOk == 0)
        {
            // TODO
            alertDialog = popSendEmailSupportRestoreFailed(context, countRestoreFailed, clickListener);
        }
        else if (countRestoreFailed > 0)
        {
            alertDialog = popSendEmailSupportRestorePartiallyFailed(context, clickListener, countOk, countRestoreFailed);
        }
        else if (verbose && countRestoreFailed == 0 && countOk == 0)
        {
            alertDialog = popNoPurchaseToRestore(context);
        }

        if (alertDialog != null)
        {
            alertDialog.setCanceledOnTouchOutside(true);
        }
        Timber.d("Restored purchases: %d, failed restore: %d", countOk, countRestoreFailed);
        return alertDialog;
    }

    public DialogInterface.OnClickListener createFailedRestoreClickListener(final Context context, final Exception exception)
    {
        return new DialogInterface.OnClickListener()
        {
            @Override public void onClick(DialogInterface dialog, int which)
            {
                sendSupportEmailRestoreFailed(context, exception);
            }
        };
    }

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                samsungStoreUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
    //</editor-fold>

}
