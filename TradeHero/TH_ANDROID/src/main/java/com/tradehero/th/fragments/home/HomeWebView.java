package com.tradehero.th.fragments.home;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public final class HomeWebView extends WebView
{
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCache homeContentCache;

    private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> homeContentCacheListener;

    //region Constructors
    public HomeWebView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //endregion

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        
        DaggerUtils.inject(this);
    }

    @Override public void reload()
    {
        reloadWebView();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
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

    private void reloadWebView()
    {
        reloadWebView(homeContentCache.get(currentUserId.toUserBaseKey()));
    }

    private void reloadWebView(@Nullable HomeContentDTO homeContentDTO)
    {
        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        if (homeContentDTO != null)
        {
            Timber.d("Getting home app data from cache!");
            loadDataWithBaseURL(Constants.BASE_STATIC_CONTENT_URL, homeContentDTO.content, "text/html", "", appHomeLink);
        }
    }

    public String createTypedAuthParameters(@NotNull CredentialsDTO credentialsDTO)
    {
        return String.format("%1$s %2$s", credentialsDTO.getAuthType(), credentialsDTO.getAuthHeaderParameter());
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
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            // do nothing
        }
    }
    //</editor-fold>
}
