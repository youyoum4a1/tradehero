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

    private TextView totalRevenue;
    private TextView totalAmountPaid;
    private TextView followersCount;
    private ListView followerList;
    private ProgressBar progressBar;

    private FollowerAndPayoutListItemAdapter followerListAdapter;
    private FollowerSummaryDTO followerSummaryDTO;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<FollowerSummaryCache> followerSummaryCache;
    private DTOCache.Listener<UserBaseKey, FollowerSummaryDTO> followerSummaryListener;
    private DTOCache.GetOrFetchTask<FollowerSummaryDTO> followerSummaryFetchTask;

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

    protected void initViews(View view)
    {
        totalRevenue = (TextView) view.findViewById(R.id.manage_followers_total_revenue);
        totalAmountPaid = (TextView) view.findViewById(R.id.manage_followers_total_amount_paid);
        followersCount = (TextView) view.findViewById(R.id.manage_followers_number_followers);
        followerList = (FollowerListView) view.findViewById(R.id.followers_list);
        progressBar = (ProgressBar) view.findViewById(android.R.id.empty);

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

        if (followerList != null)
        {
            followerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    handleFollowerItemClicked(view, position, id);
                }
            });
            followerList.setAdapter(followerListAdapter);
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
        fetchFollowerSummary();
    }

    @Override public void onPause()
    {
        followerSummaryListener = null;
        if (followerSummaryFetchTask != null)
        {
            followerSummaryFetchTask.forgetListener(true);
        }
        followerSummaryFetchTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        followerList = null;
        followerListAdapter = null;
        super.onDestroyView();
    }

    protected void fetchFollowerSummary()
    {
        FollowerSummaryDTO summaryDTO = followerSummaryCache.get().get(new UserBaseKey(getApplicablePortfolioId().userId));
        if (summaryDTO != null)
        {
            display(summaryDTO);
        }
        else
        {
            if (followerSummaryListener == null)
            {
                followerSummaryListener = new DTOCache.Listener<UserBaseKey, FollowerSummaryDTO>()
                {
                    @Override public void onDTOReceived(UserBaseKey key, FollowerSummaryDTO value)
                    {
                        displayProgress(false);
                        display(value);
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        displayProgress(false);
                        THToast.show("There was an error fetching your follower summary information");
                        THLog.e(TAG, "Failed to fetch FollowerSummary", error);
                    }
                };
            }
            if (followerSummaryFetchTask != null)
            {
                followerSummaryFetchTask.forgetListener(true);
            }
            followerSummaryFetchTask = followerSummaryCache.get().getOrFetch(new UserBaseKey(getApplicablePortfolioId().userId), followerSummaryListener);
            followerSummaryFetchTask.execute();
        }
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
            displayTotalRevenue();
            displayTotalAmountPaid();
            displayFollowersCount();
            displayFollowerList();
        }
    }

    public void display()
    {
        displayTotalRevenue();
        displayTotalAmountPaid();
        displayFollowersCount();
        displayFollowerList();
    }

    public void displayTotalRevenue()
    {
        if (totalRevenue != null)
        {
            if (followerSummaryDTO != null)
            {
                totalRevenue.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, followerSummaryDTO.totalRevenue));
            }
            else
            {
                totalRevenue.setText(R.string.na);
            }
        }
    }

    public void displayTotalAmountPaid()
    {
        if (totalAmountPaid != null)
        {
            if (followerSummaryDTO != null && followerSummaryDTO.payoutSummary != null)
            {
                totalAmountPaid.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, followerSummaryDTO.payoutSummary.totalPayout));
            }
            else if (followerSummaryDTO != null)
            {
                totalAmountPaid.setText("0");
            }
            else
            {
                totalAmountPaid.setText(R.string.na);
            }
        }
    }

    public void displayFollowersCount()
    {
        if (followersCount != null)
        {
            if (followerSummaryDTO != null && followerSummaryDTO.userFollowers != null)
            {
                followersCount.setText(String.format("%d", followerSummaryDTO.userFollowers.size()));
            }
            else if (followerSummaryDTO != null)
            {
                followersCount.setText("0");
            }
            else
            {
                followersCount.setText(R.string.na);
            }
        }
    }

    public void displayFollowerList()
    {
        if (followerListAdapter != null)
        {
            followerListAdapter.setFollowerSummaryDTO(followerSummaryDTO);
        }
    }

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
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
}
