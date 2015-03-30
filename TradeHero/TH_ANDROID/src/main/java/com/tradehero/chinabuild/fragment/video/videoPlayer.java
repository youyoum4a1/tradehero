package com.tradehero.chinabuild.fragment.video;

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
    public void onResume() {
        super.onResume();
        //恢复播放
        webViewSimple.resumeTimers();
    }

    @Override
    public void onPause() {
        super.onPause();
        //暂停播放
        webViewSimple.pauseTimers();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        webViewSimple.destroy();
    }

    public String setWebView(String clientid, String vid) {
        return "<html>"
                + "<body>"
                + "<div id='youkuplayer' style='width:95%;height:95%'></div>"
                + "<script type='text/javascript' src='http://player.youku.com/jsapi'>"
                + "player = new YKU.Player('youkuplayer',{styleid: '0',client_id: '"
                + clientid
                + "',vid: '"
                + vid
                + "',autoplay: true,embsig: 'VERSION_TIMESTAMP_SIGNATURE'});</script>"
                + "</body>" + "</html>";
    }


    @Override public void loadUrl(String url)
    {
        //super.loadUrl(url);
        webViewSimple.loadData(setWebView("9c46c940343c4dea", "XOTE5NTY5NTY4"),
                "text/html", "UTF-8");
    }
}
