package com.androidth.general.common.billing.googleplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import java.net.UnknownServiceException;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public class BillingServiceBinderObservable
{
    public final static String INTENT_VENDING_PACKAGE = "com.android.vending";
    public final static String INTENT_VENDING_SERVICE_BIND = "com.android.vending.billing.InAppBillingService.BIND";

    @NonNull public static Intent getBillingBindIntent()
    {
        Intent serviceIntent = new Intent(INTENT_VENDING_SERVICE_BIND);
        serviceIntent.setPackage(INTENT_VENDING_PACKAGE);
        return serviceIntent;
    }

    @NonNull public static Observable<IBinder> getServiceBinder(
            @NonNull final Context context,
            @NonNull final Intent serviceIntent,
            final int flags,
            final int bindType)
    {
        return Observable.create(new Observable.OnSubscribe<IBinder>()
        {
            @Override public void call(final Subscriber<? super IBinder> subscriber)
            {
                final ServiceConnection serviceConnection = new ServiceConnection()
                {
                    @Override public void onServiceConnected(ComponentName name, IBinder service)
                    {
                        subscriber.onNext(service);
                    }

                    @Override public void onServiceDisconnected(ComponentName name)
                    {
                        subscriber.onCompleted();
                    }
                };

                List<ResolveInfo> resolveList = context.getPackageManager().queryIntentServices(serviceIntent, flags);
                boolean serviceAvailable = resolveList != null && !resolveList.isEmpty();
                if (serviceAvailable)
                {
                    try
                    {
                        if (!context.bindService(
                                serviceIntent,
                                serviceConnection,
                                bindType))
                        {
                            subscriber.onError(new RuntimeException("Failed to bind to service"));
                        }
                        else
                        {
                            Subscription unbind = Subscriptions.create(new Action0()
                            {
                                @Override public void call()
                                {
                                    context.unbindService(serviceConnection);
                                }
                            });
                            subscriber.add(unbind);
                        }
                    } catch (Exception e)
                    {
                        subscriber.onError(e);
                    }
                }
                else
                {
                    subscriber.onError(new UnknownServiceException("Service not available: " + serviceIntent));
                }
            }
        });
    }
}
