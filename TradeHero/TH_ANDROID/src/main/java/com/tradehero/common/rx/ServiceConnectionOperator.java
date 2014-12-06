package com.tradehero.common.rx;

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

public class ServiceConnectionOperator implements Observable.OnSubscribe<IBinder>
{
    @NonNull protected final Context context;
    @NonNull protected final Intent serviceIntent;
    protected final int bindType;

    //<editor-fold desc="Constructors">
    public ServiceConnectionOperator(
            @NonNull Context context,
            @NonNull Intent serviceIntent,
            int bindType)
    {
        this.context = context;
        this.serviceIntent = serviceIntent;
        this.bindType = bindType;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super IBinder> subscriber)
    {
        context.startService(serviceIntent);
        if (isServiceAvailable())
        {
            context.bindService(
                    serviceIntent,
                    new ServiceConnection()
                    {
                        @Override public void onServiceConnected(ComponentName name, IBinder service)
                        {
                            subscriber.onNext(service);
                        }

                        @Override public void onServiceDisconnected(ComponentName name)
                        {
                            subscriber.onCompleted();
                        }
                    },
                    bindType);
        }
        else
        {
            subscriber.onError(new UnknownServiceException("Service not available: " + serviceIntent));
        }
    }

    protected boolean isServiceAvailable()
    {
        List<ResolveInfo> intentService = context.getPackageManager().queryIntentServices(serviceIntent, 0);
        return intentService != null && !intentService.isEmpty();
    }
}
