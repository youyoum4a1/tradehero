package com.tradehero.th.fragments.social.follower;

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
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.persistence.social.HeroType;
import javax.inject.Inject;
import timber.log.Timber;

public class FollowerManagerTabFragment extends BasePurchaseManagerFragment
{
    private FollowerManagerViewContainer viewContainer;

    private FollowerAndPayoutListItemAdapter followerListAdapter;
    private UserBaseKey followedId;
    private FollowerSummaryDTO followerSummaryDTO;

    @Inject protected CurrentUserId currentUserId;
    private FollowerManagerInfoFetcher infoFetcher;

    int page;
    HeroType followerType;

    public FollowerManagerTabFragment()
    {
    }

    public FollowerManagerTabFragment(int page)
    {
        this.page = page;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.page = args.getInt(FollowerManagerFragment.KEY_PAGE);
        this.followerType = HeroType.fromId(args.getInt(FollowerManagerFragment.KEY_ID));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view =
                inflater.inflate(R.layout.fragment_store_manage_followers, container, false);
        initViews(view);
        Timber.d("FollowerManagerTabFragment onCreateView");
        return view;
    }

    @Override protected void initViews(View view)
    {
        this.viewContainer = new FollowerManagerViewContainer(view);
        this.infoFetcher =
                new FollowerManagerInfoFetcher(new FollowerManagerFollowerSummaryListener());

        if (followerListAdapter == null)
        {
            followerListAdapter = new FollowerAndPayoutListItemAdapter(getActivity(),
                    getActivity().getLayoutInflater(),
                    R.layout.follower_list_header,
                    R.layout.hero_payout_list_item,
                    R.layout.hero_payout_none_list_item,
                    R.layout.follower_list_item,
                    R.layout.follower_none_list_item
            );
        }

        if (this.viewContainer.followerList != null)
        {
            this.viewContainer.followerList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
                        {
                            handleFollowerItemClicked(view, position, id);
                        }
                    }
            );
            this.viewContainer.followerList.setAdapter(followerListAdapter);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.manage_followers_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("FollowerManagerTabFragment onResume");
        this.followedId = new UserBaseKey(
                getArguments().getInt(FollowerManagerFragment.BUNDLE_KEY_HERO_ID));

        this.infoFetcher.fetch(this.followedId);
    }

    @Override public void onDestroyView()
    {
        if (this.viewContainer.followerList != null)
        {
            this.viewContainer.followerList.setOnItemClickListener(null);
        }
        this.viewContainer = null;
        this.followerListAdapter = null;
        if (this.infoFetcher != null)
        {
            this.infoFetcher.onDestroyView();
        }
        this.infoFetcher = null;
        super.onDestroyView();
    }

    public void display(FollowerSummaryDTO summaryDTO)
    {
        linkWith(summaryDTO, true);
    }

    public void linkWith(FollowerSummaryDTO summaryDTO, boolean andDisplay)
    {
        this.followerSummaryDTO = summaryDTO;
        if (andDisplay)
        {
            this.viewContainer.displayTotalRevenue(summaryDTO);
            this.viewContainer.displayTotalAmountPaid(summaryDTO);
            this.viewContainer.displayFollowersCount(summaryDTO);
            displayFollowerList();
        }
    }

    public void display()
    {
        this.viewContainer.displayTotalRevenue(this.followerSummaryDTO);
        this.viewContainer.displayTotalAmountPaid(this.followerSummaryDTO);
        this.viewContainer.displayFollowersCount(this.followerSummaryDTO);
        displayFollowerList();
    }

    public void displayFollowerList()
    {
        if (this.followerListAdapter != null)
        {
            this.followerListAdapter.setFollowerSummaryDTO(this.followerSummaryDTO);
        }
    }

    public void displayProgress(boolean running)
    {
        if (this.viewContainer.progressBar != null)
        {
            this.viewContainer.progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    private void handleFollowerItemClicked(View view, int position, long id)
    {
        if (followerListAdapter != null
                && followerListAdapter.getItemViewType(position)
                == FollowerAndPayoutListItemAdapter.VIEW_TYPE_ITEM_FOLLOWER)
        {
            UserFollowerDTO followerDTO =
                    (UserFollowerDTO) followerListAdapter.getItem(position);
            if (followerDTO != null)
            {
                FollowerHeroRelationId followerHeroRelationId =
                        new FollowerHeroRelationId(getApplicablePortfolioId().userId, followerDTO.id, followerDTO.displayName);
                Bundle args = new Bundle();
                args.putBundle(FollowerPayoutManagerFragment.BUNDLE_KEY_FOLLOWER_ID_BUNDLE,
                        followerHeroRelationId.getArgs());
                ((DashboardActivity) getActivity()).getDashboardNavigator()
                        .pushFragment(FollowerPayoutManagerFragment.class, args);
            }
            else
            {
                Timber.d("handleFollowerItemClicked: FollowerDTO was null");
            }
        }
        else
        {
            THToast.show("Position clicked " + position);
        }
    }

    private class FollowerManagerFollowerSummaryListener
            implements DTOCache.Listener<UserBaseKey, FollowerSummaryDTO>
    {
        @Override
        public void onDTOReceived(UserBaseKey key, FollowerSummaryDTO value, boolean fromCache)
        {
            displayProgress(false);
            if (followerType == HeroType.FREE){
                display(value.getFreeFollowerSummaryDTO());
            }
            else if (followerType == HeroType.PREMIUM)
            {
                display(value.getPaidFollowerSummaryDTO());
            }
            else
            {
                display(value);
            }

            notifyFollowerLoaded(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_follower);
            Timber.e("Failed to fetch FollowerSummary", error);
        }
    }


    private void notifyFollowerLoaded(FollowerSummaryDTO value)
    {
        Timber.d("notifyFollowerLoaded for page:%d", page);
        OnFollowersLoadedListener loadedListener = FragmentUtils.getParent(this,OnFollowersLoadedListener.class);
        if (loadedListener != null && !isDetached())
        {
            loadedListener.onFollowerLoaded(page, value);
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    //</editor-fold>
}