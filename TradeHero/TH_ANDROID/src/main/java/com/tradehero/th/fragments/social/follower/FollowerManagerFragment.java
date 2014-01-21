package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.FollowerId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class FollowerManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = FollowerManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_FOLLOWED_ID = FollowerManagerFragment.class.getName() + ".followedId";

    private FollowerManagerViewContainer viewContainer;

    private FollowerAndPayoutListItemAdapter followerListAdapter;
    private UserBaseKey followedId;
    private FollowerSummaryDTO followerSummaryDTO;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    private FollowerManagerInfoFetcher infoFetcher;

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_followers, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        this.viewContainer = new FollowerManagerViewContainer(view);
        this.infoFetcher = new FollowerManagerInfoFetcher(new FollowerManagerFollowerSummaryListener());

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
            this.viewContainer.followerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    handleFollowerItemClicked(view, position, id);
                }
            });
            this.viewContainer.followerList.setAdapter(followerListAdapter);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.manage_followers_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        this.followedId = new UserBaseKey(getArguments().getInt(BUNDLE_KEY_FOLLOWED_ID));
        this.infoFetcher.fetch(this.followedId);
    }

    @Override public void onPause()
    {
        this.infoFetcher.onPause();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (this.viewContainer.followerList != null)
        {
            this.viewContainer.followerList.setOnItemClickListener(null);
        }
        this.viewContainer = null;
        this.followerListAdapter = null;
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
        if (followerListAdapter != null && followerListAdapter.getItemViewType(position) == FollowerAndPayoutListItemAdapter.VIEW_TYPE_ITEM_FOLLOWER)
        {
            UserFollowerDTO followerDTO = (UserFollowerDTO) followerListAdapter.getItem(position);
            if (followerDTO != null)
            {
                FollowerId followerId = new FollowerId(getApplicablePortfolioId().userId, followerDTO.id);
                Bundle args = new Bundle();
                args.putBundle(FollowerPayoutManagerFragment.BUNDLE_KEY_FOLLOWER_ID_BUNDLE, followerId.getArgs());
                ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(FollowerPayoutManagerFragment.class, args);
            }
            else
            {
                THLog.d(TAG, "handleFollowerItemClicked: FollowerDTO was null");
            }
        }
        else
        {
            THToast.show("Position clicked " + position);
        }
    }

    private class FollowerManagerFollowerSummaryListener implements DTOCache.Listener<UserBaseKey, FollowerSummaryDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, FollowerSummaryDTO value)
        {
            displayProgress(false);
            display(value);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_follower);
            THLog.e(TAG, "Failed to fetch FollowerSummary", error);
        }
    }
}
