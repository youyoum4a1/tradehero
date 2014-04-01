package com.tradehero.common.billing.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;
import com.alipay.android.app.pay.PayTask;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import timber.log.Timber;

/**
 * Created by alex on 14-3-31.
 */
public class AlipayActivity extends Activity
{
    public static String ALIPAY_TYPE_KEY = "alipay_type_key";
    public static String ALIPAY_POSITION_KEY = "alipay_position_key";
    private int mType = 0;
    private int mPosition = 0;
    private String PRICE[][] = {{"0.01", "0.02", "0.03"}, {"0.01", "0.02", "0.03"}, {"0.01", "0.02", "0.03"}, {"0.01"}};
    //private int PRICE[][] = {{6, 18, 30}, {12, 123, 238}, {12, 30, 68}, {12}};
    public static int ORDER_ID[][] = {{10, 11, 12}, {2, 1, 3}, {17, 18, 19}, {13}};

    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        mType = intent.getIntExtra(ALIPAY_TYPE_KEY, 0);
        mPosition = intent.getIntExtra(ALIPAY_POSITION_KEY, 0);
        Timber.d("lyl onCreate type=%d position=%d", mType, mPosition);
        new Thread()
        {
            @Override
            public void run()
            {
                beginAlipay();
            }
        }.start();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        mType = intent.getIntExtra(ALIPAY_TYPE_KEY, 0);
        mPosition = intent.getIntExtra(ALIPAY_POSITION_KEY, 0);
        Timber.d("lyl onNewIntent type=%d position=%d", mType, mPosition);
        new Thread()
        {
            @Override
            public void run()
            {
                beginAlipay();
            }
        }.start();
    }

    //private void beginAlipay(int type)
    //{
    //    Timber.d("lyl beginAlipay");
    //    String info = getNewOrderInfo();
    //    String sign = Rsa.sign(info, Constants.ALIPAY_PRIVATE);
    //    sign = URLEncoder.encode(sign);
    //    info += "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
    //    Timber.d("lyl info=%s", info);

    //AliPay alipay = new AliPay(this, mHandler);
    //String result = alipay.pay(info);
    //Timber.d("lyl result=%s", result);

    //Message msg = new Message();
    //msg.what = 1;//RQF_PAY;
    //msg.obj = result;
    //mHandler.sendMessage(msg);
    //}

    private void beginAlipay()
    {
        Timber.d("lyl beginAlipay");
        PayTask.initialization(getApplicationContext(), Constants.ALIPAY_DEFAULT_PARTNER);
        String info = getNewOrderInfo();
        String sign = Rsa.sign(info, Constants.ALIPAY_PRIVATE);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        Timber.d("lyl info=%s", info);

        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction("com.alipay.mobilepay.android");
        intent.putExtra("order_info", info);
        startActivityForResult(intent, 0);

        //PayTask payTask = new PayTask(this, new PayTask.OnPayListener() {
        //    @Override
        //    public void onPaySuccess(Context context, String resultStatus,
        //            String memo, String result) {
        //        Timber.d("lyl :)");
        //    }
        //
        //    @Override
        //    public void onPayFailed(Context context, String resultStatus,
        //            String memo, String result) {
        //        Timber.d("lyl :(");
        //    }
        //});
        //payTask.initialization(getApplicationContext(), Constants.ALIPAY_DEFAULT_PARTNER);
        //payTask.execute(info);
    }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent data)
    {
        super.onActivityResult(requestCode, result, data);

        Timber.d("lyl onActivityResult requestCode=%d result=%d", requestCode, result);

        if (data != null)
        {
            String action = data.getAction();
            String resultStatus = data.getStringExtra("resultStatus");
            String memo = data.getStringExtra("memo");
            String resultString = data.getStringExtra("result");
            Toast.makeText(getApplicationContext(), "action = [" + action
                    + "], resultStatus = " + resultStatus + ", memo = [" + memo
                    + "], result = [" + resultString + "]", Toast.LENGTH_LONG).show();
            Timber.d("lyl onActivityResult resultStatus=%s memo=%s resultString=%s"
                    , resultStatus, memo, resultString);
            finish();
        }
    }

    private String getNewOrderInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Constants.ALIPAY_DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append("alipay-" + getOrderId());
        sb.append("\"&subject=\"");
        switch (mType)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                sb.append(getString(R.string.alipay_virtual_dollars_subject));
                break;
            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                sb.append(getString(R.string.alipay_follow_credits_subject));
                break;
            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                sb.append(getString(R.string.alipay_stock_alerts_subject));
                break;
            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                sb.append(getString(R.string.alipay_reset_portfolio_subject));
                break;
        }
        sb.append("\"&body=\"");
        sb.append("body");
        sb.append("\"&total_fee=\"");
        //sb.append("0.01");
        sb.append(PRICE[mType-1][mPosition]);
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode("http://www.tradehero.mobi/api/alipay/notify"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
        sb.append(Constants.ALIPAY_DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
        sb.append("\"");

        return new String(sb);
    }

    private String getOrderId()
    {

        OwnedPortfolioId portfolioId = portfolioCompactListCache.getDefaultPortfolio(
                currentUserId.toUserBaseKey());
        Timber.d("lyl portfolioId=%s", portfolioId.portfolioId.toString());
        String url = "https://www.tradehero.mobi/api/alipay/createOrder/" + String.valueOf(ORDER_ID[mType-1][mPosition]);

        String resultStr = "";
        HttpClient httpclient = new DefaultHttpClient();

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

}
