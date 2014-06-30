
package com.tradehero.th.fragments.web;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.thm.R;
import timber.log.Timber;

public class WebViewFragment extends BaseWebViewFragment
{
    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
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
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Timber.e(e, "No activity for %s", url);
                        alertDialogUtil.popWithNegativeButton(
                                getActivity(),
                                R.string.webview_error_no_browser_for_intent_title,
                                R.string.webview_error_no_browser_for_intent_description,
                                R.string.cancel);
                    }
                }
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>
}
