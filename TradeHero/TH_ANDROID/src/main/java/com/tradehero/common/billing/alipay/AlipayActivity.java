package com.tradehero.common.billing.alipay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.Window;
import com.alipay.android.app.pay.PayTask;
import com.tradehero.common.billing.alipay.service.AlipayServiceWrapper;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alipay.OrderIdFormDTO;
import com.tradehero.th.api.alipay.OrderStatusDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.billing.StoreItemAdapter;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.net.URLEncoder;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
=======
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
>>>>>>> enable payment of alipay activity need check callback
import timber.log.Timber;

/**
 * Created by alex on 14-3-31.
 */
public class AlipayActivity extends Activity
{
<<<<<<< HEAD
    public static int PRODUCT_ID[][] = {{10, 11, 12}, {2, 1, 3}, {17, 18, 19}, {13}};
    public static String ALIPAY_TYPE_KEY = "alipay_type_key";
    public static String ALIPAY_POSITION_KEY = "alipay_position_key";
    public static String ALIPAY_ERROR_CODE_CANCELED_BY_USER = "6001";
    public static String ALIPAY_ERROR_CODE_SUCCESS = "9000";
    public final static String ALIPAY_ACTION_KEY = "com.alipay.mobilepay.android";
    public final static String ALIPAY_ORDER_INFO_KEY = "order_info";

    private int mType = 0;
    private int mPosition = 0;
    private String mOrderId = "";
    private String PRICE[][] =
            {{"0.01", "0.01", "0.01"}, {"0.01", "0.01", "0.01"}, {"0.01", "0.01", "0.01"},
                    {"0.01"}};
    //private String PRICE[][] =
    //        {{"6", "18", "30"}, {"12", "123", "238"}, {"12", "30", "68"}, {"12"}};
    private OwnedPortfolioId mPortfolioId;
    private MiddleCallback<String> getOrderIdMiddleCallback;
    private MiddleCallback<OrderStatusDTO> getOrderStatusMiddleCallback;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<AlipayServiceWrapper> alipayServiceWrapperLazy;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject protected PortfolioCache portfolioCache;
    @Inject protected UserProfileCache userProfileCache;

=======
>>>>>>> enable payment of alipay activity need check callback
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        mType = intent.getIntExtra(ALIPAY_TYPE_KEY, 0);
        mPosition = intent.getIntExtra(ALIPAY_POSITION_KEY, 0);
        Timber.d("lyl onCreate type=%d position=%d", mType, mPosition);
        getOrderIdFromServer();
=======
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
>>>>>>> enable payment of alipay activity need check callback
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
<<<<<<< HEAD
        mType = intent.getIntExtra(ALIPAY_TYPE_KEY, 0);
        mPosition = intent.getIntExtra(ALIPAY_POSITION_KEY, 0);
        Timber.d("lyl onNewIntent type=%d position=%d", mType, mPosition);
        getOrderIdFromServer();
    }

    private void beginAlipay()
    {
        PayTask.initialization(getApplicationContext(), Constants.ALIPAY_DEFAULT_PARTNER);
        String info = getOrderInfo();
        String sign = Rsa.sign(info, Constants.ALIPAY_PRIVATE);
=======
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
>>>>>>> enable payment of alipay activity need check callback
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        Timber.d("lyl info=%s", info);

<<<<<<< HEAD
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction(ALIPAY_ACTION_KEY);
        intent.putExtra(ALIPAY_ORDER_INFO_KEY, info);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int result, Intent data)
    {
        super.onActivityResult(requestCode, result, data);
        if (data != null)
        {
            String resultStatus = data.getStringExtra("resultStatus");
            //String action = data.getAction();
            //String memo = data.getStringExtra("memo");
            //String resultString = data.getStringExtra("result");
            //Toast.makeText(getApplicationContext(), "action = [" + action
            //        + "], resultStatus = " + resultStatus + ", memo = [" + memo
            //        + "], result = [" + resultString + "]", Toast.LENGTH_LONG).show();
            Timber.d("lyl onActivityResult resultStatus=%s", resultStatus);
            if (resultStatus.contains(ALIPAY_ERROR_CODE_CANCELED_BY_USER))
            {
                THToast.show(R.string.alipay_cancel);
            }
            else if (resultStatus.contains(ALIPAY_ERROR_CODE_SUCCESS))
            {
                checkWithServer();
            }
        }
    }

    @Override protected void onDestroy()
    {
        detachGetOrderIdMiddleCallback();
        super.onDestroy();
    }

    private void checkWithServer()
    {
        detachGetOrderStatusMiddleCallback();
        getOrderStatusMiddleCallback =
                alipayServiceWrapperLazy.get().checkWithServer(mOrderId,
                        new CheckWithServerCallback());
    }

    private String getOrderInfo()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("partner=\"");
        sb.append(Constants.ALIPAY_DEFAULT_PARTNER);
        sb.append("\"&out_trade_no=\"");
        sb.append("alipay-").append(mOrderId);
        sb.append("\"&subject=\"");
        setOrderInfoSubject(sb);
        sb.append("\"&body=\"");
        sb.append("body");
        sb.append("\"&total_fee=\"");
        //sb.append("0.01");
        sb.append(PRICE[mType - 1][mPosition]);
=======
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
>>>>>>> enable payment of alipay activity need check callback
        sb.append("\"&notify_url=\"");

        // 网址需要做URL编码
        sb.append(URLEncoder.encode("http://www.tradehero.mobi/api/alipay/notify"));
        sb.append("\"&service=\"mobile.securitypay.pay");
        sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&return_url=\"");
        sb.append(URLEncoder.encode("http://m.alipay.com"));
        sb.append("\"&payment_type=\"1");
        sb.append("\"&seller_id=\"");
<<<<<<< HEAD
        sb.append(Constants.ALIPAY_DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"3m");
=======
        sb.append(Constants.DEFAULT_SELLER);

        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"1m");
>>>>>>> enable payment of alipay activity need check callback
        sb.append("\"");

        return new String(sb);
    }

<<<<<<< HEAD
    private void setOrderInfoSubject(StringBuilder sb)
    {
        switch (mType)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                sb.append(getString(R.string.alipay_virtual_dollars_subject)).append("\n");
                sb.append(getResources().getStringArray(
                        R.array.alipay_virtual_dollars_array)[mPosition]);
                break;
            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                sb.append(getString(R.string.alipay_follow_credits_subject)).append("\n");
                sb.append(getResources().getStringArray(
                        R.array.alipay_follow_credits_array)[mPosition]);
                break;
            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                sb.append(getString(R.string.alipay_stock_alerts_subject)).append("\n");
                sb.append(getResources().getStringArray(
                        R.array.alipay_stock_alerts_array)[mPosition]);
                break;
            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                sb.append(getString(R.string.alipay_reset_portfolio_subject));
                break;
        }
    }

    private void getOrderIdFromServer()
    {
        OwnedPortfolioId portfolioId = portfolioCompactListCache.getDefaultPortfolio(
                currentUserId.toUserBaseKey());
        if (portfolioId != null)
        {
            Timber.d("lyl portfolioId=%s", portfolioId.portfolioId.toString());
            mPortfolioId = portfolioId;
        }

        OrderIdFormDTO orderIdFormDTO = new OrderIdFormDTO();
        orderIdFormDTO.productId = String.valueOf(PRODUCT_ID[mType - 1][mPosition]);
        orderIdFormDTO.portfolioId = portfolioId.portfolioId.toString();

        detachGetOrderIdMiddleCallback();
        getOrderIdMiddleCallback =
                alipayServiceWrapperLazy.get().getOrderId(orderIdFormDTO, new GetOrderIdCallback());
    }

    private void detachGetOrderIdMiddleCallback()
    {
        if (getOrderIdMiddleCallback != null)
        {
            getOrderIdMiddleCallback.setPrimaryCallback(null);
        }
        getOrderIdMiddleCallback = null;
    }

    private void detachGetOrderStatusMiddleCallback()
    {
        if (getOrderStatusMiddleCallback != null)
        {
            getOrderStatusMiddleCallback.setPrimaryCallback(null);
        }
        getOrderStatusMiddleCallback = null;
    }

    private class GetOrderIdCallback implements Callback<String>
    {
        @Override public void success(String orderId, Response response2)
        {
            Timber.d("lyl orderId=%s", orderId);
            mOrderId = orderId;
            if (!orderId.isEmpty())
            {
                beginAlipay();
            }
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            finish();
        }
    }

    private class CheckWithServerCallback implements Callback<OrderStatusDTO>
    {
        @Override public void success(OrderStatusDTO orderStatusDTO, Response response2)
        {
            Timber.d("lyl orderStatusDTO=%s", orderStatusDTO.status);
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
                    portfolioCompactListCache.invalidate(
                            currentUserId.toUserBaseKey());//update portfolio list
                    break;
            }
            if (orderStatusDTO.status.equals("2"))
            {
                THToast.show(R.string.alipay_success);
            }
            else
            {
                THToast.show(R.string.alipay_not_sure);
            }
            finish();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            finish();
        }
    }
}
=======
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
>>>>>>> enable payment of alipay activity need check callback
