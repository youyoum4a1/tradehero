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
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import timber.log.Timber;

/**
 * Created by alex on 14-3-31.
 */
public class AlipayActivity extends Activity
{
    public static String ALIPAY_TYPE_KEY = "alipay_type_key";
    public static String ALIPAY_POSITION_KEY = "alipay_position_key";
    public static String ALIPAY_PORTFOLIO_ID_KEY = "portfolioId";
    public static String ALIPAY_PRODUCT_ID_KEY = "productId";
    public static String ALIPAY_ERROR_CODE_CANCELED_BY_USER = "6001";
    public static String ALIPAY_ERROR_CODE_SUCCESS = "9000";
    private int mType = 0;
    private int mPosition = 0;
    private String mOrderId = "";
    private OwnedPortfolioId mPortfolioId;
    private String PRICE[][] =
            {{"0.01", "0.01", "0.01"}, {"0.01", "0.01", "0.01"}, {"0.01", "0.01", "0.01"},
                    {"0.01"}};
    //private int PRICE[][] = {{6, 18, 30}, {12, 123, 238}, {12, 30, 68}, {12}};
    public static int PRODUCT_ID[][] = {{10, 11, 12}, {2, 1, 3}, {17, 18, 19}, {13}};

    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected PortfolioCompactCache portfolioCompactCache;
    @Inject protected PortfolioCache portfolioCache;
    @Inject protected UserProfileCache userProfileCache;

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

    private void beginAlipay()
    {
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
    }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent data)
    {
        super.onActivityResult(requestCode, result, data);
        if (data != null)
        {
            //String action = data.getAction();
            String resultStatus = data.getStringExtra("resultStatus");
            //String memo = data.getStringExtra("memo");
            //String resultString = data.getStringExtra("result");
            //Toast.makeText(getApplicationContext(), "action = [" + action
            //        + "], resultStatus = " + resultStatus + ", memo = [" + memo
            //        + "], result = [" + resultString + "]", Toast.LENGTH_LONG).show();
            Timber.d("lyl onActivityResult resultStatus=%s", resultStatus);
            if (resultStatus.contains(ALIPAY_ERROR_CODE_CANCELED_BY_USER))
            {
                Toast.makeText(getApplicationContext(), R.string.alipay_cancel, Toast.LENGTH_LONG)
                        .show();
            }
            else if (resultStatus.contains(ALIPAY_ERROR_CODE_SUCCESS))
            {
                Toast.makeText(getApplicationContext(), R.string.alipay_success, Toast.LENGTH_LONG)
                        .show();
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        checkWithServer();
                    }
                }.start();
            }
        }
    }

    private void checkWithServer()
    {
        String url = "https://www.tradehero.mobi/api/alipay/order/" + mOrderId;
        String resultStr = "";
        HttpClient httpClient = new DefaultHttpClient();

        HttpGet httpGet = new HttpGet(url);
        try
        {
            httpGet.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
            HttpResponse response;
            response = httpClient.execute(httpGet);
            resultStr = EntityUtils.toString(response.getEntity());
            Timber.d("lyl resultStr=%s", resultStr);

            //portfolioCompactCache.invalidate(mPortfolioId);
            switch (mType)
            {
                case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                    portfolioCache.invalidate(mPortfolioId);//update portfolioId's detail
                    break;
                case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                    userProfileCache.invalidate(currentUserId.toUserBaseKey());
                    break;
                case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                    userProfileCache.invalidate(currentUserId.toUserBaseKey());
                    break;
                case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                    portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());//update portfolio list
                    break;
            }
            if (resultStr.contains("\"status\"=2"))
            {
            }
            finish();
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
        sb.append(PRICE[mType - 1][mPosition]);
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
        if (portfolioId != null)
        {
            Timber.d("lyl portfolioId=%s", portfolioId.portfolioId.toString());
            mPortfolioId = portfolioId;
        }
        String url = "https://www.tradehero.mobi/api/alipay/createOrder";

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(ALIPAY_PRODUCT_ID_KEY,
                String.valueOf(PRODUCT_ID[mType - 1][mPosition])));
        switch (mType)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                nameValuePairs.add(new BasicNameValuePair(ALIPAY_PORTFOLIO_ID_KEY,
                        portfolioId.portfolioId.toString()));
                break;
        }

        String resultStr = "";
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(url);
        try
        {
            httpPost.addHeader(Constants.AUTHORIZATION, THUser.getAuthHeader());
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response;
            response = httpClient.execute(httpPost);
            resultStr = EntityUtils.toString(response.getEntity());
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

        mOrderId = resultStr;
        return resultStr;
    }
}