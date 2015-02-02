package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;

abstract public class AbstractDiscussionCompactItemViewLinear<T>
        extends LinearLayout
        implements DTOView<T>
{
    @Inject protected DiscussionCacheRx discussionCache;
    @Inject protected SocialShareTranslationHelper socialShareHelper;
    @Inject DashboardNavigator dashboardNavigator;

    protected AbstractDiscussionCompactItemViewHolder viewHolder;
    protected T discussionKey;
    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;

    @NonNull protected SubscriptionList subscriptions;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        subscriptions = new SubscriptionList();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            viewHolder = createViewHolder();
            viewHolder.onFinishInflate(this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            viewHolder.onAttachedToWindow(this);
            //noinspection unchecked
            viewHolder.linkWith(abstractDiscussionCompactDTO);
            //noinspection unchecked
            subscriptions.add(viewHolder.getUserActionObservable()
                    .flatMap(new Func1<DiscussionActionButtonsView.UserAction,
                            Observable<DiscussionActionButtonsView.UserAction>>()
                    {
                        @Override public Observable<DiscussionActionButtonsView.UserAction> call(
                                DiscussionActionButtonsView.UserAction userAction)
                        {
                            return handleUserAction(userAction);
                        }
                    })
                    .subscribe(new EmptyObserver()));
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        viewHolder.onDetachedFromWindow();
        subscriptions.unsubscribe();
        subscriptions = new SubscriptionList();
        super.onDetachedFromWindow();
    }

    @NonNull protected AbstractDiscussionCompactItemViewHolder createViewHolder()
    {
        return new AbstractDiscussionCompactItemViewHolder<>(getContext());
    }

    @NonNull protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.MoreUserAction)
        {
            return socialShareHelper.show(abstractDiscussionCompactDTO, false)
                    .flatMap(result -> Observable.empty());
        }
        return Observable.just(userAction);
    }

    @Override public void display(T discussionKey)
    {
        this.discussionKey = discussionKey;
        fetchDiscussionDetail();
    }

    protected void fetchDiscussionDetail()
    {
        if (discussionKey instanceof DTOKey)
        {
            AbstractDiscussionCompactDTO value = discussionCache.getCachedValue(((DiscussionKey) discussionKey));
            if (value != null)
            {
                linkWith(value);
            }
            else
            {
                refresh();
            }
        }
        else if (discussionKey instanceof AbstractDiscussionCompactDTO)
        {
            linkWith((AbstractDiscussionCompactDTO) discussionKey);
        }
    }

    public void refresh()
    {
        subscriptions.add(discussionCache.get((DiscussionKey) discussionKey)
                .map(pair -> pair.second)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::linkWith,
                        e -> THToast.show(new THException(e))
                ));
    }

    protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO)
    {
        this.abstractDiscussionCompactDTO = abstractDiscussionDTO;
        //noinspection unchecked
        viewHolder.linkWith(abstractDiscussionDTO);
    }

    protected DashboardNavigator getNavigator()
    {
        return dashboardNavigator;
    }
}
