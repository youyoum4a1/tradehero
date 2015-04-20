package com.tradehero.th.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.tradehero.chinabuild.fragment.web.CallNativeFromJS;
import com.tradehero.th.R;
import com.tradehero.th.utils.StringUtils;

/**
 * Play videos online
 *
 * Created by palmer on 15/4/20.
 */
public class VideoPlayActivity extends Activity{


    public static final String BUNDLE_VIDEO_VID = "bundle_video_vid";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_layout);
        webView = (WebView)findViewById(R.id.webViewSimple);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.addJavascriptInterface(new CallNativeFromJS(), "CallNativeFromJS");
        playVideo();
    }

    @Override
    public void onResume() {
        super.onResume();
        //恢复播放

        webView.resumeTimers();
        try {
            webView.getClass().getMethod("onResume")
                    .invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //暂停播放
        webView.pauseTimers();
        try {
            webView.getClass().getMethod("onPause")
                    .invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        webView.onPause();

        String PHONE = android.os.Build.MODEL;
        if ((!StringUtils.isNullOrEmpty(PHONE)) && PHONE.startsWith("HM")) {
            webView.stopLoading();
            try {
                webView.pauseTimers();
                webView.stopLoading();
                webView.loadData("", "text/html", "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

    private String setWebView(String clientId, String vid)
    {
        return "<html>"
                + "<body>"
                + "<div id='youkuplayer' style='width:100%;height:100%'></div>"
                + "<script type='text/javascript' src='http://player.youku.com/jsapi'>"
                + "player = new YKU.Player('youkuplayer',{styleid: '0',client_id: '"
                + clientId
                + "',vid: '"
                + vid
                + "',autoplay: true,embsig: 'VERSION_TIMESTAMP_SIGNATURE'});</script>"
                + "</body>" + "</html>";
    }

    private String getVid()
    {
        Bundle arg = getIntent().getExtras();
        if (arg != null)
        {
            return arg.getString(BUNDLE_VIDEO_VID, "");
        }
        return "";
    }

    private void playVideo() {
        webView.loadData(setWebView("9c46c940343c4dea", getVid()),
                "text/html", "UTF-8");
    }
}
