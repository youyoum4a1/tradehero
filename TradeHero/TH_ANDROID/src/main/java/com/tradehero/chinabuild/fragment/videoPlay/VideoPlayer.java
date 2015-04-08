package com.tradehero.chinabuild.fragment.videoPlay;

import android.content.pm.ActivityInfo;
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
    public static final String BUNDLE_VIDEO_NAME = "bundle_video_name";


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        String videoName = getVideoName();
        if(videoName.equals("")) {
            setHeadViewMiddleMain(getString(R.string.stock_learning_video));
        }else{
            setHeadViewMiddleMain(videoName);
        }
    }

    @Override
    public void onResume() {
        if(getActivity().getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        webViewSimple.destroy();
    }

    private String setWebView(String clientId, String vid) {
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

    private String getVid() {
        Bundle arg = getArguments();
        if (arg != null)
        {
            return arg.getString(BUNDLE_VIDEO_VID, "");
        }
        return "";
    }

    private String getVideoName(){
        Bundle arg = getArguments();
        if(arg!=null && arg.containsKey(BUNDLE_VIDEO_NAME)){
            return arg.getString(BUNDLE_VIDEO_NAME, "");
        }
        return "";
    }

    @Override
    public void loadUrl(String url) {
        webViewSimple.loadData(setWebView("9c46c940343c4dea", getVid()),
                "text/html", "UTF-8");
    }
}
