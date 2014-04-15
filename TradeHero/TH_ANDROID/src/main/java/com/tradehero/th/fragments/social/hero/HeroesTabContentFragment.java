package com.tradehero.th.fragments.social.hero;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroIdExtWrapper;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.googleplay.THIABPurchase;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.persistence.social.HeroKey;
import com.tradehero.th.persistence.social.HeroCache;
import com.tradehero.th.persistence.social.HeroType;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import retrofit.client.Response;
import timber.log.Timber;

public class HeroesTabContentFragment extends BasePurchaseManagerFragment
{
    private HeroType heroType;
    private int page;

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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.page = args.getInt(HeroManagerFragment.KEY_PAGE);
        int heroTypeId = args.getInt(HeroManagerFragment.KEY_ID);
        this.heroType = HeroType.fromId(heroTypeId);
        Timber.d("onCreate page:%s,heroTypeId:%s,heroType:%s", page, heroTypeId, heroType);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
        initViews(view);
        Timber.d("onCreateView page:%s", page);
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

        this.heroStatusButtonClickedListener =
                new HeroListItemView.OnHeroStatusButtonClickedListener()
                {
                    @Override
                    public void onHeroStatusButtonClicked(HeroListItemView heroListItemView,
                            HeroDTO heroDTO)
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
        this.heroListAdapter.setHeroStatusButtonClickedListener(
                this.heroStatusButtonClickedListener);
        this.heroListAdapter.setMostSkilledClicked(this.heroListMostSkilledClickedListener);
        if (this.viewContainer.heroListView != null)
        {
            this.viewContainer.heroListView.setAdapter(this.heroListAdapter);
            this.viewContainer.heroListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
                        {
                            handleHeroClicked(parent, view, position, id);
                        }
                    });
        }
        setListShown(false);
        this.infoFetcher = new HeroManagerInfoFetcher(new HeroManagerUserProfileCacheListener(),
                new HeroManagerHeroListCacheListener());
    }

    private void setListShown(boolean shown)
    {
        if (shown)
        {
            this.viewContainer.heroListView.setVisibility(View.VISIBLE);
            this.viewContainer.progressBar.setVisibility(View.INVISIBLE);
        }
        else
        {
            this.viewContainer.heroListView.setVisibility(View.INVISIBLE);
            this.viewContainer.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.manage_heroes_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        this.followerId =
                new UserBaseKey(getArguments().getInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID));
        displayProgress(true);

        Timber.d("fetch heros heroType:%s", getHeroType());
        this.infoFetcher.fetch(this.followerId, getHeroType());
    }

    HeroType getHeroType()
    {
        return this.heroType;
    }

    void setHeroType(HeroType heroType)
    {
        this.heroType = heroType;
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
        Timber.d("onDestroyView page:%s", page);
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        Timber.d("onDestroy page:%s", page);
    }

    private void handleBuyMoreClicked()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
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
            heroAlertDialogUtil.popAlertFollowHero(getActivity(),
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            followUser(clickedHeroDTO.getBaseKey());
                        }
                    });
        }
        else
        {
            heroAlertDialogUtil.popAlertUnfollowHero(getActivity(),
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            unfollowUser(clickedHeroDTO.getBaseKey());
                        }
                    });
        }
    }

    private void pushTimelineFragment(UserBaseKey userBaseKey)
    {
        Bundle args = new Bundle();
        args.putInt(PushableTimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseKey.key);
        getDashboardNavigator().pushFragment(PushableTimelineFragment.class, args);
    }

    private void handleGoMostSkilled()
    {
        // TODO this feels HACKy
        getDashboardNavigator().popFragment();

        // TODO make it go to most skilled
        getDashboardNavigator().goToTab(DashboardTabType.COMMUNITY);
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

    private class HeroManagerUserProfileCacheListener
            implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            if (key.equals(HeroesTabContentFragment.this.followerId))
            {
                display(value);
            }
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            Timber.e("Could not fetch user profile", error);
            THToast.show(R.string.error_fetch_user_profile);
        }
    }

    private class HeroManagerHeroListCacheListener
            implements DTOCache.Listener<HeroKey, HeroIdExtWrapper>
    {
        @Override public void onDTOReceived(HeroKey key, HeroIdExtWrapper value, boolean fromCache)
        {
            //displayProgress(false);
            setListShown(true);
            display(heroCache.get().get(value.heroIdList));
            notifyHeroesLoaded(value);
            Timber.d("onDTOReceived key:%s,value:%s", key, value);
        }

        @Override public void onErrorThrown(HeroKey key, Throwable error)
        {
            displayProgress(false);
            setListShown(true);
            Timber.e("Could not fetch heroes", error);
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


    private void notifyHeroesLoaded(HeroIdExtWrapper value)
    {

        OnHeroesLoadedListener listener = FragmentUtils.getParent(this,OnHeroesLoadedListener.class);
        Timber.d("OnHeroesLoadedListener listener:%s",listener);
        if (listener != null && !isDetached())
        {
            listener.onHerosLoaded(page, value);
        }
    }
}