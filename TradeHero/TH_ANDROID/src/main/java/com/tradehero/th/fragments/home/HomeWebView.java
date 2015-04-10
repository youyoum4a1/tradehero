package com.tradehero.th.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import com.tradehero.common.widget.NotifyingWebView;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public final class HomeWebView extends NotifyingWebView
{
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCacheRx homeContentCache;
    @Inject DTOCacheUtilImpl dtoCacheUtil;

    @Nullable private Subscription homeContentCacheSubscription;

    //region Constructors
    public HomeWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
    }

    @Override public void reload()
    {
        forceReloadWebView();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        forceReloadWebView();
    }

    private void forceReloadWebView()
    {
        detachHomeCacheListener();
        homeContentCacheSubscription = homeContentCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createHomeContentCacheObserver());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachHomeCacheListener();
        homeContentCacheSubscription = null;
        super.onDetachedFromWindow();
    }

    private void detachHomeCacheListener()
    {
        Subscription copy = homeContentCacheSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        homeContentCacheSubscription = null;
    }

    private void reloadWebView(@NonNull HomeContentDTO homeContentDTO)
    {
        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        loadDataWithBaseURL(Constants.BASE_STATIC_CONTENT_URL, homeContentDTO.content, "text/html", "", appHomeLink);
    }

    //<editor-fold desc="Listeners">
    @NonNull private Observer<Pair<UserBaseKey, HomeContentDTO>> createHomeContentCacheObserver()
    {
        return new HomeContentCacheObserver();
    }

    private class HomeContentCacheObserver implements Observer<Pair<UserBaseKey, HomeContentDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, HomeContentDTO> pair)
        {
            reloadWebView(pair.second);
//            dtoCacheUtil.anonymousPrefetches();
//            dtoCacheUtil.initialPrefetches();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Failed fetching home page for key");
        }
    }
    //</editor-fold>
}
