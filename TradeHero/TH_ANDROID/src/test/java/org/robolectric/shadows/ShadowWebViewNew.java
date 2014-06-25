package org.robolectric.shadows;

import android.webkit.WebView;
import java.util.Collections;
import java.util.Map;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(value = WebView.class)
public class ShadowWebViewNew extends ShadowWebView
{
    private Map<String, String> additionalHttpHeaders;

    @Implementation
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders)
    {
        loadUrl(url);

        if (additionalHttpHeaders != null)
        {
            this.additionalHttpHeaders = Collections.unmodifiableMap(additionalHttpHeaders);
        }
        else
        {
            this.additionalHttpHeaders = null;
        }
    }

    public Map<String, String> getLastAdditionalHttpHeaders()
    {
        return additionalHttpHeaders;
    }
}
