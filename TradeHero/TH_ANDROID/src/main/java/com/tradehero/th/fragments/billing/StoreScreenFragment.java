package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.alipay.android.app.net.SSLSocketFactoryEx;
import com.alipay.android.app.sdk.AliPay;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.alipay.Result;
import com.tradehero.common.billing.alipay.Rsa;
import com.tradehero.common.billing.googleplay.exception.IABBillingUnavailableException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.LocalyticsConstants;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyStore;
import javax.inject.Inject;
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

public class StoreScreenFragment extends BasePurchaseManagerFragment
        implements WithTutorial
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserId currentUserId;
    @Inject THIABAlertDialogUtil THIABAlertDialogUtil;
    @Inject LocalyticsSession localyticsSession;

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity(), getActivity().getLayoutInflater());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new StoreScreenTHIABUserInteractor();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.store_option_menu_title); // Add the changing cute icon
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Store);

        storeItemAdapter.notifyDataSetChanged();
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
        }
        listView = null;
        storeItemAdapter = null;
        super.onDestroyView();
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }

    private void handlePositionClicked(int position)
    {
        Bundle bundle;
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                //userInteractor.conditionalPopBuyVirtualDollars();
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        beginAlipay(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
                    }
                }.start();
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                userInteractor.conditionalPopBuyFollowCredits();
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                userInteractor.conditionalPopBuyStockAlerts();
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                userInteractor.conditionalPopBuyResetPortfolio();
                break;

            case StoreItemAdapter.POSITION_MANAGE_HEROES:
                pushHeroFragment();
                break;

            case StoreItemAdapter.POSITION_MANAGE_FOLLOWERS:
                pushFollowerFragment();
                break;
            case StoreItemAdapter.POSITION_MANAGE_STOCK_ALERTS:
                pushStockAlertFragment();
                break;
            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

    private void beginAlipay(int type)
    {
        Timber.d("lyl beginAlipay");
        String info = getNewOrderInfo();
        String sign = Rsa.sign(info, Constants.PRIVATE);
        sign = URLEncoder.encode(sign);
        info += "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
        Timber.d("lyl info=%s", info);

        AliPay alipay = new AliPay(getSherlockActivity(), mHandler);
        String result = alipay.pay(info);
        Timber.d("lyl result=%s", result);

        Message msg = new Message();
        msg.what = 1;//RQF_PAY;
        msg.obj = result;
        mHandler.sendMessage(msg);
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
        }

        ;
    };

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

    private void pushStockAlertFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(AlertManagerFragment.BUNDLE_KEY_USER_ID, currentUserId.get());
        pushFragment(AlertManagerFragment.class, bundle);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(HeroManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                    applicablePortfolio.getArgs());
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FollowerManagerFragment.BUNDLE_KEY_HERO_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(
                    FollowerManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                    applicablePortfolio.getArgs());
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(fragmentClass);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator()
                .pushFragment(fragmentClass, bundle);
    }

    private void popPleaseWait()
    {
        THIABAlertDialogUtil.popWithNegativeButton(getActivity(),
                R.string.error_incomplete_info_title,
                R.string.error_incomplete_info_message,
                R.string.error_incomplete_info_cancel);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }

    public class StoreScreenTHIABUserInteractor extends THIABUserInteractor
    {
        public StoreScreenTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
        {
            // TODO warn if there are things unset
            if (throwable instanceof IABBillingUnavailableException
                    && !alreadyNotifiedNeedCreateAccount)
            {
                alreadyNotifiedNeedCreateAccount = true;
                popBillingUnavailable();
            }
            // Nothing to do presumably
        }
    }
}
