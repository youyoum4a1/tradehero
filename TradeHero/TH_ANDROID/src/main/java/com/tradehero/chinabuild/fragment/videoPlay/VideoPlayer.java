package com.tradehero.chinabuild.fragment.videoPlay;

import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.th.R;

/**
 * Created by huhaiping on 15/3/30.
 */
public class VideoPlayer extends WebViewFragment
{

    public static final String BUNDLE_VIDEO_VID = "bundle_video_vid";

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.stock_learning_video));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //恢复播放
        webViewSimple.resumeTimers();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        //暂停播放
        webViewSimple.pauseTimers();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        webViewSimple.destroy();
    }

    public String setWebView(String clientid, String vid)
    {
        return "<html>"
                + "<body>"
                + "<div id='youkuplayer' style='width:100%;height:100%'></div>"
                + "<script type='text/javascript' src='http://player.youku.com/jsapi'>"
                + "player = new YKU.Player('youkuplayer',{styleid: '0',client_id: '"
                + clientid
                + "',vid: '"
                + vid
                + "',autoplay: true,embsig: 'VERSION_TIMESTAMP_SIGNATURE'});</script>"
                + "</body>" + "</html>";
    }

    public String getVid()
    {
        Bundle arg = getArguments();
        if (arg != null)
        {
            return arg.getString(BUNDLE_VIDEO_VID, "");
        }
        return "";
    }

    @Override public void loadUrl(String url)
    {
        //super.loadUrl(url);
        webViewSimple.loadData(setWebView("9c46c940343c4dea", getVid()),
                "text/html", "UTF-8");
    }
}