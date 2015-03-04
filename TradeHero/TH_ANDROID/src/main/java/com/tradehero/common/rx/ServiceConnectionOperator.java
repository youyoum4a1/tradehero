package com.tradehero.common.rx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import java.net.UnknownServiceException;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class ServiceConnectionOperator implements Observable.OnSubscribe<IBinder>
{
    @NonNull protected final Context context;
    @NonNull protected final Intent serviceIntent;
    protected final int flags;
    protected final int bindType;

    //<editor-fold desc="Constructors">
    public ServiceConnectionOperator(
            @NonNull Context context,
            @NonNull Intent serviceIntent,
            int flags,
            int bindType)
    {
        this.context = context;
        this.serviceIntent = serviceIntent;
        this.flags = flags;
        this.bindType = bindType;
    }
    //</editor-fold>

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
                THToast.show("ServiceConnectionOperator disconnected");
                subscriber.onCompleted();
            }
        };

        Observable.create(
                new Observable.OnSubscribe<Boolean>()
                {
                    @Override public void call(Subscriber<? super Boolean> subscriber)
                    {
                        List<ResolveInfo> resolveList = context.getPackageManager().queryIntentServices(serviceIntent, flags);
                        subscriber.onNext(resolveList != null && !resolveList.isEmpty());
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(new Observer<Boolean>()
                {
                    @Override public void onNext(Boolean isServiceAvailable)
                    {
                        if (isServiceAvailable)
                        {
                            try
                            {
                                context.bindService(
                                        serviceIntent,
                                        serviceConnection,
                                        bindType);
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

                    @Override public void onCompleted()
                    {
                        // Nothing to do
                    }

                    @Override public void onError(Throwable e)
                    {
                        subscriber.onError(e);
                    }
                });
    }
}
