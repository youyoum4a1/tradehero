package com.tradehero.ReactNative;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.facebook.react.LifecycleState;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.tradehero.th.BuildConfig;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class ReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    private ReactRootView mReactRootView;
    private static ReactInstanceManager mReactInstanceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReactRootView = new ReactRootView(this);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("debug_http_host", "fhmainstorage.blob.core.windows.net/fhres").apply();

        if (mReactInstanceManager == null) {
            mReactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(getApplication())
                    .setBundleAssetName("index.android.bundle")
                    .setJSMainModuleName("index.android")
                    .addPackage(new MainReactPackage())
                    .setUseDeveloperSupport(true)
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .build();
        }


        mReactRootView.startReactApplication(mReactInstanceManager, "AwesomeProject", null);

        setContentView(mReactRootView);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mReactInstanceManager != null) {
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onResume(this);
        }
    }
}
