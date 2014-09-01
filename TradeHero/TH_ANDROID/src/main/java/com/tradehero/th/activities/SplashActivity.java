package com.tradehero.th.activities;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.actionbarsherlock.app.SherlockActivity;
import com.crashlytics.android.Crashlytics;
import com.localytics.android.LocalyticsSession;
import com.mobileapptracker.MobileAppTracker;
import com.tapstream.sdk.Api;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.LoginFormDTO;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.RequestHeaders;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.prefs.FirstLaunch;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.VersionUtils;
import com.tradehero.thm.R;
import dagger.Lazy;
import java.util.Collections;
import java.util.Timer;
import javax.inject.Inject;
import javax.inject.Provider;

public class SplashActivity extends SherlockActivity {
  private Timer timerToShiftActivity;
  private AsyncTask<Void, Void, Void> initialAsyncTask;
  @Inject SessionServiceWrapper sessionServiceWrapper;
  @Inject RequestHeaders requestHeaders;
  @Inject Provider<LoginFormDTO> loginFormDTOProvider;
  @Inject @FacebookAppId String facebookAppId;
  @Inject @FirstLaunch BooleanPreference firstLaunchPreference;

  @Inject MainCredentialsPreference mainCredentialsPreference;
  @Inject Lazy<LocalyticsSession> localyticsSession;
  @Inject Lazy<Api> tapStream;
  @Inject Lazy<MobileAppTracker> mobileAppTrackerLazy;
  @Inject CurrentActivityHolder currentActivityHolder;
  @Inject DTOCacheUtil dtoCacheUtil;
  @Inject SystemStatusCache systemStatusCache;
  @Inject CurrentUserId currentUserId;
  @Inject AlertDialogUtil alertDialogUtil;

  @Override protected void onCreate(Bundle savedInstanceState) {
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
    currentActivityHolder.setCurrentActivity(this);
    dtoCacheUtil.anonymousPrefetches();
  }

  @Override protected void onResume() {
    super.onResume();
    try {
      ApplicationInfo info = getPackageManager().
          getApplicationInfo("com.tradehero.th", 0);
      new ForceRemoveRunnable().run();
    } catch (PackageManager.NameNotFoundException e) {
      new ForceUpgradeRunnable().run();
    }
  }

  @Override protected void onPause() {
    localyticsSession.get().close(Collections.singletonList(Constants.TAP_STREAM_TYPE.name()));
    localyticsSession.get().upload();

    super.onPause();
  }

  @Override protected void onDestroy() {
    initialAsyncTask = null;
    super.onDestroy();
  }

  private class ForceUpgradeRunnable implements Runnable {
    @Override public void run() {
      alertDialogUtil.popWithOkCancelButton(SplashActivity.this,
          R.string.restore_original_app_title, R.string.restore_original_app_description,
          R.string.update_now, R.string.exit_app, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              try {
                THToast.show(R.string.update_guide);
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.tradehero.th")));
              } catch (ActivityNotFoundException ex) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google" + ".com/store/apps/details?id=com.tradehero.th")));
              }
            }
          }, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              Intent intent = new Intent(Intent.ACTION_MAIN);
              intent.addCategory(Intent.CATEGORY_HOME);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              SplashActivity.this.startActivity(intent);
            }
          });
    }
  }

  private class ForceRemoveRunnable implements Runnable {
    @Override public void run() {
      alertDialogUtil.popWithOkCancelButton(SplashActivity.this,
          R.string.uninstall_me, R.string.remove_me_description,
          R.string.uninstall_me, R.string.exit_app, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              Toast.makeText(SplashActivity.this, R.string.remove_me_message,
                  Toast.LENGTH_LONG).show();
              try {
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS));
              } catch (ActivityNotFoundException ex) {
                finish();
              }
            }
          }, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
              finish();
            }
          });
    }
  }
}
