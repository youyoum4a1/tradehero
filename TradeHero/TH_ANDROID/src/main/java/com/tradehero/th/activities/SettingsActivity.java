package com.tradehero.th.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.fragments.settings.SettingsFragment;
import java.util.Set;
import javax.inject.Inject;
import rx.functions.Action1;
import timber.log.Timber;

public class SettingsActivity extends OneFragmentActivity
{
    @Inject @SocialAuth Set<ActivityResultRequester> activityResultRequesters;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.registerRoutes(SettingsFragment.class);
    }

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return SettingsFragment.class;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult %d, %d, %s", requestCode, resultCode, data);
        CollectionUtils.apply(activityResultRequesters, new Action1<ActivityResultRequester>()
        {
            @Override public void call(ActivityResultRequester requester)
            {
                requester.onActivityResult(requestCode, resultCode, data);
            }
        });
    }
}
