package com.tradehero.common.billing.googleplay;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.tradehero.common.rx.ServiceConnectionOperator;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

@Singleton
public class BillingServiceBinderObservable
{
    public final static String INTENT_VENDING_PACKAGE = "com.android.vending";
    public final static String INTENT_VENDING_SERVICE_BIND = "com.android.vending.billing.InAppBillingService.BIND";

    @NonNull private final Context context;
    private ServiceConnectionOperator serviceConnectionOperator;
    private BehaviorSubject<IBinder> serviceSubject;
    private Subscription subjectSubscription;

    //<editor-fold desc="Constructors">
    @Inject public BillingServiceBinderObservable(@NonNull Context context)
    {
        this.context = context;
        this.serviceSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    @NonNull public Observable<IBinder> getBinder()
    {
        if (subjectSubscription == null)
        {
            bindSubject();
        }
        return serviceSubject.share();
    }

    private void bindSubject()
    {
        serviceConnectionOperator = new ServiceConnectionOperator(
                context,
                getBillingBindIntent(),
                0,
                Context.BIND_AUTO_CREATE);
        subjectSubscription = Observable.create(serviceConnectionOperator)
                .finallyDo(() -> {
                    subjectSubscription = null;
                    serviceSubject = BehaviorSubject.create();
                    serviceConnectionOperator = null;
                    THToast.show("Billing Service binder disconnected");
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(serviceSubject);
    }

    public void onDestroy()
    {
        serviceConnectionOperator.onDestroy();
    }

    @NonNull public static Intent getBillingBindIntent()
    {
        Intent serviceIntent = new Intent(INTENT_VENDING_SERVICE_BIND);
        serviceIntent.setPackage(INTENT_VENDING_PACKAGE);
        return serviceIntent;
    }
}
