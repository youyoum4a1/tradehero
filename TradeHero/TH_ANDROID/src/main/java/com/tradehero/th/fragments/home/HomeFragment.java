package com.tradehero.th.fragments.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.VersionUtils;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class HomeFragment extends BaseWebViewFragment
{
    @InjectView(android.R.id.progress) View progressBar;
    @InjectView(R.id.main_content_wrapper) BetterViewAnimator mainContentWrapper;

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject @LanguageCode String languageCode;
    @Inject ResideMenu resideMenu;
    @Inject CurrentUserId currentUserId;

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
        thWebViewClient.setClearCacheAfterFinishRequest(false);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(Constants.AUTHORIZATION, createTypedAuthParameters(mainCredentialsPreference.getCredentials()));
        additionalHeaders.put(Constants.TH_CLIENT_VERSION, VersionUtils.getVersionId(getActivity()));
        additionalHeaders.put(Constants.TH_LANGUAGE_CODE, languageCode);

        String appHomeLink = Constants.APP_HOME;
        // use following line instead when the server is ready
        // String appHomeLink = String.format("%s/%d", Constants.APP_HOME, currentUserId.get());

        loadUrl(appHomeLink, additionalHeaders);
    }


    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setTitle(R.string.dashboard_home);
        actionBar.setLogo(R.drawable.icn_actionbar_hamburger);
        actionBar.setHomeButtonEnabled(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                resideMenu.openMenu();
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

        if (mainContentWrapper != null && newProgress > 90)
        {
            mainContentWrapper.setDisplayedChildByLayoutId(R.id.webview);
        }
    }

    public String createTypedAuthParameters(CredentialsDTO credentialsDTO)
    {
        return String.format("%1$s %2$s", credentialsDTO.getAuthType(), credentialsDTO.getAuthHeaderParameter());
    }
}
