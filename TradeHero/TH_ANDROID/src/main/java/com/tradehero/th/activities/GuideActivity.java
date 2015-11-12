package com.tradehero.th.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.auth.DeviceAuthenticationProvider;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.DeviceCredentialsDTO;
import com.tradehero.th.persistence.prefs.DiviceID;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener,
        View.OnClickListener
{
    public static long TIMES = (long)1000000;
    public static long TIMES2 = (long)10000000;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject @DiviceID StringPreference mDeviceIDStringPreference;
    @InjectView(R.id.guide_screen_indicator0) ImageView mIndicator0;
    @InjectView(R.id.guide_screen_indicator1) ImageView mIndicator1;
    @InjectView(R.id.guide_screen_indicator2) ImageView mIndicator2;
    @InjectView(R.id.guide_screen_indicator3) ImageView mIndicator3;
    @InjectView(R.id.guide_screen_fast_login) Button mFastLogin;
    @InjectView(R.id.guide_screen_login) Button mLogin;
    private ProgressDialog mProgressDialog;

    private ViewPager viewpager;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);
        ButterKnife.inject(this);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.guide_screen1);
        list.add(R.drawable.guide_screen2);
        list.add(R.drawable.guide_screen3);
        list.add(R.drawable.guide_screen4);
        mIndicator0.setBackgroundResource(R.drawable.guide_screen_indicator_on);

        viewpager.setAdapter(new ListViewPagerAdapter(list));
        viewpager.setOnPageChangeListener(this);

        try
        {
            if (isInstallShortcut())
            {
                removeShortcut();
            }
            createShortcut();
        }
        catch (SecurityException e)
        {
            Timber.e(e, null);
        }
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    private void createShortcut()
    {
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        intent.putExtra("duplicate", false);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.launcher);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, createLaunchIntent());
        sendBroadcast(intent);
    }

    private Intent createLaunchIntent()
    {
        Intent launchIntent = new Intent(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        launchIntent.setComponent(new ComponentName(getPackageName(), SplashActivity.class.getName()));
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return launchIntent;
    }

    private void printShortcutName(Cursor c)
    {
        if (!BuildConfig.DEBUG)
        {
            return;
        }

        if (c == null || c.getCount() <= 0)
        {
            return;
        }

        String name = "title";
        c.moveToFirst();
        int index = c.getColumnIndex(name);
        if (index == -1)
        {
            return;
        }
        c.moveToPrevious();
        while (c.moveToNext())
        {
            String title = c.getString(index);
            Timber.d("Shortcut title:%s",title);
        }
    }

    private boolean isInstallShortcut()
    {
        String name = getString(R.string.app_name);
        boolean  isInstallShortcut = false;
        final ContentResolver cr = getContentResolver();
        String AUTHORITY = "com.android.launcher.settings";
        Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");

        Cursor c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
                "title=?", new String[]{name}, null);

        if (c != null && c.getCount() > 0)
        {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        if (c != null)
        {
            c.close();
        }

        if (isInstallShortcut)
        {
            return isInstallShortcut;
        }

        AUTHORITY = "com.android.launcher2.settings";
        CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        c = cr.query(CONTENT_URI, new String[] { "title", "iconResource" },
                "title=?", new String[]{name}, null);

        if (c != null && c.getCount() > 0) {
            isInstallShortcut = true;
        }
        printShortcutName(c);

        return isInstallShortcut;
    }

    private void removeShortcut()
    {

        Intent shortcutIntent = new Intent(getApplicationContext(), SplashActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }

    @OnClick({R.id.guide_screen_fast_login, R.id.guide_screen_login})
    @Override public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.guide_screen_fast_login:
                if(viewpager!=null){
                    analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN_ANONYMOUS, String.valueOf(viewpager.getCurrentItem())));
                }
                authenticateWithDevice();
                break;
            case R.id.guide_screen_login:
                if(viewpager!=null){
                    analytics.addEvent(new MethodEvent(AnalyticsConstants.SIGN_IN_ACCOUNT, String.valueOf(viewpager.getCurrentItem())));
                }
                ActivityHelper.presentFromActivity(this, AuthenticationActivity.class);
                finish();
                break;
        }
    }

    class ListViewPagerAdapter extends PagerAdapter
    {
        private List<Integer> drawableIdList = null;

        public ListViewPagerAdapter(List<Integer> drawableIdList)
        {
            this.drawableIdList = drawableIdList;
        }

        @Override public int getCount()
        {
            return drawableIdList.size();
        }

        private boolean isLast(int position)
        {
            return position == getCount() - 1;
        }

        private boolean isClickable(int position)
        {
            return isLast(position);
        }

        @Override public Object instantiateItem(ViewGroup container, int position)
        {
            View view;
            ImageView imageView = (ImageView) LayoutInflater.from(GuideActivity.this).inflate(R.layout.guide_layout, null);
            view = imageView;
            try
            {
                imageView.setBackgroundResource(drawableIdList.get(position));
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "Expanding position %d", position);
            }
            if (isClickable(position))
            {
                imageView.setOnClickListener(GuideActivity.this);
            }
            else
            {
                imageView.setOnClickListener(null);
            }
            container.addView(view);
            //analytics.addEvent(new MethodEvent(AnalyticsConstants.SplashScreen, AnalyticsConstants.Screen + String.valueOf(position)));
            return view;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
            if (object instanceof ImageView)
            {
                ((ImageView) object).setBackgroundResource(0);
            }
        }

        @Override public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }
    }

    @Override public void onPageScrolled(int i, float v, int i2)
    {
    }

    @Override public void onPageSelected(int i)
    {
        switch (i)
        {
            case 0:
                if (mIndicator3 != null)
                {
                    mIndicator3.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator1.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator0.setBackgroundResource(R.drawable.guide_screen_indicator_on);
                }
                break;
            case 1:
                if (mIndicator0 != null)
                {
                    mIndicator0.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator2.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator1.setBackgroundResource(R.drawable.guide_screen_indicator_on);
                }
                break;
            case 2:
                if (mIndicator1 != null)
                {
                    mIndicator1.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator3.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator2.setBackgroundResource(R.drawable.guide_screen_indicator_on);
                }
                break;
            case 3:
                if (mIndicator2 != null)
                {
                    mIndicator2.setBackgroundResource(R.drawable.guide_screen_indicator_off);
                    mIndicator3.setBackgroundResource(R.drawable.guide_screen_indicator_on);
                }
                break;
        }
    }

    @Override public void onPageScrollStateChanged(int i)
    {
    }

    @Override public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private void authenticateWithDevice()
    {
        mProgressDialog = progressDialogUtil.show(this, getString(R.string.faster_login), getString(R.string.guest_user_id, getIMEI()));
        JSONCredentials createdJson = new JSONCredentials(getUserFromMap());
        DeviceAuthenticationProvider.setCredentials(createdJson);
        THUser.setAuthenticationMode(AuthenticationMode.Device);
        THUser.logInWithAsync(DeviceCredentialsDTO.DEVICE_AUTH_TYPE,
                createCallbackForEmailSign(AuthenticationMode.Device));
    }

    private Map<String, Object> getUserFromMap()
    {
        Map<String, Object> map = new HashMap<>();
        map.put(DeviceAuthenticationProvider.KEY_ACCESS_TOKEN, getIMEI());
        return map;
    }

    public String getIMEI()
    {
        String imei = mDeviceIDStringPreference.get();
        if (imei.isEmpty())
        {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String strIMEI = tm.getDeviceId();
            if (strIMEI == null || strIMEI.isEmpty() || strIMEI.contains("000000000000000"))
            {
                strIMEI = String.valueOf((int)Math.floor((Math.random() + 1) * TIMES));
                strIMEI = strIMEI+String.valueOf((int)Math.floor((Math.random() + 1) * TIMES2));
                mDeviceIDStringPreference.set(strIMEI);
            }
            else
            {
                mDeviceIDStringPreference.set(strIMEI);
            }
            return strIMEI;
        }
        return imei;
    }

    private LogInCallback createCallbackForEmailSign(final AuthenticationMode authenticationMode)
    {
        final boolean isSigningUp = authenticationMode == AuthenticationMode.SignUp;
        return new SocialAuthenticationCallback(AnalyticsConstants.LOGIN_USER_ACCOUNT)
        {
            private final boolean signingUp = isSigningUp;

            @Override public boolean isSigningUp()
            {
                return signingUp;
            }

            @Override public boolean onSocialAuthDone(JSONCredentials json)
            {
                return true;
            }
        };
    }

    private class SocialAuthenticationCallback extends LogInCallback
    {
        private final String providerName;

        public SocialAuthenticationCallback(String providerName)
        {
            this.providerName = providerName;
        }

        @Override public void done(UserLoginDTO user, THException ex)
        {
            mProgressDialog.dismiss();
            Throwable cause;
            Response response;
            if (user != null)
            {
                //analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Success, providerName));
                launchDashboard(user);
            }
            else if ((cause = ex.getCause()) != null && cause instanceof RetrofitError &&
                    (response = ((RetrofitError) cause).getResponse()) != null && response.getStatus() == 403) // Forbidden
            {
                THToast.show(R.string.authentication_not_registered);
            }
            else
            {
                THToast.show(ex);
            }
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            return true;
        }

        @Override public void onStart()
        {
            //progressDialog.setMessage(getString(R.string.authentication_connecting_tradehero_only));
        }

        public boolean isSigningUp()
        {
            return false;
        }
    }

    private void launchDashboard(UserLoginDTO userLoginDTO)
    {
        THSharePreferenceManager.clearDialogShowedRecord();

        int userId = userLoginDTO.profileDTO.id;
        if(userId <=0 ||THSharePreferenceManager.isRecommendedStock(userId, this)){
            Intent intent = new Intent(this, TradeHeroMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(UserLoginDTO.SUGGEST_UPGRADE, userLoginDTO.suggestUpgrade);
            intent.putExtra(UserLoginDTO.SUGGEST_LI_REAUTH, userLoginDTO.suggestLiReauth);
            intent.putExtra(UserLoginDTO.SUGGEST_TW_REAUTH, userLoginDTO.suggestTwReauth);
            intent.putExtra(UserLoginDTO.SUGGEST_FB_REAUTH, userLoginDTO.suggestFbReauth);
            startActivity(intent);
            overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        }else{
            Intent intent = new Intent(this, RecommendStocksActivity.class);
            intent.putExtra(RecommendStocksActivity.LOGIN_USER_ID, userId);
            startActivity(intent);
        }
        finish();
    }
}
