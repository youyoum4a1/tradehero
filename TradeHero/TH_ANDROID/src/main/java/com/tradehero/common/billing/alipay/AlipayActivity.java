package com.tradehero.common.billing.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import com.alipay.android.app.net.SSLSocketFactoryEx;
import com.alipay.android.app.sdk.AliPay;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.utils.Constants;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import timber.log.Timber;

/**
 * Created by alex on 14-3-31.
 */
public class AlipayActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Timber.d("lyl onCreate");
        new Thread()
        {
            @Override
            public void run()
            {
                beginAlipay(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
            }
        }.start();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Timber.d("lyl onNewIntent");
        new Thread()
        {
            @Override
            public void run()
            {
                beginAlipay(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
            }
        }.start();
    }

    Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
        Result result = new Result((String) msg.obj);
        Timber.d("lyl result=%s", result);
        Timber.d("lyl result=%s", result.getResult());
        //switch (msg.what) {
        //    case RQF_PAY:
        //    case RQF_LOGIN: {
        //        Toast.makeText(getSherlockActivity(), result.getResult(),
        //                Toast.LENGTH_SHORT).show();
        //
        //    }
        //    break;
        //    default:
        //        break;
        //}
    };
};

    private void beginAlipay(int type)
    {
        Timber.d("lyl beginAlipay");
        String info = getNewOrderInfo();
        String sign = Rsa.sign(info, Constants.PRIVATE);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        Timber.d("lyl info=%s", info);

        AliPay alipay = new AliPay(this, mHandler);
        String result = alipay.pay(info);
        Timber.d("lyl result=%s", result);

        Message msg = new Message();
        msg.what = 1;//RQF_PAY;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    private String getNewOrderInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Constants.DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append("alipay-" + getOrderId());
        sb.append("\"&subject=\"");
        sb.append("subject");
        sb.append("\"&body=\"");
        sb.append("body");
        sb.append("\"&total_fee=\"");
        sb.append("0.01");
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode("http://www.tradehero.mobi/api/alipay/notify"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Constants.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");

        return new String(sb);
    }

    private String getOrderId()
    {
        String url = "https://www.tradehero.mobi/api/alipay/createOrder/10";
        String resultStr = "";
        HttpClient httpclient = getNewHttpClient();

        HttpPost httppost = new HttpPost(url);
        try
        {
            httppost.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
            HttpResponse response;
            response = httpclient.execute(httppost);
            resultStr = EntityUtils.toString(response.getEntity());
            Timber.d("lyl orderId=%s", resultStr);
        } catch (UnsupportedEncodingException e)
        {
            Timber.d("lyl UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e)
        {
            Timber.d("lyl ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e)
        {
            Timber.d("lyl IOException");
            e.printStackTrace();
        }

        return resultStr;
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
}
