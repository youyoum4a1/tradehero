package com.tradehero.th.fragments.home;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.th.R;
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
    private static final String URL = "http://www.tradehero.mobi/AppHome";

    @Inject MainCredentialsPreference mainCredentialsPreference;
    @Inject @LanguageCode String languageCode;
    @Inject ResideMenu resideMenu;

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
    }

    @Override protected void initViews(View v)
    {
        super.initViews(v);

        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setUseWideViewPort(false);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);


        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put(Constants.AUTHORIZATION, createTypedAuthParameters(mainCredentialsPreference.getCredentials()));
        additionalHeaders.put(Constants.TH_CLIENT_VERSION, VersionUtils.getVersionId(getActivity()));
        additionalHeaders.put(Constants.TH_LANGUAGE_CODE, languageCode);
        loadUrl(URL, additionalHeaders);
    }


    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setTitle(R.string.home);
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

    public String createTypedAuthParameters(CredentialsDTO credentialsDTO)
    {
        return String.format("%1$s %2$s", credentialsDTO.getAuthType(), credentialsDTO.getAuthHeaderParameter());
    }
}
