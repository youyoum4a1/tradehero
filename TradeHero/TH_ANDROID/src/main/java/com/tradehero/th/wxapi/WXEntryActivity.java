package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import timber.log.Timber;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    public static final int WECHAT_MESSAGE_TYPE_BUY_SELL = 0;
    public static final int WECHAT_MESSAGE_TYPE_CREATE_DIS = 1;
    public static final int WECHAT_MESSAGE_TYPE_DIS = 2;
    public static final int WECHAT_MESSAGE_TYPE_NEWS = 3;
    public static final int WECHAT_MESSAGE_TYPE_TIMELINE = 4;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    public static final String WECHAT_MESSAGE_ID_KEY = "wechat_message_id_key";
    public static final String WECHAT_MESSAGE_TYPE_KEY = "wechat_message_type_key";
    private static final String WECHAT_SHARE_MSG_KEY = "msg";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_KEY = "type";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";
    private static final String WECHAT_SHARE_URL = "https://www.tradehero.mobi/api/users/";

    private IWXAPI api;
    private int mMsgType;
    private int mMsgId;

    @Inject CurrentUserId currentUserId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mMsgType = getIntent().getIntExtra(WECHAT_MESSAGE_TYPE_KEY, WECHAT_MESSAGE_TYPE_NONE);
        mMsgId = getIntent().getIntExtra(WECHAT_MESSAGE_ID_KEY, 0);
        api = WXAPIFactory.createWXAPI(this, Constants.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);

        api.registerApp(Constants.WECHAT_APP_ID);
        boolean isWXInstalled = api.isWXAppInstalled();
        if (isWXInstalled)
        {
            if (api.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = Constants.BASE_STATIC_CONTENT_URL;
                WXMediaMessage msg = new WXMediaMessage(webpage);
                msg.title = getMsgTitle(mMsgType);
                msg.description = msg.title;
                Bitmap thumb =
                        BitmapFactory.decodeResource(getResources(), R.drawable.notification_logo);
                msg.thumbData = Util.bmpToByteArray(thumb, true);

                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(
                        System.currentTimeMillis()); //not sure for transaction, maybe identify id?
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                api.sendReq(req);
            }
            else
            {
                THToast.show(getString(R.string.need_update_wechat));
                finish();
            }
        }
        else
        {
            THToast.show(getString(R.string.need_install_wechat));
            finish();
        }
    }

    private void submit()
    {
        String url = WECHAT_SHARE_URL + currentUserId.toUserBaseKey().key + "/trackshare";
        String resultStr = "";
        HttpClient httpclient = getNewHttpClient();

        //Timber.d("WX userId=%s mMsgId=%d", currentUserId.toUserBaseKey().key, mMsgId);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(WECHAT_SHARE_MSG_KEY,
                WECHAT_SHARE_NEWS_KEY + String.valueOf(mMsgId)));
        nameValuePairs.add(new BasicNameValuePair(WECHAT_SHARE_TYPE_KEY, WECHAT_SHARE_TYPE_VALUE));
        HttpPost httppost = new HttpPost(url);
        try
        {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httppost.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
            HttpResponse response;
            response = httpclient.execute(httppost);
            resultStr = EntityUtils.toString(response.getEntity());
            //Timber.d("WX resultStr=%s", resultStr);
        } catch (UnsupportedEncodingException e)
        {
            Timber.d("WX UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e)
        {
            Timber.d("WX ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e)
        {
            Timber.d("WX IOException");
            e.printStackTrace();
        }
        finish();
    }

    public static HttpClient getNewHttpClient()
    {
        try
        {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e)
        {
            return new DefaultHttpClient();
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req)
    {
        switch (req.getType())
        {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                //goToGetMsg();
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                //goToShowMsg((ShowMessageFromWX.Req) req);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp)
    {
        //Timber.d("WX onResp %d %d", resp.errCode, mMsgType);
        //THToast.show("WX onResp=" + resp.errCode + " mMsgType=" + mMsgType);
        int result = 0;
        switch (resp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                THToast.show(getString(R.string.share_success));
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        submit();
                    }
                }.start();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    private String getMsgTitle(int type)
    {
        switch (type)
        {
            case WECHAT_MESSAGE_TYPE_NEWS:
                return getString(R.string.share_to_wechat_timeline_news);
            case WECHAT_MESSAGE_TYPE_CREATE_DIS:
                return getString(R.string.share_to_wechat_timeline_create_dis);
            case WECHAT_MESSAGE_TYPE_DIS:
                return getString(R.string.share_to_wechat_timeline_dis);
            case WECHAT_MESSAGE_TYPE_TIMELINE:
                return getString(R.string.share_to_wechat_timeline_timeline);
            case WECHAT_MESSAGE_TYPE_BUY_SELL:
                return getString(R.string.share_to_wechat_timeline_buy_sell);
            default:
                break;
        }
        return getString(R.string.share_to_wechat_timeline_news);
    }
}