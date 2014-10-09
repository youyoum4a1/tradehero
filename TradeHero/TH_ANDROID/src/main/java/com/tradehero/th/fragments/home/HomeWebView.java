package com.tradehero.th.fragments.home;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.NotifyingWebView;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public final class HomeWebView extends NotifyingWebView
{
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCache homeContentCache;
    @Inject DTOCacheUtil dtoCacheUtil;

    @Nullable private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> homeContentCacheListener;

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
        homeContentCacheListener = createHomeContentCacheListener();
        homeContentCache.register(currentUserId.toUserBaseKey(), homeContentCacheListener);
        homeContentCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachHomeCacheListener();
        homeContentCacheListener = null;
        super.onDetachedFromWindow();
    }

    private void detachHomeCacheListener()
    {
        homeContentCache.unregister(currentUserId.toUserBaseKey(), homeContentCacheListener);
    }

    private void reloadWebView(@NotNull HomeContentDTO homeContentDTO)
    {
        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        loadDataWithBaseURL(Constants.BASE_STATIC_CONTENT_URL, homeContentDTO.content, "text/html", "", appHomeLink);
    }

    //<editor-fold desc="Listeners">
    @NotNull private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> createHomeContentCacheListener()
    {
        return new HomeContentCacheListener();
    }

    private class HomeContentCacheListener implements DTOCacheNew.HurriedListener<UserBaseKey, HomeContentDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull HomeContentDTO value)
        {
            reloadWebView(value);
        }

        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HomeContentDTO value)
        {
            reloadWebView(value);
            dtoCacheUtil.anonymousPrefetches();
            dtoCacheUtil.initialPrefetches();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            Timber.e(error, "Failed fetching home page for key %s", key);
        }
    }
    //</editor-fold>
}
