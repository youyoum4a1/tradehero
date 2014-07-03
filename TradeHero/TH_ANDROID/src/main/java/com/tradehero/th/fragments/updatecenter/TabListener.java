package com.tradehero.th.fragments.updatecenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import timber.log.Timber;

// TODO remove?
public class TabListener implements ActionBar.TabListener
{
    protected Activity mActivity;
    protected Fragment mFragment;
    protected final String mTag;
    protected final Class<? extends Fragment> mFragmentClass;
    protected final Bundle mArgs;

    public TabListener(SherlockFragmentActivity activity, Class<? extends Fragment> fragmentClass,
            String tag, Bundle args)
    {
        this.mActivity = activity;
        this.mFragmentClass = fragmentClass;
        this.mTag = tag;
        this.mArgs = args;

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        mFragment = activity.getSupportFragmentManager().findFragmentByTag(mTag);
        Timber.d("TabListener tag:%s fragment:%s",mTag,mFragment);
        if (mFragment != null && !mFragment.isDetached())
        {
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.detach(mFragment);
            ft.commit();
        }
    }

    @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        if (mFragment == null)
        {
            mFragment = Fragment.instantiate(mActivity, mFragmentClass.getName(), mArgs);
            ft.add(android.R.id.content, mFragment, mTag);
        }
        else
        {
            ft.attach(mFragment);
        }
    }

    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        if (mFragment != null)
        {
            ft.detach(mFragment);
        }
    }

    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        //Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
    }
}