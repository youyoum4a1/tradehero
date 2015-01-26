package com.tradehero.common.rx;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import java.net.UnknownServiceException;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class ServiceConnectionOperator implements Observable.OnSubscribe<IBinder>
{
    @NonNull protected final Context context;
    @NonNull protected final Intent serviceIntent;
    protected final int flags;
    protected final int bindType;
    private BehaviorSubject<IBinder> subject;
    private ServiceConnection serviceConnection;

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
        subject = BehaviorSubject.create();
        serviceConnection = new ServiceConnection()
        {
            @Override public void onServiceConnected(ComponentName name, IBinder service)
            {
                subject.onNext(service);
            }

            @Override public void onServiceDisconnected(ComponentName name)
            {
                THToast.show("ServiceConnectionOperator disconnected");
                subject.onCompleted();
            }
        };
        bind();
    }
    //</editor-fold>

    private void bind()
    {
        Observable.just(flags)
                .subscribeOn(Schedulers.computation())
                .map(flags -> context.getPackageManager().queryIntentServices(serviceIntent, flags))
                .map(resolveList -> resolveList != null && !resolveList.isEmpty())
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
                                subject.onError(e);
                            }
                        }
                        else
                        {
                            subject.onError(new UnknownServiceException("Service not available: " + serviceIntent));
                        }
                    }

                    @Override public void onCompleted()
                    {
                        // Nothing to do
                    }

                    @Override public void onError(Throwable e)
                    {
                        subject.onError(e);
                    }
                });
    }

    @Override public void call(Subscriber<? super IBinder> subscriber)
    {
        subject.subscribe(subscriber);
    }

    public void onDestroy()
    {
        context.unbindService(serviceConnection);
        serviceConnection = null;
        subject.onCompleted();
    }
}
