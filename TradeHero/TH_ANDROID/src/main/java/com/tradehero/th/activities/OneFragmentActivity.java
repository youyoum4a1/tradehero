package com.tradehero.th.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.BaseFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.functions.Action1;

abstract public class OneFragmentActivity extends BaseActivity
        implements AchievementAcceptor
{
    @Inject protected THRouter thRouter;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    protected DashboardNavigator navigator;
    @InjectView(R.id.my_toolbar) protected Toolbar toolbar;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_fragment);
        ButterKnife.inject(this);

        setSupportActionBar(toolbar);

        navigator = new DashboardNavigator(this, R.id.realtabcontent);

        if (savedInstanceState == null)
        {
            navigator.pushFragment(
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
        superModules.add(new OneFragmentActivityModule());
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
            navigator.popFragment();
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
    }

    @Module(
            addsTo = AppModule.class,
            includes = {
                    UIModule.class
            },
            library = true,
            complete = false,
            overrides = true
    )
    public class OneFragmentActivityModule
    {
        @Provides DashboardNavigator provideDashboardNavigator()
        {
            return navigator;
        }

        @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
        {
            return new THRouter(context, navigatorProvider);
        }

        @Provides FragmentOuterElements provideFragmentElements()
        {
            return new BaseFragmentOuterElements();
        }
    }
}
