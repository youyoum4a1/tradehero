package com.tradehero.th.models;

import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import android.content.Context;

public class Request
{
    public static final int REQUEST_TYPE_POST = -100;
    public static final int REQUEST_TYPE_POST_JSON = -101;
    public static final int REQUEST_TYPE_POST_PARAMS = -102;
    public static final int REQUEST_TYPE_GET = -200;
    public static final int REQUEST_TYPE_BASIC_AUTH = -300;
    private String mApiUrl;
    private Context mContext;
    private String mUserName;
    private String mPassword;
    private JSONObject mReqJsonObject;
    private Header[] mHeaders;
    private String mClientParams;
    private ArrayList<BasicNameValuePair> mParameters = new ArrayList<BasicNameValuePair>();

    private int mRequestType = REQUEST_TYPE_POST;
    private ArrayList<Header> mBasicHeaders = new ArrayList<Header>();
    private ArrayList<BasicHttpParams> mBasicHttpParams = new ArrayList<BasicHttpParams>();

    public String getApiUrl()
    {
        return mApiUrl;
    }

    public void setApiUrl(String pApiUrl)
    {
        this.mApiUrl = pApiUrl;
    }

    public void addRequestHeader(Header pBasicHeader)
    {
        mBasicHeaders.add(pBasicHeader);
    }

    public ArrayList<Header> getHeaders()
    {
        return mBasicHeaders;
    }

    public int getRequestType()
    {
        return mRequestType;
    }

    public void setRequestType(int pRequestType)
    {
        this.mRequestType = pRequestType;
    }

    public Context getContext()
    {
        return mContext;
    }

    public void setContext(Context mContext)
    {
        this.mContext = mContext;
    }

    public ArrayList<BasicNameValuePair> getParameters()
    {
        return mParameters;
    }

    public void setParameter(BasicNameValuePair pParameter)
    {
        this.mParameters.add(pParameter);
    }

    public String getUserName()
    {
        return mUserName;
    }

    public void setUserName(String mUserName)
    {
        this.mUserName = mUserName;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(String mPassword)
    {
        this.mPassword = mPassword;
    }

    public JSONObject getReqJsonObject()
    {
        return mReqJsonObject;
    }

    public void setReqJsonObject(JSONObject pReqJsonObject)
    {
        this.mReqJsonObject = pReqJsonObject;
    }

    public ArrayList<BasicHttpParams> getBasicHttpParams()
    {
        return mBasicHttpParams;
    }

    public void addBasicHttpParams(BasicHttpParams pBasicHttpParam)
    {
        this.mBasicHttpParams.add(pBasicHttpParam);
    }

    public Header[] getmeaders()
    {
        return mHeaders;
    }

    public void setHeaders(Header[] mHeaders)
    {
        this.mHeaders = mHeaders;
    }

    public String getClientParams()
    {
        return mClientParams;
    }

    public void setClientParams(String mClientParams)
    {
        this.mClientParams = mClientParams;
    }
}
