
package com.tradehero.th.fragments.web;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import timber.log.Timber;

@Routable("web/url/:requiredUrlEncoded")
public class WebViewFragment extends BaseWebViewFragment
{
    @Inject THRouter thRouter;

    @RouteProperty("requiredUrlEncoded") String requiredUrlEncoded;

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
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.webview_menu, menu);
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
}
