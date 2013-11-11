package com.tradehero.th.fragments.billing.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.persistence.social.FollowerSummaryCache;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class FollowerManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = FollowerManagerFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_USER_ID = FollowerManagerFragment.class.getName() + ".userId";

    private TextView totalRevenue;
    private TextView totalAmountPaid;
    private TextView followersCount;

    private UserBaseKey userBaseKey;
    private FollowerSummaryDTO followerSummaryDTO;

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
        Bundle args = getArguments();
        if (args != null)
        {
            userBaseKey = new UserBaseKey(args.getInt(BUNDLE_KEY_USER_ID, THUser.getCurrentUserBase().getBaseKey().key));
        }
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

    protected void fetchFollowerSummary()
    {
        if (userBaseKey == null)
        {
            userBaseKey = THUser.getCurrentUserBase().getBaseKey();
        }
        FollowerSummaryDTO summaryDTO = followerSummaryCache.get().get(userBaseKey);
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
                        display(value);
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        THToast.show("There was an error fetching your follower summary information");
                    }
                };
            }
            if (followerSummaryFetchTask != null)
            {
                followerSummaryFetchTask.forgetListener(true);
            }
            followerSummaryFetchTask = followerSummaryCache.get().getOrFetch(userBaseKey, followerSummaryListener);
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
        }
    }

    public void display()
    {
        displayTotalRevenue();
        displayTotalAmountPaid();
        displayFollowersCount();
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
}
