package com.ayondo.academy.fragments.settings;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.models.push.urbanairship.ActionRunnerOperator;
import com.ayondo.academy.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.ayondo.academy.rx.dialog.AlertDialogRx;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.urbanairship.UAirship;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionResult;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import timber.log.Timber;

public class FakePushNotificationUtil
{
    public static void showDialogAndSend(@NonNull final Context activityContext)
    {
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        final ManualPushActionView innerView = (ManualPushActionView) inflater.inflate(R.layout.debug_ask_for_notification_action, null);
        createManualActionDialog(activityContext, innerView)
                .flatMap(new Func1<OnDialogClickEvent, Observable<Pair<String, ActionArguments>>>()
                {
                    @Override public Observable<Pair<String, ActionArguments>> call(OnDialogClickEvent event)
                    {
                        if (event.isPositive())
                        {
                            return innerView.getActionArgumentObservable();
                        }
                        else if (event.isNeutral())
                        {
                            UAirship uAirship = UrbanAirshipPushNotificationManager.getUAirship();
                            if (uAirship == null)
                            {
                                THToast.show("UAirship instance is null");
                            }
                            else
                            {
                                Intent email = new Intent(Intent.ACTION_SEND);
                                email.setType("text/text");
                                email.putExtra(Intent.EXTRA_EMAIL, "support@tradehero.mobi");
                                email.putExtra(Intent.EXTRA_SUBJECT, "My UrbanAirship Channel Id");
                                email.putExtra(Intent.EXTRA_TEXT, "Package name " + activityContext.getPackageName() + "\n"
                                        + uAirship.getPushManager().getChannelId());
                                if (email.resolveActivity(activityContext.getPackageManager()) != null)
                                {
                                    activityContext.startActivity(email);
                                }
                            }
                        }
                        return Observable.empty();
                    }
                })
                .flatMap(new Func1<Pair<String, ActionArguments>, Observable<? extends ActionResult>>()
                {
                    @Override public Observable<? extends ActionResult> call(Pair<String, ActionArguments> pair)
                    {
                        activityContext.sendBroadcast(new Intent("com.urbanairship.push.RECEIVED")
                                .addCategory("com.ayondo.academy.dev"));
                        activityContext.sendBroadcast(new Intent("com.urbanairship.push.RECEIVED")
                                .addCategory("com.ayondo.academy"));
                        return Observable.create(new ActionRunnerOperator(
                                pair.first,
                                pair.second));
                    }
                })
                .flatMap(new Func1<ActionResult, Observable<? extends OnDialogClickEvent>>()
                {
                    @Override public Observable<? extends OnDialogClickEvent> call(ActionResult result)
                    {
                        return createActionResultDialog(activityContext, result);
                    }
                })
                .subscribe(
                        Actions.empty(),
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "");
                            }
                        }
                );
    }

    @NonNull public static Observable<OnDialogClickEvent> createManualActionDialog(
            @NonNull Context activityContext,
            @NonNull View innerView)
    {
        return AlertDialogRx.build(activityContext)
                .setView(innerView)
                .setPositiveButton(R.string.private_message_btn_send)
                .setNegativeButton(R.string.cancel)
                .setNeutralButton("Email")
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .build();
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @NonNull public static Observable<OnDialogClickEvent> createActionResultDialog(
            @NonNull Context activityContext,
            @NonNull ActionResult actionResult)
    {
        String message;
        if (actionResult.getException() != null)
        {
            Timber.e(actionResult.getException(), "ActionResult: Status.%s, Value: %s", actionResult.getStatus(), actionResult.getValue());
            message = actionResult.getException().getMessage();
        }
        else
        {
            Timber.e("ActionResult: Status.%s, Value: %s", actionResult.getStatus(), actionResult.getValue());
            message = "Value: " + actionResult.getValue();
        }
        return AlertDialogRx.build(activityContext)
                .setTitle(actionResult.getStatus())
                .setMessage(message)
                .setPositiveButton(R.string.ok)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .build();
    }
}
