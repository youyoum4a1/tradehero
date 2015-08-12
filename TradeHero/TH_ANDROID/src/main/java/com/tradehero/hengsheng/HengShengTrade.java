package com.tradehero.hengsheng;

import android.text.TextUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palmer on 15/8/11.
 */
public class HengShengTrade {

    private static HengShengTrade hengShengTrade = null;

    private static String accessToken = "";

    public final static String APP_KEY = "72ce07d4-1270-40d8-a158-0a9d0e26c61c";
    public final static String APP_SECRET = "1135189c-3a38-4794-8de8-48359d9b2e55";

    /**
     * HTTP HEADER字段 Authorization应填充字符串Bearer
     */
    public static final String BEARER = "Bearer ";

    /**
     * HTTP HEADER字段 Authorization应填充字符串BASIC
     */
    public static final String BASIC = "Basic YTE1ZTZhMTEtNTk5My00MTA5LWI0N2MtMzUyOGFhNzA3ZmYwOjMxNjAwNDdlLTBhNWEtNDc1OS05ZDQ1LWM5N2ZkY2E3OGU5MQ==";

    private HengShengTrade(){

    }

    public static HengShengTrade getInstance(){
        synchronized (HengShengTrade.class){
            if(hengShengTrade == null){
                hengShengTrade = new HengShengTrade();
            }
            return hengShengTrade;
        }
    }

    public boolean isLogin(){
        if(TextUtils.isEmpty(accessToken)){
            return false;
        }
        return true;
    }

    public boolean isLogout(){
        if(TextUtils.isEmpty(accessToken)){
            return true;
        }
        return false;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public static String getAccessToken(String account_content, String password){
        String uriAPI = "http://sandbox.hs.net/oauth2/oauth2/oauthacct_trade_bind";
        HttpPost httpRequest =new HttpPost(uriAPI);
        httpRequest.addHeader("Authorization",BASIC);
        List<NameValuePair> params=new ArrayList();
        params.add(new BasicNameValuePair("targetcomp_id", "91000"));
        params.add(new BasicNameValuePair("sendercomp_id", "91008"));
        params.add(new BasicNameValuePair("targetbusinsys_no", "1000"));
        params.add(new BasicNameValuePair("op_station", "http://www.baidu.com/"));
        params.add(new BasicNameValuePair("input_content", "6"));
        params.add(new BasicNameValuePair("account_content", account_content));
        params.add(new BasicNameValuePair("password", password));

        String result = "";

        try{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            result = EntityUtils.toString(httpResponse.getEntity());
        }catch(ClientProtocolException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String queryBankAccount(){
        if(TextUtils.isEmpty(hengShengTrade.getAccessToken())){
            return "";
        }
        String uriAPI = "https://sandbox.hs.net/secu/v1/bankaccout_qry";
        HttpPost httpRequest =new HttpPost(uriAPI);
        httpRequest.addHeader("Authorization",BEARER + hengShengTrade.getAccessToken());
        List<NameValuePair> params=new ArrayList();
        params.add(new BasicNameValuePair("targetcomp_id", "91000"));
        params.add(new BasicNameValuePair("sendercomp_id", "91008"));
        params.add(new BasicNameValuePair("bank_no", "1212"));
        String result = "";

        try{
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            result = EntityUtils.toString(httpResponse.getEntity());
        }catch(ClientProtocolException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
