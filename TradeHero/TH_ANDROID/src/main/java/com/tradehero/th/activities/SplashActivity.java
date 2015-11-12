package com.tradehero.th.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.crashlytics.android.Crashlytics;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tapstream.sdk.Event;
import com.tendcloud.tenddata.TCAgent;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.models.user.auth.CredentialsDTO;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.RequestHeaders;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.persistence.prefs.ShareDialogKey;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.th.utils.metrics.MetricsModule;
import com.tradehero.th.utils.metrics.tapstream.TapStreamType;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import retrofit.RetrofitError;

public class SplashActivity extends AppCompatActivity {
    @Inject
    Lazy<Api> tapStream;
    @Inject
    MobileAppTracker mobileAppTracker;
    @Inject
    CurrentActivityHolder currentActivityHolder;
    @Inject
    @ShareDialogKey
    BooleanPreference mShareDialogKeyPreference;
    @InjectView(R.id.tips)
    TextView mTipsText;

    private static final String TAG_TASK_FRAGMENT = "auth.task.fragment";
    TaskFragment taskFragment;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppTiming.splashCreate = System.currentTimeMillis();
        super.onCreate(savedInstanceState);

        if (Constants.RELEASE) {
            Crashlytics.start(this);
        }
        setContentView(R.layout.splash_screen);

        TextView appVersion = (TextView) findViewById(R.id.app_version);
        if (appVersion != null) {
            appVersion.setText(VersionUtils.getAppVersion(this));
        }

        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        currentActivityHolder.setCurrentActivity(this);
        mShareDialogKeyPreference.set(true);
        if (mTipsText != null) {
            String[] sa = getResources().getStringArray(R.array.loading_page_tips);
            mTipsText.setText(sa[(int) Math.floor(Math.random() * 10)]);
        }

        FragmentManager fm = getSupportFragmentManager();
        taskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (taskFragment == null) {
            taskFragment = new TaskFragment();
            fm.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT).commit();
        }

        //check upgrade from baidu store only for baidu store package
        checkUpdate();
    }

    private void checkUpdate() {
        if (Constants.TAP_STREAM_TYPE == TapStreamType.Baidu
                || Constants.TAP_STREAM_TYPE == TapStreamType.NineOne
                || Constants.TAP_STREAM_TYPE == TapStreamType.AZouSC) {
            dialog = new ProgressDialog(this);
            dialog.setIndeterminate(true);
            dialog.setMessage(getString(R.string.checking_upgrade));
            dialog.show();
            BDAutoUpdateSDK.uiUpdateAction(this, new MyUICheckUpdateCallback());
        }
    }

    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {

        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        tapStream.get().fireEvent(new Event(getString(Constants.TAP_STREAM_TYPE.openResId), false));

        mobileAppTracker.setReferralSources(this);
        mobileAppTracker.measureSession();

        TCAgent.init(getApplicationContext(), MetricsModule.TD_APP_ID_KEY, Constants.TAP_STREAM_TYPE.name());

        if (!Constants.RELEASE) {
            VersionUtils.logScreenMeasurements(this);
        }
    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onDestroy();
    }

    public static class TaskFragment extends BaseFragment {
        @Inject
        @FirstLaunch
        BooleanPreference firstLaunchPreference;
        @Inject
        SessionServiceWrapper sessionServiceWrapper;
        @Inject
        MainCredentialsPreference mainCredentialsPreference;
        @Inject
        RequestHeaders requestHeaders;
        @Inject
        Provider<LoginFormDTO> loginFormDTOProvider;
        private int userId = -1;

        private static final int STATUS_FIRST_LAUNCH = 1;
        private static final int STATUS_AUTH_OK = 2;
        private static final int STATUS_AUTH_NO = 3;

        AsyncTask<Void, Void, Integer> task;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            task = new AuthAsyncTask();
            task.execute();
        }

        void handleTaskResult(int result) {
            if (getActivity() == null) {
                return;
            }
            switch (result) {
                case STATUS_FIRST_LAUNCH:
                    ActivityHelper.presentFromActivity(getActivity(), GuideActivity.class);
                    firstLaunchPreference.set(false);
                    break;
                case STATUS_AUTH_OK:
                    if (userId <= 0 || THSharePreferenceManager.isRecommendedStock(userId, getActivity())) {
                        THSharePreferenceManager.clearDialogShowedRecord();
                        ActivityHelper.presentFromActivity(getActivity(), TradeHeroMainActivity.class, Intent.FLAG_ACTIVITY_CLEAR_TOP, null);
                    } else {
                        Bundle args = new Bundle();
                        args.putInt(RecommendStocksActivity.LOGIN_USER_ID, userId);
                        ActivityHelper.presentFromActivity(getActivity(), RecommendStocksActivity.class, 0, args);
                    }
                    break;
                case STATUS_AUTH_NO:
                    ActivityHelper.presentFromActivity(getActivity(), AuthenticationActivity.class);
                    break;
            }
            getActivity().finish();
        }

        private class AuthAsyncTask extends AsyncTask<Void, Void, Integer> {

            @Override
            protected Integer doInBackground(Void... params) {
                if (firstLaunchPreference.get()) {
                    return STATUS_FIRST_LAUNCH;
                }
                CredentialsDTO credentialsDTO = mainCredentialsPreference.getCredentials();
                if (credentialsDTO != null) {
                    try {
                        UserLoginDTO userLoginDTO = sessionServiceWrapper.login(
                                requestHeaders.createTypedAuthParameters(credentialsDTO),
                                loginFormDTOProvider.get());
                        if (userLoginDTO != null && userLoginDTO.profileDTO != null) {
                            userId = userLoginDTO.profileDTO.id;
                            return STATUS_AUTH_OK;
                        }
                    } catch (RetrofitError retrofitError) {
                        return STATUS_AUTH_NO;
                    }
                }
                return STATUS_AUTH_NO;
            }

            @Override
            protected void onPostExecute(Integer result) {
                handleTaskResult(result);
            }
        }
    }
}
