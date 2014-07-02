package com.tradehero.th.fragments.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.home.HomeContentDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class HomeFragment extends BaseWebViewFragment
{
    @InjectView(android.R.id.progress) View progressBar;
    @InjectView(R.id.main_content_wrapper) BetterViewAnimator mainContentWrapper;

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject @LanguageCode String languageCode;
    @Inject CurrentUserId currentUserId;
    @Inject HomeContentCache homeContentCache;
    private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> homeContentCacheListener;

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_home_webview;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);
        ButterKnife.inject(this, view);

        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setUseWideViewPort(false);

        detachHomeCacheListener();
        homeContentCacheListener = createHomeContentCacheListener();
        homeContentCache.register(currentUserId.toUserBaseKey(), homeContentCacheListener);
        homeContentCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
    }

    @Override public void onStop()
    {
        detachHomeCacheListener();
        super.onStop();
    }

    private void detachHomeCacheListener()
    {
        homeContentCache.unregister(currentUserId.toUserBaseKey(), homeContentCacheListener);
    }

    private DTOCacheNew.Listener<UserBaseKey, HomeContentDTO> createHomeContentCacheListener()
    {
        return new HomeContentCacheListener();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        homeContentCache.getOrFetchAsync(currentUserId.toUserBaseKey(), true);
        super.onDestroyView();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        reloadWebView();
    }

    private void reloadWebView()
    {
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(Constants.AUTHORIZATION, createTypedAuthParameters(mainCredentialsPreference.getCredentials()));
        additionalHeaders.put(Constants.TH_CLIENT_VERSION, VersionUtils.getVersionId(getActivity()));
        additionalHeaders.put(Constants.TH_LANGUAGE_CODE, languageCode);

        String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        HomeContentDTO homeContentDTO = homeContentCache.get(currentUserId.toUserBaseKey());
        if (homeContentDTO != null)
        {
            Timber.d("Getting home app data from cache!");
            webView.loadDataWithBaseURL(Constants.BASE_STATIC_CONTENT_URL, homeContentDTO.content, "text/html", "", appHomeLink);
        }
        else
        {
            loadUrl(appHomeLink, additionalHeaders);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(R.string.dashboard_home);
        inflater.inflate(R.menu.menu_refresh_button, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_fresh:
                homeContentCache.invalidate(currentUserId.toUserBaseKey());
                reloadWebView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);
        Activity activity = getActivity();
        if (activity != null)
        {
            activity.setProgress(newProgress * 100);
        }

        if (mainContentWrapper != null && newProgress > 50)
        {
            mainContentWrapper.setDisplayedChildByLayoutId(R.id.webview);
        }
    }

    public String createTypedAuthParameters(CredentialsDTO credentialsDTO)
    {
        return String.format("%1$s %2$s", credentialsDTO.getAuthType(), credentialsDTO.getAuthHeaderParameter());
    }


    private class HomeContentCacheListener implements DTOCacheNew.Listener<UserBaseKey, HomeContentDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull HomeContentDTO value)
        {
            reloadWebView();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            // do nothing
        }
    }
}
