package com.tradehero.th.fragments.social.hero;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.persistence.social.HeroCache;
import dagger.Lazy;
import java.text.MessageFormat;
import java.util.List;
import javax.inject.Inject;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class HeroManagerFragment extends BaseFragment /*BasePurchaseManagerFragment*/
{
    public static final String TAG = HeroManagerFragment.class.getSimpleName();

    /**
     * We are showing the heroes of this follower
     */
    public static final String BUNDLE_KEY_FOLLOWER_ID = HeroManagerFragment.class.getName() + ".followerId";

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes_2, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        addTabs();
    }


    private void addTabs() {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        Fragment f0 = new HerosTabContentFragment(0);
        f0.setArguments(getArguments());

        Fragment f1 = new HerosTabContentFragment(1);
        f1.setArguments(getArguments());

        Fragment f2 = new HerosTabContentFragment(2);
        f2.setArguments(getArguments());


        ////Action Bar Tab must have a Callback
        actionBar.addTab(actionBar.newTab().setText(MessageFormat.format(getSherlockActivity().getString(
                R.string.leaderboard_community_hero_premium),0)).setTabListener(
                new TabListener(f0)));
        actionBar.addTab(actionBar.newTab().setText(MessageFormat.format(
                getSherlockActivity().getString(R.string.leaderboard_community_hero_free),
                0)).setTabListener(
                new TabListener(f1)));
        actionBar.addTab(actionBar.newTab().setText(MessageFormat.format(
                getSherlockActivity().getString(R.string.leaderboard_community_hero_all),
                0)).setTabListener(
                new TabListener(f2)));

    }

    private void clearTabs()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.removeAllTabs();
    }

    /**
     * change the number of tab
     * @param page
     * @param number
     */
    private void changetTabTitle(int page, int number)
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

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        clearTabs();
    }

    /**
     * Callback
     */
    private class TabListener implements ActionBar.TabListener {


        private Fragment mFragment;

        public TabListener(Fragment fragment) {
            mFragment = fragment;
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {

            ft.add(R.id.fragment_content, mFragment, mFragment.getTag());
        }

        @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            ft.remove(mFragment);
        }

        @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            //Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     *
     */
    public static class HerosTabContentFragment extends BasePurchaseManagerFragment {

        private int page;
        public HerosTabContentFragment(int page)
        {
            this.page = page;
        }

        private HeroManagerViewContainer viewContainer;
        private ProgressDialog progressDialog;

        private HeroListItemAdapter heroListAdapter;
        private HeroListItemView.OnHeroStatusButtonClickedListener heroStatusButtonClickedListener;
        private HeroListMostSkilledClickedListener heroListMostSkilledClickedListener;

        // The user whose heroes we are listing
        private UserBaseKey followerId;
        private UserProfileDTO userProfileDTO;
        private List<HeroDTO> heroDTOs;

        @Inject public Lazy<HeroCache> heroCache;
        private HeroManagerInfoFetcher infoFetcher;
        @Inject public HeroAlertDialogUtil heroAlertDialogUtil;

        //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
        @Override public boolean isTabBarVisible()
        {
            return false;
        }
        //</editor-fold>

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
            initViews(view);
            return view;
        }

        @Override protected void initViews(View view)
        {
            this.viewContainer = new HeroManagerViewContainer(view);
            if (this.viewContainer.btnBuyMore != null)
            {
                this.viewContainer.btnBuyMore.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View view)
                    {
                        handleBuyMoreClicked();
                    }
                });
            }

            this.heroStatusButtonClickedListener = new HeroListItemView.OnHeroStatusButtonClickedListener()
            {
                @Override public void onHeroStatusButtonClicked(HeroListItemView heroListItemView, HeroDTO heroDTO)
                {
                    handleHeroStatusButtonClicked(heroDTO);
                }
            };
            this.heroListMostSkilledClickedListener = new HeroListMostSkilledClickedListener();
            this.heroListAdapter = new HeroListItemAdapter(
                    getActivity(),
                    getActivity().getLayoutInflater(),
                    R.layout.hero_list_item_empty_placeholder,
                    R.layout.hero_list_item,
                    R.layout.hero_list_header,
                    R.layout.hero_list_header);
            this.heroListAdapter.setHeroStatusButtonClickedListener(this.heroStatusButtonClickedListener);
            this.heroListAdapter.setMostSkilledClicked(this.heroListMostSkilledClickedListener);
            if (this.viewContainer.heroListView != null)
            {
                this.viewContainer.heroListView.setAdapter(this.heroListAdapter);
                this.viewContainer.heroListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        handleHeroClicked(parent, view, position, id);
                    }
                });
            }
            setListShown(false);
            this.infoFetcher = new HeroManagerInfoFetcher(new HeroManagerUserProfileCacheListener(), new HeroManagerHeroListCacheListener());
        }
        private void setListShown(boolean shown)
        {
            if (shown)
            {
                this.viewContainer.heroListView.setVisibility(View.VISIBLE);
                this.viewContainer.progressBar.setVisibility(View.INVISIBLE);
            }else
            {
                this.viewContainer.heroListView.setVisibility(View.INVISIBLE);
                this.viewContainer.progressBar.setVisibility(View.VISIBLE);
            }

        }


        @Override protected void createUserInteractor()
        {
            userInteractor = new HeroManagerTHIABUserInteractor();
        }

        @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            ActionBar actionBar = getSherlockActivity().getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.setTitle(R.string.manage_heroes_title);
            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override public void onResume()
        {
            super.onResume();
            this.followerId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWER_ID));
            displayProgress(true);
            this.infoFetcher.fetch(this.followerId);
        }

        @Override public void onPause()
        {
            this.infoFetcher.onPause();

            if (this.progressDialog != null)
            {
                this.progressDialog.hide();
            }
            super.onPause();
        }

        @Override public void onDestroyView()
        {
            if (this.infoFetcher != null)
            {
                this.infoFetcher.onDestroyView();
            }
            this.infoFetcher = null;

            this.heroStatusButtonClickedListener = null;
            if (this.heroListAdapter != null)
            {
                this.heroListAdapter.setHeroStatusButtonClickedListener(null);
                this.heroListAdapter.setMostSkilledClicked(null);
            }
            this.heroListAdapter = null;
            if (this.viewContainer.heroListView != null)
            {
                this.viewContainer.heroListView.setOnItemClickListener(null);
            }
            this.viewContainer = null;
            super.onDestroyView();
        }

        private void handleBuyMoreClicked()
        {
            userInteractor.conditionalPopBuyFollowCredits();
        }

        private void handleHeroStatusButtonClicked(HeroDTO heroDTO)
        {
            handleHeroStatusChangeRequired(heroDTO);
        }

        private void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
        {
            pushTimelineFragment(((HeroDTO) parent.getItemAtPosition(position)).getBaseKey());
        }

        private void handleHeroStatusChangeRequired(final HeroDTO clickedHeroDTO)
        {
            if (!clickedHeroDTO.active)
            {
                heroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        userInteractor.followHero(clickedHeroDTO.getBaseKey());
                    }
                });
            }
            else
            {
                heroAlertDialogUtil.popAlertUnfollowHero(getActivity(), new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        userInteractor.unfollowHero(clickedHeroDTO.getBaseKey());
                    }
                });
            }
        }

        private void pushTimelineFragment(UserBaseKey userBaseKey)
        {
            Bundle args = new Bundle();
            args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);
            ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(
                    PushableTimelineFragment.class, args);
        }

        private void handleGoMostSkilled()
        {
            // TODO this feels HACKy
            ((DashboardActivity) getActivity()).getDashboardNavigator().popFragment();

            // TODO make it go to most skilled
            ((DashboardActivity) getActivity()).getDashboardNavigator().goToTab(
                    DashboardTabType.COMMUNITY);
        }

        public void display(UserProfileDTO userProfileDTO)
        {
            linkWith(userProfileDTO, true);
        }

        private void display(List<HeroDTO> heroDTOs)
        {
            linkWith(heroDTOs, true);
        }

        public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
        {
            this.userProfileDTO = userProfileDTO;
            if (andDisplay)
            {
                viewContainer.displayFollowCount(userProfileDTO);
                viewContainer.displayCoinStack(userProfileDTO);
            }
        }

        public void linkWith(List<HeroDTO> heroDTOs, boolean andDisplay)
        {
            this.heroDTOs = heroDTOs;
            heroListAdapter.setItems(this.heroDTOs);
            if (andDisplay)
            {
                displayHeroList();
            }
        }

        //<editor-fold desc="Display methods">
        public void display()
        {
            viewContainer.displayFollowCount(userProfileDTO);
            viewContainer.displayCoinStack(userProfileDTO);
            displayHeroList();
        }

        public void displayHeroList()
        {
            if (heroListAdapter != null)
            {
                heroListAdapter.notifyDataSetChanged();
            }
        }

        public void displayProgress(boolean running)
        {
            if (viewContainer.progressBar != null)
            {
                viewContainer.progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
            }
        }
        //</editor-fold>

        public class HeroManagerTHIABUserInteractor extends THIABUserInteractor
        {
            public HeroManagerTHIABUserInteractor()
            {
                super();
            }

            @Override protected void handleShowProductDetailsMilestoneComplete()
            {
                super.handleShowProductDetailsMilestoneComplete();
            }

            @Override protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
            {
                super.handleShowProductDetailsMilestoneFailed(throwable);
            }

            @Override protected void handlePurchaseReportSuccess(THIABPurchase reportedPurchase, UserProfileDTO updatedUserProfile)
            {
                super.handlePurchaseReportSuccess(reportedPurchase, updatedUserProfile);
                display(updatedUserProfile);
            }

            @Override protected void createFollowCallback()
            {
                this.followCallback = new UserInteractorFollowHeroCallback(heroListCache.get(), userProfileCache.get())
                {
                    @Override public void success(UserProfileDTO userProfileDTO, Response response)
                    {
                        super.success(userProfileDTO, response);
                        HerosTabContentFragment.this.linkWith(userProfileDTO, true);
                        if (HerosTabContentFragment.this.infoFetcher != null)
                        {
                            HerosTabContentFragment.this.infoFetcher.fetchHeroes(HerosTabContentFragment.this.followerId);
                        }
                    }
                };
            }
        }

        private class HeroManagerUserProfileCacheListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
            {
                if (key.equals(HerosTabContentFragment.this.followerId))
                {
                    display(value);
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THLog.e(TAG, "Could not fetch user profile", error);
                THToast.show(R.string.error_fetch_user_profile);
            }
        }

        private class HeroManagerHeroListCacheListener implements DTOCache.Listener<UserBaseKey, HeroIdList>
        {
            @Override public void onDTOReceived(UserBaseKey key, HeroIdList value, boolean fromCache)
            {
                //displayProgress(false);
                setListShown(true);
                display(heroCache.get().get(value));
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                displayProgress(false);
                setListShown(false);
                THLog.e(TAG, "Could not fetch heroes", error);
                THToast.show(R.string.error_fetch_hero);
            }
        }

        private class HeroListMostSkilledClickedListener implements View.OnClickListener
        {
            @Override public void onClick(View view)
            {
                handleGoMostSkilled();
            }
        }
    }
    }


