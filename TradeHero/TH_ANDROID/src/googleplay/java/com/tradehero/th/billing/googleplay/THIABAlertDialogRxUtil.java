package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABInvalidConsumptionException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.googleplay.exception.IABResultErrorException;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingAlertDialogRxUtil;
import com.tradehero.th.fragments.billing.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THIABStoreProductDetailView;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import com.tradehero.th.rx.dialog.AlertDialogButtonConstants;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.utils.ActivityUtil;
import java.util.HashMap;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class THIABAlertDialogRxUtil
        extends THBillingAlertDialogRxUtil<
        IABSKU,
        THIABProductDetail,
        THIABLogicHolderRx,
        THIABStoreProductDetailView,
        THIABSKUDetailAdapter>
{
    @NonNull private Analytics analytics;
    @NonNull private ActivityUtil activityUtil;
    @NonNull protected THIABPurchaseCacheRx thiabPurchaseCache;
    @NonNull protected GooglePlayUtils googlePlayUtils;

    //<editor-fold desc="Constructors">
    @Inject public THIABAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull ActivityUtil activityUtil,
            @NonNull THIABPurchaseCacheRx thiabPurchaseCache,
            @NonNull GooglePlayUtils googlePlayUtils)
    {
        super();
        this.analytics = analytics;
        this.activityUtil = activityUtil;
        this.thiabPurchaseCache = thiabPurchaseCache;
        this.googlePlayUtils = googlePlayUtils;
    }
    //</editor-fold>

    @Override @NonNull public Observable<Pair<DialogInterface, Integer>> popError(
            @NonNull final Context activityContext,
            @NonNull final Throwable exception)
    {
        if (exception instanceof IABBadResponseException)
        {
            return popBadResponse(activityContext);
        }
        if (exception instanceof IABResultErrorException)
        {
            return popResultError(activityContext);
        }
        if (exception instanceof IABRemoteException)
        {
            return popRemoteError(activityContext);
        }
        if (exception instanceof IABInvalidConsumptionException)
        {
            return popSendEmailSupportConsumeFailed(activityContext, exception);
        }
        return super.popError(activityContext, exception);
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popVerificationFailed(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.google_play_billing_verification_failed_window_title)
                        .setMessage(R.string.google_play_billing_verification_failed_window_description))
                .setNegativeButton(R.string.google_play_billing_verification_failed_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popBadResponse(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.google_play_billing_bad_response_window_title)
                        .setMessage(R.string.google_play_billing_bad_response_window_description))
                .setNegativeButton(R.string.google_play_billing_bad_response_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popResultError(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.google_play_billing_result_error_window_title)
                        .setMessage(R.string.google_play_billing_result_error_window_description))
                .setNegativeButton(R.string.google_play_billing_result_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popRemoteError(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.google_play_billing_remote_error_window_title)
                        .setMessage(R.string.google_play_billing_remote_error_window_description))
                .setNegativeButton(R.string.google_play_billing_remote_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popSendIntent(
            @NonNull final Context activityContext)
    {
        return Observable.create(AlertDialogOnSubscribe.builder(
                createDefaultDialogBuilder(activityContext)
                        .setTitle(R.string.google_play_billing_send_intent_error_window_title)
                        .setMessage(R.string.google_play_billing_send_intent_error_window_description))
                .setNegativeButton(R.string.google_play_billing_send_intent_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build());
    }

    @NonNull public Observable<Pair<DialogInterface, Integer>> popSendEmailSupportConsumeFailed(
            @NonNull final Context activityContext,
            @NonNull final Throwable exception)
    {
        return Observable.create(
                AlertDialogOnSubscribe.builder(
                        createDefaultDialogBuilder(activityContext)
                                .setTitle(R.string.google_play_send_support_email_consume_fail_title)
                                .setMessage(R.string.google_play_send_support_email_consume_fail_message))
                        .setPositiveButton(R.string.google_play_send_support_email_consume_fail_ok)
                        .setNegativeButton(R.string.google_play_send_support_email_consume_fail_cancel)
                        .build())
                .filter(pair -> pair.second.equals(AlertDialogButtonConstants.POSITIVE_BUTTON_INDEX))
                .doOnNext(pair -> {
                    sendSupportEmailConsumeFailed(activityContext, exception);
                });
    }

    public void sendSupportEmailConsumeFailed(@NonNull final Context context, @NonNull Throwable exception)
    {
        activityUtil.sendSupportEmail(
                context,
                googlePlayUtils.getSupportPurchaseConsumeEmailIntent(context, exception));
    }

    //<editor-fold desc="SKU related">
    @Override @NonNull protected THIABSKUDetailAdapter createProductDetailAdapter(
            @NonNull Activity activity,
            @NonNull ProductIdentifierDomain skuDomain)
    {
        return new THIABSKUDetailAdapter(activity, skuDomain);
    }

    @Override @NonNull public HashMap<ProductIdentifier, Boolean> getEnabledItems()
    {
        HashMap<ProductIdentifier, Boolean> enabledItems = new HashMap<>();

        for (THIABPurchase key : thiabPurchaseCache.getValues())
        {
            Timber.d("Disabling %s", key);
            enabledItems.put(key.getProductIdentifier(), false);
        }

        return enabledItems;
    }
    //</editor-fold>
}
