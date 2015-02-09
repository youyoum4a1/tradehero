package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABDeveloperErrorException;
import com.tradehero.common.billing.googleplay.exception.IABInvalidConsumptionException;
import com.tradehero.common.billing.googleplay.exception.IABItemAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.googleplay.exception.IABResultErrorException;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THBillingAlertDialogRxUtil;
import com.tradehero.th.fragments.billing.THIABSKUDetailAdapter;
import com.tradehero.th.fragments.billing.THIABStoreProductDetailView;
import com.tradehero.th.persistence.billing.googleplay.THIABPurchaseCacheRx;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.ActivityUtil;
import com.tradehero.th.utils.VersionUtils;
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
        THIABSKUDetailAdapter,
        THIABOrderId,
        THIABPurchase>
{
    @NonNull protected THIABPurchaseCacheRx thiabPurchaseCache;
    @NonNull protected GooglePlayUtils googlePlayUtils;

    //<editor-fold desc="Constructors">
    @Inject public THIABAlertDialogRxUtil(
            @NonNull Analytics analytics,
            @NonNull VersionUtils versionUtils,
            @NonNull THIABPurchaseCacheRx thiabPurchaseCache,
            @NonNull GooglePlayUtils googlePlayUtils)
    {
        super(analytics, versionUtils);
        this.thiabPurchaseCache = thiabPurchaseCache;
        this.googlePlayUtils = googlePlayUtils;
    }
    //</editor-fold>

    @Override @NonNull public Observable<OnDialogClickEvent> popErrorAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        if (throwable instanceof IABBadResponseException)
        {
            return popBadResponseAndHandle(activityContext);
        }
        if (throwable instanceof IABResultErrorException)
        {
            return popResultErrorAndHandle(activityContext);
        }
        if (throwable instanceof IABRemoteException)
        {
            return popRemoteErrorAndHandle(activityContext);
        }
        if (throwable instanceof IABInvalidConsumptionException)
        {
            return popSendEmailSupportConsumeFailedAndHandle(activityContext, throwable);
        }
        if (throwable instanceof IABItemAlreadyOwnedException)
        {
            return popAlreadyOwnedAndHandle(activityContext, throwable);
        }
        if (throwable instanceof IABDeveloperErrorException)
        {
            return popDeveloperErrorAndHandle(activityContext, throwable);
        }
        return super.popErrorAndHandle(activityContext, throwable);
    }

    //<editor-fold desc="Verification Failed">
    @NonNull public static Observable<OnDialogClickEvent> popVerificationFailedAndHandle(
            @NonNull final Context activityContext)
    {
        return popVerificationFailed(activityContext)
                .flatMap(pair -> Observable.empty());
    }

    @NonNull public static Observable<OnDialogClickEvent> popVerificationFailed(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_verification_failed_window_title)
                .setMessage(R.string.google_play_billing_verification_failed_window_description)
                .setNegativeButton(R.string.google_play_billing_verification_failed_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Bad Response">
    @NonNull public static Observable<OnDialogClickEvent> popBadResponseAndHandle(
            @NonNull final Context activityContext)
    {
        return popBadResponse(activityContext)
                .flatMap(pair -> Observable.empty());
    }

    @NonNull public static Observable<OnDialogClickEvent> popBadResponse(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_bad_response_window_title)
                .setMessage(R.string.google_play_billing_bad_response_window_description)
                .setNegativeButton(R.string.google_play_billing_bad_response_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Result Error">
    @NonNull public static Observable<OnDialogClickEvent> popResultErrorAndHandle(
            @NonNull final Context activityContext)
    {
        return popResultError(activityContext)
                .flatMap(pair -> Observable.empty());
    }

    @NonNull public static Observable<OnDialogClickEvent> popResultError(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_result_error_window_title)
                .setMessage(R.string.google_play_billing_result_error_window_description)
                .setNegativeButton(R.string.google_play_billing_result_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Remote Error">
    @NonNull public static Observable<OnDialogClickEvent> popRemoteErrorAndHandle(
            @NonNull final Context activityContext)
    {
        return popRemoteError(activityContext)
                .flatMap(pair -> Observable.empty());
    }

    @NonNull public static Observable<OnDialogClickEvent> popRemoteError(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_remote_error_window_title)
                .setMessage(R.string.google_play_billing_remote_error_window_description)
                .setNegativeButton(R.string.google_play_billing_remote_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Send Intent">
    @NonNull public static Observable<OnDialogClickEvent> popSendIntentAndHandle(
            @NonNull final Context activityContext)
    {
        return popSendIntent(activityContext)
                .flatMap(pair -> Observable.empty());
    }

    @NonNull public static Observable<OnDialogClickEvent> popSendIntent(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_send_intent_error_window_title)
                .setMessage(R.string.google_play_billing_send_intent_error_window_description)
                .setNegativeButton(R.string.google_play_billing_send_intent_error_cancel)
                .setCanceledOnTouchOutside(true)
                .build();
    }
    //</editor-fold>

    //<editor-fold desc="Consume Failed">
    @NonNull public Observable<OnDialogClickEvent> popSendEmailSupportConsumeFailedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popSendEmailSupportConsumeFailed(activityContext)
                .flatMap(pair -> handleSendEmailSupportConsumeFailed(activityContext, pair, throwable));
    }

    @NonNull public static Observable<OnDialogClickEvent> popSendEmailSupportConsumeFailed(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_send_support_email_consume_fail_title)
                .setMessage(R.string.google_play_send_support_email_consume_fail_message)
                .setPositiveButton(R.string.google_play_send_support_email_consume_fail_ok)
                .setNegativeButton(R.string.google_play_send_support_email_consume_fail_cancel)
                .build();
    }

    @NonNull public Observable<OnDialogClickEvent> handleSendEmailSupportConsumeFailed(
            @NonNull final Context activityContext,
            @NonNull OnDialogClickEvent event,
            @NonNull Throwable throwable)
    {
        if (event.isPositive())
        {
            sendSupportEmailConsumeFailed(activityContext, throwable);
        }
        return Observable.empty();
    }

    public void sendSupportEmailConsumeFailed(@NonNull final Context context, @NonNull Throwable exception)
    {
        ActivityUtil.sendSupportEmail(
                context,
                googlePlayUtils.getSupportPurchaseConsumeEmailIntent(context, exception));
    }
    //</editor-fold>

    //<editor-fold desc="Already Owned">
    @NonNull public Observable<OnDialogClickEvent> popAlreadyOwnedAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popAlreadyOwned(activityContext)
                .flatMap(pair -> handleSendEmailAlreadyOwned(activityContext, pair, throwable));
    }

    @NonNull public static Observable<OnDialogClickEvent> popAlreadyOwned(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_already_owned_window_title)
                .setMessage(R.string.google_play_billing_already_owned_window_description)
                .setPositiveButton(R.string.google_play_billing_already_owned_ok)
                .setNegativeButton(R.string.google_play_billing_already_owned_cancel)
                .build();
    }

    @NonNull public Observable<OnDialogClickEvent> handleSendEmailAlreadyOwned(
            @NonNull final Context activityContext,
            @NonNull OnDialogClickEvent event,
            @NonNull Throwable throwable)
    {
        if (event.isPositive())
        {
            sendSupportEmailAlreadyOwned(activityContext, throwable);
        }
        return Observable.empty();
    }

    public void sendSupportEmailAlreadyOwned(@NonNull final Context context, @NonNull Throwable exception)
    {
        ActivityUtil.sendSupportEmail(
                context,
                googlePlayUtils.getSupportAlreadyOwnedIntent(context, exception));
    }
    //</editor-fold>

    //<editor-fold desc="Developer Error">
    @NonNull public Observable<OnDialogClickEvent> popDeveloperErrorAndHandle(
            @NonNull final Context activityContext,
            @NonNull final Throwable throwable)
    {
        return popDeveloperError(activityContext)
                .flatMap(pair -> handleSendEmailDeveloperError(activityContext, pair, throwable));
    }

    @NonNull public static Observable<OnDialogClickEvent> popDeveloperError(
            @NonNull final Context activityContext)
    {
        return buildDefault(activityContext)
                .setTitle(R.string.google_play_billing_developer_error_window_title)
                .setMessage(R.string.google_play_billing_developer_error_window_description)
                .setPositiveButton(R.string.google_play_billing_developer_error_ok)
                .setNegativeButton(R.string.google_play_billing_developer_error_cancel)
                .build();
    }

    @NonNull public Observable<OnDialogClickEvent> handleSendEmailDeveloperError(
            @NonNull final Context activityContext,
            @NonNull OnDialogClickEvent event,
            @NonNull Throwable throwable)
    {
        if (event.isPositive())
        {
            sendSupportEmailDeveloperError(activityContext, throwable);
        }
        return Observable.empty();
    }

    public void sendSupportEmailDeveloperError(@NonNull final Context context, @NonNull Throwable exception)
    {
        ActivityUtil.sendSupportEmail(
                context,
                googlePlayUtils.getSupportDeveloperErrorIntent(context, exception));
    }
    //</editor-fold>

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

    public void sendSupportEmailRestoreFailed(final Context context, Exception exception)
    {
        context.startActivity(Intent.createChooser(
                googlePlayUtils.getSupportPurchaseRestoreEmailIntent(context, exception),
                context.getString(R.string.iap_send_support_email_chooser_title)));
    }
}