package com.tradehero.common.billing.samsung.rx;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.listener.OnGetInboxListener;
import com.sec.android.iap.lib.listener.OnGetItemListener;
import com.sec.android.iap.lib.listener.OnIapBindListener;
import com.sec.android.iap.lib.listener.OnPaymentListener;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.ItemVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
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
    @NonNull private static final Queue<Pair<ItemListQueryPackage, PublishSubject<List<ItemVo>>>> itemQueries = new LinkedBlockingQueue<>();
    @NonNull private static final Queue<Pair<InboxListQueryPackage, PublishSubject<InboxVo>>> inboxQueries = new LinkedBlockingQueue<>();
    @NonNull private static final Queue<Pair<PurchaseQueryPackage, PublishSubject<PurchaseVo>>> purchaseQueries = new LinkedBlockingQueue<>();

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
        itemQueries.add(Pair.create(itemListQueryPackage, subject));
        getNextItemPackageIfIdle();
        return subject.share().cache(1);
    }

    private static void getNextItemPackageIfIdle()
    {
        Pair<ItemListQueryPackage, PublishSubject<List<ItemVo>>> pair = itemQueries.poll();

        if (pair != null)
        {
            ItemListQueryPackage queryPackage = pair.first;
            Context context = queryPackage.weakContext.get();
            if (context == null)
            {
                getNextItemPackageIfIdle();
            }
            else
            {
                OnGetItemListener currentListener = SamsungIapHelper.getInstance(context, queryPackage.mode)
                        .getOnGetItemListener();
                if (currentListener == null)
                {
                    getItemsPrivate(pair.first)
                            .subscribe(pair.second);
                }
                else
                {
                    itemQueries.add(pair);
                }
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
                                    itemListQueryPackage.itemListQueryGroup.groupId,
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
                                                        itemListQueryPackage.itemListQueryGroup.groupId,
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
                            getNextItemPackageIfIdle();
                        }
                    });
                    subscriber.add(cleanup);
                }
            }
        });
    }

    @NonNull public static Observable<Pair<InboxListQueryGroup, Observable<InboxVo>>> getInboxes(
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
                .map(new Func1<InboxListQueryPackage, Pair<InboxListQueryGroup, Observable<InboxVo>>>()
                {
                    @Override public Pair<InboxListQueryGroup, Observable<InboxVo>> call(InboxListQueryPackage inboxListQueryPackage)
                    {
                        return Pair.create(
                                inboxListQueryPackage.inboxListQueryGroup,
                                getInbox(inboxListQueryPackage));
                    }
                });
    }

    @NonNull public static Observable<InboxVo> getInbox(
            @NonNull final InboxListQueryPackage inboxListQueryPackage)
    {
        PublishSubject<InboxVo> subject = PublishSubject.create();
        inboxQueries.add(Pair.create(inboxListQueryPackage, subject));
        getNextInboxPackageIfIdle();
        return subject.share().cache();
    }

    private static void getNextInboxPackageIfIdle()
    {
        Pair<InboxListQueryPackage, PublishSubject<InboxVo>> pair = inboxQueries.poll();

        if (pair != null)
        {
            InboxListQueryPackage queryPackage = pair.first;
            Context context = queryPackage.weakContext.get();
            if (context == null)
            {
                getNextInboxPackageIfIdle();
            }
            else
            {
                OnGetInboxListener currentListener = SamsungIapHelper.getInstance(context, queryPackage.mode)
                        .getOnGetInboxListener();
                if (currentListener == null)
                {
                    getInboxPrivate(pair.first)
                            .subscribe(pair.second);
                }
                else
                {
                    inboxQueries.add(pair);
                }
            }
        }
    }

    @NonNull private static Observable<InboxVo> getInboxPrivate(
            @NonNull final InboxListQueryPackage inboxListQueryPackage)
    {
        return Observable.create(new Observable.OnSubscribe<InboxVo>()
        {
            @Override public void call(final Subscriber<? super InboxVo> subscriber)
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
                                    inboxListQueryPackage.inboxListQueryGroup.groupId,
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
                                                for (InboxVo inboxVo : inboxList)
                                                {
                                                    subscriber.onNext(inboxVo);
                                                }
                                                subscriber.onCompleted();
                                            }
                                            else
                                            {
                                                subscriber.onError(new SamsungPurchaseFetchException(
                                                        errorVo,
                                                        inboxListQueryPackage.inboxListQueryGroup.groupId));
                                            }
                                        }
                                    });
                    Subscription cleanup = Subscriptions.create(new Action0()
                    {
                        @Override public void call()
                        {
                            SamsungIapHelper.getInstance(context, inboxListQueryPackage.mode)
                                    .setOnGetInboxListener(null);
                            getNextInboxPackageIfIdle();
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
        purchaseQueries.add(Pair.create(purchaseQueryPackage, subject));
        getNextPurchaseIfIdle();
        return subject.share().cache(1);
    }

    private static void getNextPurchaseIfIdle()
    {
        Pair<PurchaseQueryPackage, PublishSubject<PurchaseVo>> pair = purchaseQueries.poll();

        if (pair != null)
        {
            PurchaseQueryPackage queryPackage = pair.first;
            Context context = queryPackage.weakContext.get();
            if (context == null)
            {
                getNextPurchaseIfIdle();
            }
            else
            {
                OnPaymentListener currentListener = SamsungIapHelper.getInstance(context, queryPackage.mode)
                        .getOnPaymentListener();
                if (currentListener == null)
                {
                    getPurchasePrivate(pair.first)
                            .subscribe(pair.second);
                }
                else
                {
                    purchaseQueries.add(pair);
                }
            }
        }
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
                                    purchaseQueryPackage.groupId,
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
                                                        purchaseQueryPackage.groupId,
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
                            getNextPurchaseIfIdle();
                        }
                    });
                    subscriber.add(cleanup);
                }
            }
        });
    }
}
