package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;
import com.samsung.android.sdk.iap.lib.listener.OnGetInboxListener;
import com.samsung.android.sdk.iap.lib.listener.OnGetItemListener;
import com.samsung.android.sdk.iap.lib.listener.OnIapBindListener;
import com.samsung.android.sdk.iap.lib.listener.OnPaymentListener;
import com.samsung.android.sdk.iap.lib.vo.ErrorVo;
import com.samsung.android.sdk.iap.lib.vo.InboxVo;
import com.samsung.android.sdk.iap.lib.vo.ItemVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.exception.SamsungBindException;
import com.tradehero.common.billing.samsung.exception.SamsungItemListException;
import com.tradehero.common.billing.samsung.exception.SamsungPurchaseException;
import com.tradehero.common.billing.samsung.exception.SamsungPurchaseFetchException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

public class SamsungIapHelperFacade
{
    @NonNull private static final Queue<Pair<SamsungQueryPackage, PublishSubject>> queries = new LinkedBlockingQueue<>();

    @NonNull public static Observable<Integer> bind(
            @NonNull final Context context,
            @SamsungBillingMode final int mode)
    {
        return Observable.create(new Observable.OnSubscribe<Integer>()
        {
            @Override public void call(final Subscriber<? super Integer> subscriber)
            {
                SamsungIapHelper.getInstance(context, mode).bindIapService(
                        new OnIapBindListener()
                        {
                            @Override public void onBindIapFinished(int result)
                            {
                                if (result == SamsungIapHelper.IAP_RESPONSE_RESULT_OK)
                                {
                                    subscriber.onNext(result);
                                    subscriber.onCompleted();
                                }
                                else
                                {
                                    subscriber.onError(new SamsungBindException(result));
                                }
                            }
                        });
                // There is no way to unregister...
            }
        });
    }

    @NonNull public static Observable<Pair<ItemListQueryGroup, List<ItemVo>>> getItems(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull List<ItemListQueryGroup> queryGroups)
    {
        List<ItemListQueryPackage> itemListQueryPackages = new ArrayList<>();
        for (ItemListQueryGroup queryGroup : queryGroups)
        {
            itemListQueryPackages.add(new ItemListQueryPackage(context, mode, queryGroup));
        }
        return Observable.from(itemListQueryPackages)
                .flatMap(new Func1<ItemListQueryPackage, Observable<Pair<ItemListQueryGroup, List<ItemVo>>>>()
                {
                    @Override public Observable<Pair<ItemListQueryGroup, List<ItemVo>>> call(final ItemListQueryPackage itemListQueryPackage)
                    {
                        return getItems(itemListQueryPackage)
                                .map(new Func1<List<ItemVo>, Pair<ItemListQueryGroup, List<ItemVo>>>()
                                {
                                    @Override public Pair<ItemListQueryGroup, List<ItemVo>> call(List<ItemVo> itemVos)
                                    {
                                        return Pair.create(
                                                itemListQueryPackage.itemListQueryGroup,
                                                itemVos);
                                    }
                                });
                    }
                });
    }

    @NonNull public static Observable<List<ItemVo>> getItems(
            @NonNull final ItemListQueryPackage itemListQueryPackage)
    {
        PublishSubject<List<ItemVo>> subject = PublishSubject.create();
        queries.add(Pair.create((SamsungQueryPackage) itemListQueryPackage, (PublishSubject) subject));
        getNextQueryPackageIfIdle();
        return subject.share().cache(1);
    }

    private static void getNextQueryPackageIfIdle()
    {
        Pair<SamsungQueryPackage, PublishSubject> pair = queries.poll();

        if (pair != null)
        {
            SamsungQueryPackage queryPackage = pair.first;
            Context context = queryPackage.weakContext.get();
            if (context == null)
            {
                getNextQueryPackageIfIdle();
            }
            else if (SamsungIapHelper.getInstance(context, queryPackage.mode)
                    .getOnGetItemListener() != null
                    || SamsungIapHelper.getInstance(context, queryPackage.mode)
                    .getOnGetInboxListener() != null
                    || SamsungIapHelper.getInstance(context, queryPackage.mode)
                    .getOnPaymentListener() != null)
            {
                queries.add(pair);
            }
            else if (queryPackage instanceof ItemListQueryPackage)
            {
                //noinspection unchecked
                getItemsPrivate((ItemListQueryPackage) queryPackage)
                        .subscribe((PublishSubject<List<ItemVo>>) pair.second);
            }
            else if (queryPackage instanceof InboxListQueryPackage)
            {
                //noinspection unchecked
                getInboxPrivate((InboxListQueryPackage) pair.first)
                        .subscribe((PublishSubject<List<InboxVo>>) pair.second);
            }
            else if (queryPackage instanceof PurchaseQueryPackage)
            {
                //noinspection unchecked
                getPurchasePrivate((PurchaseQueryPackage) pair.first)
                        .subscribe((PublishSubject<PurchaseVo>) pair.second);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled " + queryPackage);
            }
        }
    }

    @NonNull private static Observable<List<ItemVo>> getItemsPrivate(
            @NonNull final ItemListQueryPackage itemListQueryPackage)
    {
        return Observable.create(new Observable.OnSubscribe<List<ItemVo>>()
        {
            @Override public void call(final Subscriber<? super List<ItemVo>> subscriber)
            {
                final Context context = itemListQueryPackage.weakContext.get();
                if (context == null)
                {
                    subscriber.onError(new NullPointerException("Context has been terminated"));
                }
                else
                {
                    SamsungIapHelper.getInstance(context, itemListQueryPackage.mode)
                            .getItemList(
                                    itemListQueryPackage.itemListQueryGroup.startNum,
                                    itemListQueryPackage.itemListQueryGroup.endNum,
                                    itemListQueryPackage.itemListQueryGroup.itemType,
                                    itemListQueryPackage.mode,
                                    new OnGetItemListener()
                                    {
                                        @Override public void onGetItem(ErrorVo errorVo, ArrayList<ItemVo> itemList)
                                        {
                                            if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                                            {
                                                subscriber.onNext(itemList);
                                                subscriber.onCompleted();
                                            }
                                            else
                                            {
                                                subscriber.onError(new SamsungItemListException(
                                                        errorVo,
                                                        itemListQueryPackage.mode));
                                            }
                                        }
                                    });
                    Subscription cleanup = Subscriptions.create(new Action0()
                    {
                        @Override public void call()
                        {
                            SamsungIapHelper.getInstance(context, itemListQueryPackage.mode)
                                    .setOnGetItemListener(null);
                            getNextQueryPackageIfIdle();
                        }
                    });
                    subscriber.add(cleanup);
                }
            }
        });
    }

    @NonNull public static Observable<Pair<InboxListQueryGroup, List<InboxVo>>> getInboxes(
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull final List<InboxListQueryGroup> inboxListQueryGroups)
    {
        List<InboxListQueryPackage> inboxListQueryPackages = new ArrayList<>();
        for (InboxListQueryGroup queryGroup : inboxListQueryGroups)
        {
            inboxListQueryPackages.add(new InboxListQueryPackage(context, mode, queryGroup));
        }
        return Observable.from(inboxListQueryPackages)
                .flatMap(new Func1<InboxListQueryPackage, Observable<Pair<InboxListQueryGroup, List<InboxVo>>>>()
                {
                    @Override public Observable<Pair<InboxListQueryGroup, List<InboxVo>>> call(final InboxListQueryPackage inboxListQueryPackage)
                    {
                        return getInbox(inboxListQueryPackage)
                        .map(new Func1<List<InboxVo>, Pair<InboxListQueryGroup, List<InboxVo>>>()
                        {
                            @Override public Pair<InboxListQueryGroup, List<InboxVo>> call(List<InboxVo> inboxVos)
                            {
                                return Pair.create(
                                        inboxListQueryPackage.inboxListQueryGroup,
                                        inboxVos);
                            }
                        });
                    }
                });
    }

    @NonNull public static Observable<List<InboxVo>> getInbox(
            @NonNull final InboxListQueryPackage inboxListQueryPackage)
    {
        PublishSubject<List<InboxVo>> subject = PublishSubject.create();
        queries.add(Pair.create((SamsungQueryPackage) inboxListQueryPackage, (PublishSubject) subject));
        getNextQueryPackageIfIdle();
        return subject.share().cache();
    }

    @NonNull private static Observable<List<InboxVo>> getInboxPrivate(
            @NonNull final InboxListQueryPackage inboxListQueryPackage)
    {
        return Observable.create(new Observable.OnSubscribe<List<InboxVo>>()
        {
            @Override public void call(final Subscriber<? super List<InboxVo>> subscriber)
            {
                final Context context = inboxListQueryPackage.weakContext.get();
                if (context == null)
                {
                    subscriber.onError(new NullPointerException("Context has been terminated"));
                }
                else
                {
                    SamsungIapHelper.getInstance(context, inboxListQueryPackage.mode)
                            .getItemInboxList(
                                    inboxListQueryPackage.inboxListQueryGroup.startNum,
                                    inboxListQueryPackage.inboxListQueryGroup.endNum,
                                    inboxListQueryPackage.inboxListQueryGroup.startDate,
                                    inboxListQueryPackage.inboxListQueryGroup.endDate,
                                    new OnGetInboxListener()
                                    {
                                        @Override public void onGetItemInbox(ErrorVo errorVo, ArrayList<InboxVo> inboxList)
                                        {
                                            if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                                            {
                                                subscriber.onNext(inboxList);
                                                subscriber.onCompleted();
                                            }
                                            else
                                            {
                                                subscriber.onError(new SamsungPurchaseFetchException(errorVo));
                                            }
                                        }
                                    });
                    Subscription cleanup = Subscriptions.create(new Action0()
                    {
                        @Override public void call()
                        {
                            SamsungIapHelper.getInstance(context, inboxListQueryPackage.mode)
                                    .setOnGetInboxListener(null);
                            getNextQueryPackageIfIdle();
                        }
                    });
                    subscriber.add(cleanup);
                }
            }
        });
    }

    @NonNull public static Observable<PurchaseVo> getPurchase(
            @NonNull final PurchaseQueryPackage purchaseQueryPackage)
    {
        PublishSubject<PurchaseVo> subject = PublishSubject.create();
        queries.add(Pair.create((SamsungQueryPackage) purchaseQueryPackage, (PublishSubject) subject));
        getNextQueryPackageIfIdle();
        return subject.share().cache(1);
    }

    @NonNull private static Observable<PurchaseVo> getPurchasePrivate(
            @NonNull final PurchaseQueryPackage purchaseQueryPackage)
    {
        return Observable.create(new Observable.OnSubscribe<PurchaseVo>()
        {
            @Override public void call(final Subscriber<? super PurchaseVo> subscriber)
            {
                final Context context = purchaseQueryPackage.weakContext.get();
                if (context == null)
                {
                    subscriber.onError(new NullPointerException("Context has been terminated"));
                }
                else
                {
                    SamsungIapHelper.getInstance(context, purchaseQueryPackage.mode)
                            .startPayment(
                                    purchaseQueryPackage.itemId,
                                    purchaseQueryPackage.showSuccessDialog,
                                    new OnPaymentListener()
                                    {
                                        @Override public void onPayment(ErrorVo errorVo, PurchaseVo purchaseVo)
                                        {
                                            if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                                            {
                                                subscriber.onNext(purchaseVo);
                                                subscriber.onCompleted();
                                            }
                                            else
                                            {
                                                subscriber.onError(new SamsungPurchaseException(
                                                        errorVo,
                                                        purchaseQueryPackage.itemId,
                                                        purchaseQueryPackage.showSuccessDialog));
                                            }
                                        }
                                    });
                    Subscription cleanup = Subscriptions.create(new Action0()
                    {
                        @Override public void call()
                        {
                            SamsungIapHelper.getInstance(context, purchaseQueryPackage.mode)
                                    .setOnPaymentListener(null);
                            getNextQueryPackageIfIdle();
                        }
                    });
                    subscriber.add(cleanup);
                }
            }
        });
    }
}
