package com.androidth.general.rx.observable;

import android.content.Context;

import com.androidth.general.common.persistence.RealmManager;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Func1;

public final class RealmObservable
{
    private RealmObservable() {
    }

    public static <T extends RealmObject> Observable<T> object(Context context, final Func1<Realm, T> function) {
        return Observable.create(new RealmManager.OnSubscribeRealm<T>(){
            @Override
            public T get(Realm realm) {
                return function.call(realm);
            }
        });
    }

//    public static <T extends RealmObject> Observable<T> object(Context context, String fileName, final Func1<Realm, T> function) {
//        return Observable.create(new OnSubscribeRealm<T>(context, fileName) {
//            @Override
//            public T get(Realm realm) {
//                return function.call(realm);
//            }
//        });
//    }
}
