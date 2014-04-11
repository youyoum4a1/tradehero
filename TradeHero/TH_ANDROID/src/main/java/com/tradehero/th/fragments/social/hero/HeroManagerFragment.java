package com.tradehero.th.fragments.social.hero;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.updatecenter.TabListener;
import java.text.MessageFormat;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template
 * use File | Settings | File Templates.
 */
public class HeroManagerFragment extends BaseFragment /*BasePurchaseManagerFragment*/
{
    public static final String TAG = HeroManagerFragment.class.getSimpleName();

    static final String KEY_PAGE = "KEY_PAGE";
    static final String KEY_ID = "KEY_ID";
    /**
     * We are showing the heroes of this follower
     */
    public static final String BUNDLE_KEY_FOLLOWER_ID =
            HeroManagerFragment.class.getName() + ".followerId";
    /** categories of hero:premium,free,all */
    private HeroTypeExt[] heroTypes;
    private int selectedId = -1;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        heroTypes = HeroTypeExt.getSortedList();
        Timber.d("onCreate");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes_2, container, false);
        Timber.d("onCreateView");
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        addTabs();
    }

    @Override public void onStart()
    {
        super.onStart();
        Timber.d("onStart");
    }


    @Override public void onResume()
    {
        super.onResume();
        Timber.d("onResume");
    }

    @Override public void onPause()
    {
        super.onPause();
        Timber.d("onPause");
    }

    @Override public void onStop()
    {
        super.onStop();
        Timber.d("onStop");
    }

    private void addTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        ActionBar.Tab lastSavedTab = null;
        int lastSelectedId = selectedId;
        HeroTypeExt[] types = heroTypes;
        Bundle args = getArguments();
        if (args == null)
        {
            args = new Bundle();
        }
        for (HeroTypeExt type : types)
        {
            args = new Bundle(args);
            args.putInt(KEY_PAGE, type.pageIndex);
            args.putInt(KEY_ID, type.heroType.typeId);
            ActionBar.Tab tab = actionBar.newTab().setTabListener(
                    new MyTabListener(getSherlockActivity(), type.fragmentClass, type.toString(),
                            args));
            tab.setTag(type.heroType.typeId);
            setTabTitle(tab, type.titleRes, 0);
            actionBar.addTab(tab);
            if (type.heroType.typeId == lastSelectedId)
            {
                lastSavedTab = tab;
            }
        }
        //actionBar.setSelectedNavigationItem();
        Timber.d("lastSavedTab %s selectedId %d", lastSavedTab, selectedId);
        if (lastSavedTab != null)
        {
            actionBar.selectTab(lastSavedTab);
        }
    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
    }

    private void saveSelectedTab()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        this.selectedId = (Integer) actionBar.getSelectedTab().getTag();
    }

    private void setTabTitle(ActionBar.Tab tab, int titleRes, int number)
    {
        String title;
        title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    /**
     * change the number of tab
     */
    private void changeTabTitle(int page, int number)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        ActionBar.Tab tab = actionBar.getTabAt(page);

        int titleRes = 0;
        switch (page)
        {
            case 0:
                titleRes = R.string.leaderboard_community_hero_premium;
                break;
            case 1:
                titleRes = R.string.leaderboard_community_hero_free;
                break;
            case 2:
                titleRes = R.string.leaderboard_community_hero_all;
                break;
        }
        String title = "";
        title = MessageFormat.format(getSherlockActivity().getString(titleRes), number);
        tab.setText(title);
    }

    private void changeTabTitle(int number1, int number2, int number3)
    {
        changeTabTitle(0, number1);
        changeTabTitle(1, number2);
        changeTabTitle(2, number3);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        saveSelectedTab();
        clearTabs();
        Timber.d("onDestroyView");
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        Timber.d("onDestroy");
    }

    @Override public void onDetach()
    {
        super.onDetach();
        Timber.d("onDetach");
    }

    OnHeroesLoadedListener onHeroesLoadedListener = new OnHeroesLoadedListener()
    {
        @Override public void onHerosLoaded(int page, HeroIdExtWrapper value)
        {
            if (!isDetached())
            {
                changeTabTitle(0, value.herosCountGetPaid);
                changeTabTitle(1, value.herosCountNotGetPaid);
                changeTabTitle(2, (value.herosCountGetPaid + value.herosCountNotGetPaid));
            }
        }
    };

    public static interface OnHeroesLoadedListener
    {
        void onHerosLoaded(int page, HeroIdExtWrapper value);
    }

    class MyTabListener extends TabListener
    {

        public MyTabListener(SherlockFragmentActivity activity,
                Class<? extends Fragment> fragmentClass, String tag, Bundle args)
        {
            super(activity, fragmentClass, tag, args);
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            if (mFragment == null)
            {
                mFragment = Fragment.instantiate(mActivity, mFragmentClass.getName(), mArgs);
                HeroesTabContentFragment fragment = (HeroesTabContentFragment) mFragment;
                fragment.setOnHeroesLoadedListener(onHeroesLoadedListener);
                ft.add(R.id.fragment_content, mFragment, mTag);
            }
            else
            {
                super.onTabSelected(tab, ft);
            }
        }
    }
}


