package com.androidth.general.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.common.activities.ActivityResultRequester;
import com.androidth.general.common.utils.CollectionUtils;
import com.androidth.general.R;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.ActionBarOwnerMixin;
import com.androidth.general.utils.route.THRouter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import rx.functions.Action1;

abstract public class OneFragmentActivity extends BaseActivity
        implements AchievementAcceptor
{
    OneFragmentActivityModule activityModule;
    @Inject protected THRouter thRouter;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    @Bind(R.id.my_toolbar) protected Toolbar toolbar;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fragment);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        activityModule.toolbar = toolbar;
        activityModule.navigator = new DashboardNavigator(this, R.id.realtabcontent);

        if (savedInstanceState == null)
        {
            activityModule.navigator.pushFragment(
                    getInitialFragment(),
                    getInitialBundle(),
                    null,
                    getInitialFragment().getName(),
                    false);
        }
    }

    @NonNull abstract protected Class<? extends Fragment> getInitialFragment();

    @NonNull protected Bundle getInitialBundle()
    {
        Bundle args = new Bundle();
        ActionBarOwnerMixin.putKeyShowHome(args, false);
        return args;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.close:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull @Override protected List<Object> getModules()
    {
        List<Object> superModules = new ArrayList<>(super.getModules());
        activityModule = new OneFragmentActivityModule();
        superModules.add(activityModule);
        return superModules;
    }

    @Override public void onBackPressed()
    {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        int realCount = 0;
        if (fragments != null)
        {
            for (Fragment fragment : fragments)
            {
                if (fragment != null)
                {
                    realCount++;
                }
            }
        }
        if (realCount <= 1)
        {
            finish();
        }
        else
        {
            activityModule.navigator.popFragment();
        }
    }

    @Override protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        CollectionUtils.apply(activityResultRequesters, new Action1<ActivityResultRequester>()
        {
            @Override public void call(ActivityResultRequester requester)
            {
                requester.onActivityResult(OneFragmentActivity.this, requestCode, resultCode, data);
            }
        });
        RouteParams routeParams = getRouteParams(data);
        if (routeParams != null)
        {
            thRouter.open(routeParams.deepLink, routeParams.extras, this);
        }
        Fragment currentFragment = activityModule.navigator.getCurrentFragment();
        if (currentFragment != null)
        {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
