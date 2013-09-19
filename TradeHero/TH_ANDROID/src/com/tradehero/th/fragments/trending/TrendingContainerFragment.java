package com.tradehero.th.fragments.trending;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.base.Application;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: xavier Date: 9/18/13 Time: 11:02 AM To change this template use File | Settings | File Templates. */
public class TrendingContainerFragment extends SherlockFragment
{
    private final static String TAG = TrendingContainerFragment.class.getSimpleName();

    private FragmentFactory fragmentFactory;
    private Menu menu;
    private MenuInflater menuInflater;
    private int fragmentContentId = R.id.trending_fragment_content;

    @Override public void onAttach(Activity activity)
    {
        THLog.i(TAG, "onAttach");
        super.onAttach(activity);

        fragmentFactory = new FragmentFactory();

        if (getChildFragmentManager().findFragmentById(fragmentContentId) == null)
        {
            pushTrendingIn();
        }

        setHasOptionsMenu(true);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.i(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_trending_container, container, false);
        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater)
    {
        THLog.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu, menuInflater);
        this.menu = menu;
        this.menuInflater = menuInflater;
        Fragment childFragment = getChildFragmentManager().findFragmentById(fragmentContentId);
        callMenuOptionsIfCan(childFragment);
    }

    private void callMenuOptionsIfCan(Fragment childFragment)
    {
        THLog.i(TAG, "callMenuOptionsIfCan on " + (childFragment == null ? "" : childFragment.getClass()));
        if (childFragment != null && ((SherlockFragment) childFragment) != null)
        {
            ((SherlockFragment) childFragment).onCreateOptionsMenu(menu, menuInflater);
        }
    }

    private void setListenerOnChildFragment(Fragment childFragment)
    {
        if (childFragment instanceof TrendingFragment)
        {
            ((TrendingFragment) childFragment).setSearchRequestedListener(new TrendingFragment.SearchRequestedListener()
            {
                @Override public void onSearchRequested()
                {
                    pushSearchIn();
                }
            });
        }
        else if (childFragment instanceof SearchStockPeopleFragment)
        {
            ((SearchStockPeopleFragment) childFragment).setBackRequestedListener(new SearchStockPeopleFragment.BackRequestedListener()
            {
                @Override public void onBackRequested()
                {
                    popTrendingBackIn();
                }
            });
        }
        else
        {
            THLog.i(TAG, "Unhandled child fragment type: " + childFragment.getClass().getSimpleName());
        }
    }

    private void pushTrendingIn()
    {
        THLog.i(TAG, "pushTrendingIn");
        getChildFragmentManager().beginTransaction()
                //.setCustomAnimations(
                //        R.anim.fade_back, R.anim.fade_back,
                //        R.anim.fade_back, R.anim.fade_back)
                .replace(fragmentContentId, fragmentFactory.getInstance(TrendingFragment.class))
                .commit();
    }

    private void pushSearchIn()
    {
        THLog.i(TAG, "pushSearchIn");
        Fragment searchFragment = fragmentFactory.getInstance(SearchStockPeopleFragment.class);
        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_right_in, R.anim.slide_left_out,
                    R.anim.slide_left_in, R.anim.slide_right_out
                    //R.anim.shrink_from_front, R.anim.shrink_to_back,
                    //R.anim.inflate_from_back, R.anim.inflate_to_front
                )
                .replace(fragmentContentId, searchFragment)
                //.show(searchFragment)
                .addToBackStack(null)
                .commit();
    }

    private void popTrendingBackIn()
    {
        THLog.i(TAG, "popTrendingBackIn");
        getChildFragmentManager().popBackStack();
    }

    private class FragmentFactory
    {
        private Map<Class<?>, Fragment> instances = new HashMap<>();

        public Fragment getInstance(Class<?> clss)
        {
            Fragment fragment = instances.get(clss);
            if (fragment == null)
            {
                fragment = Fragment.instantiate(Application.context(), clss.getName(), null);
                TrendingContainerFragment.this.setListenerOnChildFragment(fragment);
                instances.put(clss, fragment);
            }
            return fragment;
        }
    }
}
