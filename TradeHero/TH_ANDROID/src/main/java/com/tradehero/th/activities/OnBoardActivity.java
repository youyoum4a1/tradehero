package com.tradehero.th.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.ButterKnife;
import com.tradehero.common.activities.ActivityResultRequester;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.ActionBarOwnerMixin;
import com.tradehero.th.fragments.base.BaseFragmentOuterElements;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.onboarding.OnBoardFragment;
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

public class OnBoardActivity extends BaseActivity
{
    @Inject protected THRouter thRouter;
    @Inject Set<ActivityResultRequester> activityResultRequesters;
    protected DashboardNavigator navigator;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board);
        ButterKnife.inject(this);


        navigator = new DashboardNavigator(this, R.id.realtabcontent);

        if (savedInstanceState == null)
        {
            navigator.pushFragment(
                    OnBoardFragment.class,
                    getInitialBundle(),
                    null,
                    OnBoardFragment.class.getName(),
                    false);
        }
    }

    @NonNull protected Bundle getInitialBundle()
    {
        Bundle args = new Bundle();
        ActionBarOwnerMixin.putKeyShowHome(args, false);
        return args;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.on_board_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        superModules.add(new OnBoardActivityModule());
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
                requester.onActivityResult(requestCode, resultCode, data);
            }
        });
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
    public class OnBoardActivityModule
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
