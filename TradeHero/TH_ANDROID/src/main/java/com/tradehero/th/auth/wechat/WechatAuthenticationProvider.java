package com.tradehero.th.auth.wechat;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.models.user.auth.WeChatCredentialsDTO;
import com.tradehero.th.network.https.HttpClientHelper;
import com.tradehero.th.utils.dagger.SocialNetworkModule;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

@Singleton
public class WechatAuthenticationProvider extends SocialAuthenticationProvider
{
    private WechatAuthData mAccessToken;

    public static final String KEY_OPEN_ID = "wechat_openid";
    public static final String KEY_ACCESS_TOKEN = "wechat_access_token";

    private String code;

    public void setCode(String code)
    {
        this.code = code;
        Timber.d("TradeHero: code = " + code);
    }

    @Inject public WechatAuthenticationProvider()
    {
    }
    //</editor-fold>

    @Override
    public void authenticate(THAuthenticationCallback callback)
    {
        doAuthenticate(callback);
    }

    private void doAuthenticate(THAuthenticationCallback callback)
    {
        if (callback == null)
        {
            return;
        }
        if (currentOperationCallback != null)
        {
            onAuthorizeCancel();
        }
        currentOperationCallback = callback;

        callback.onStart();

        getWechatAuthData();
    }

    private void getWechatAuthData()
    {
        GetAccessTokenTask task = new GetAccessTokenTask();
        task.execute();
    }

    private void onAnthorizeSuccess(WechatAuthData wechatData)
    {
        this.mAccessToken = wechatData;
        if (checkContext() && currentOperationCallback != null)
        {
            currentOperationCallback.onSuccess(buildTokenData(wechatData));
            currentOperationCallback = null;
        }
    }

    private JSONCredentials buildTokenData(WechatAuthData data)
    {
        JSONCredentials obj = null;
        try
        {
            obj = new JSONCredentials();
            obj.put(WechatAuthenticationProvider.KEY_OPEN_ID, data.openid);
            obj.put(WechatAuthenticationProvider.KEY_ACCESS_TOKEN, data.access_token);
        } catch (JSONException e)
        {
        }
        return obj;
    }

    private boolean checkContext()
    {
        if (baseContext != null && baseContext.get() != null)
        {
            Context ctx = baseContext.get();
            if (ctx instanceof Activity)
            {
                return !((Activity) ctx).isFinishing();
            }
        }
        return false;
    }

    private void onAuthorizeCancel()
    {
        handleCancel(currentOperationCallback);
        currentOperationCallback = null;
    }

    @Override public void deauthenticate()
    {
    }

    @Override
    public boolean restoreAuthentication(JSONCredentials paramJSONObject)
    {
        return false;
    }

    @Override
    public String getAuthType()
    {
        return WeChatCredentialsDTO.WECHAT_AUTH_TYPE;
    }

    @Override
    public String getAuthHeaderParameter()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mAccessToken.openid).append(":").append(mAccessToken.access_token);
        return sb.toString();
    }

    public WechatAuthData parseData(String resp)
    {
        WechatAuthData wechatAuthData = new WechatAuthData();
        try
        {
            JSONObject jsonObject = new JSONObject(resp);
            wechatAuthData.access_token = (String) jsonObject.get("access_token");
            wechatAuthData.openid = (String) jsonObject.get("openid");
            wechatAuthData.expires_in = (String) jsonObject.get("expires_in");
        } catch (Exception e)
        {

        }

        return wechatAuthData;
    }

    public class GetAccessTokenTask extends AsyncTask<Void, Void, WechatAuthData>
    {

        @Override protected WechatAuthData doInBackground(Void... voids)
        {
            WechatAuthData wechatAuthData = null;

            HttpClient httpClientHelper = HttpClientHelper.getHttpClient();
            String uriAPI = "https://api.weixin.qq.com/sns/oauth2/access_token";
            HttpPost httpRequest = new HttpPost(uriAPI);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("appid", SocialNetworkModule.WECHAT_APP_ID));
            params.add(new BasicNameValuePair("secret", SocialNetworkModule.WECHAT_APP_SECRET));
            params.add(new BasicNameValuePair("code", code));
            params.add(new BasicNameValuePair("grant_type", "authorization_code"));

            try
            {
                //发出HTTP request
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                //取得HTTP response
                HttpResponse httpResponse = httpClientHelper.execute(httpRequest);

                //若状态码为200 ok
                if (httpResponse.getStatusLine().getStatusCode() == 200)
                {
                    //取出回应字串
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    Timber.d("Tradehero: strResult = " + strResult);
                    THToast.show(strResult);
                    wechatAuthData = parseData(strResult);
                }
                else
                {
                    THToast.show("Error Response" + httpResponse.getStatusLine().toString());
                }
            } catch (ClientProtocolException e)
            {

                e.printStackTrace();
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return wechatAuthData;
        }

        protected void onPostExecute(WechatAuthData result)
        {
            if (result == null)
            {
                onAuthorizeCancel();
            }
            else
            {
                onAnthorizeSuccess(result);
            }
        }
    }
}
