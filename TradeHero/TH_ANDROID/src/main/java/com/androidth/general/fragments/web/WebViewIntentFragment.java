
package com.androidth.general.fragments.web;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.AlertDialogRxUtil;
import com.androidth.general.utils.route.THRouter;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class WebViewIntentFragment extends BaseWebViewIntentFragment
{
    @Inject THRouter thRouter;

    @RouteProperty("requiredUrlEncoded") String requiredUrlEncoded;

    private String navigationUrl;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        Bundle args = getArguments();
        if (requiredUrlEncoded != null && args != null)
        {
            try
            {
                putUrl(args, Uri.decode(requiredUrlEncoded));
            } catch (Exception e)
            {
                Timber.e(e, "Failed to decode Url %s", requiredUrlEncoded);
            }
        }

//        if(getArguments().containsKey(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)){
//            navigationUrl = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
//        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.webview_menu, menu);
        MenuItem back = menu.findItem(R.id.webview_back);
        if (back != null)
        {
            back.setVisible(webView.canGoBack());
        }
        MenuItem forward = menu.findItem(R.id.webview_forward);
        if (forward != null)
        {
            forward.setVisible(webView.canGoForward());
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.webview_back:
                if (webView.canGoBack())
                {
                    webView.goBack();
                }
                break;

            case R.id.webview_forward:
                if (webView.canGoForward())
                {
                    webView.goForward();
                }
                break;

            case R.id.webview_view_in_browser:
            {
                String url = webView.getUrl();
                if (url != null)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    try
                    {
                        getActivity().startActivity(intent);
                    } catch (ActivityNotFoundException e)
                    {
                        Timber.e(e, "No activity for %s", url);
                        onStopSubscriptions.add(AlertDialogRxUtil.buildDefault(getActivity())
                                .setTitle(R.string.webview_error_no_browser_for_intent_title)
                                .setMessage(R.string.webview_error_no_browser_for_intent_description)
                                .setPositiveButton(R.string.cancel)
                                .build()
                                .subscribe(
                                        new EmptyAction1<OnDialogClickEvent>(),
                                        new EmptyAction1<Throwable>()));
                    }
                }
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override public void loadUrl(@Nullable String url, @Nullable Map<String, String> additionalHttpHeaders)
    {
        super.loadUrl(url, additionalHttpHeaders);
        getActivity().invalidateOptionsMenu();
    }
}
