package com.androidth.general.common.persistence;

import android.content.Context;
import android.util.Log;

import com.androidth.general.api.live1b.AccountBalanceResponseDTO;
import com.androidth.general.api.live1b.PositionsResponseDTO;
import com.androidth.general.api.users.UserLiveAccount;
import com.androidth.general.models.realm.DataService;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.RealmClass;
import io.realm.exceptions.RealmException;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by jeffgan on 7/11/16.
 */

public class RealmManager{

    public static void initialise(Context context){
        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
    }

    public static RealmAsyncTask copyToRealm(RealmModel object){
        Realm realm = Realm.getDefaultInstance();
        return realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(object);
                Log.v("Realm", "Copying "+object);
            }
        }, () -> Log.v("Realm", "Copying success"),
                error -> Log.v("Realm", "Copying error "+error.getLocalizedMessage()));

//        realm.beginTransaction();
//        realm.copyToRealm(object);
//        Log.v("Realm", "Copying "+object);
//        realm.commitTransaction();
    }

    public static RealmAsyncTask replaceOldValueWith(RealmModel object){
        Realm realm = Realm.getDefaultInstance();
        Log.v ("Realm", "Replacing now");
        return realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(object.getClass());
                realm.copyToRealm(object);
                Log.v("Realm", "Replacing "+object);
            }
        }, () -> Log.v ("Realm", "Replacing success"),
            error -> Log.v("Realm", "Replacing error "+error.getLocalizedMessage()));

//        realm.beginTransaction();
//        realm.delete(object.getClass());
//        realm.copyToRealm(object);
//        realm.commitTransaction();
    }

    public static void ReplaceSyncOldValueWith(RealmModel object)
    {
        Realm realm = Realm.getDefaultInstance();
        Log.v ("Realm", "Replacing now");
        realm.executeTransaction(new Realm.Transaction() {
                                                 @Override
                                                 public void execute(Realm realm) {
                                                     realm.delete(object.getClass());
                                                     realm.copyToRealm(object);
                                                     Log.v("Realm", "Replacing "+object);
                                                 }
                                             });
    }

    public static RealmModel getOne(Class objectClass){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RealmModel> query = realm.where(objectClass);
        RealmResults<RealmModel> result = query.findAll();
        Log.v("Realm", "Query result: "+result);
        if(result!=null && result.size()>0){
            return result.first();
        }else{
            return null;
        }
    }

    public static RealmQuery getQuery(Class objectClass){
        Realm realm = Realm.getDefaultInstance();
        return realm.where(objectClass);
    }

    public static abstract class OnSubscribeRealm<T extends RealmObject> implements Observable.OnSubscribe<T> {
        Realm realm = Realm.getDefaultInstance();

        @Override
        public void call(Subscriber<? super T> subscriber) {
            subscriber.add(Subscriptions.create(new Action0() {
                @Override
                public void call() {
                    try {
                        Log.v("Realm", "Closing from call");
                        realm.close();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }));

            T object;
            realm.beginTransaction();
            try {
                object = get(realm);
                realm.commitTransaction();
            } catch (RuntimeException e) {
                realm.cancelTransaction();
                subscriber.onError(new RealmException("Error during transaction.", e));
                return;
            } catch (Error e) {
                realm.cancelTransaction();
                subscriber.onError(e);
                return;
            }
            if (object != null) {
                subscriber.onNext(object);
            }
            subscriber.onCompleted();
        }

        public abstract T get(Realm realm);
    }

}
