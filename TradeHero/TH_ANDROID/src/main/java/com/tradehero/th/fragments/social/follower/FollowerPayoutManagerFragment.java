package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.key.FollowerId;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.social.UserFollowerCache;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class FollowerPayoutManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = FollowerPayoutManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_FOLLOWER_ID_BUNDLE = FollowerPayoutManagerFragment.class.getName() + ".followerId";

    private ImageView followerPicture;
    private TextView followerName;
    private TextView totalRevenue;
    private ActionBar actionBar;
    private FollowerPaymentListView followerPaymentListView;

    private FollowerPaymentListItemAdapter followerPaymentListAdapter;
    private FollowerId followerId;
    private UserFollowerDTO userFollowerDTO;

    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<UserFollowerCache> userFollowerCache;
    private DTOCache.Listener<FollowerId, UserFollowerDTO> userFollowerListener;
    private DTOCache.GetOrFetchTask<FollowerId, UserFollowerDTO> userFollowerFetchTask;
    @Inject UserBaseDTOUtil userBaseDTOUtil;

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_follower_revenue, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        followerPicture = (ImageView) view.findViewById(R.id.follower_profile_picture);
        if (followerPicture != null)
        {
            followerPicture.setOnClickListener(userClickHandler);
        }
        followerName = (TextView) view.findViewById(R.id.follower_title);
        if (followerName != null)
        {
            followerName.setOnClickListener(userClickHandler);
        }

        totalRevenue = (TextView) view.findViewById(R.id.follower_revenue);
        followerPaymentListView = (FollowerPaymentListView) view.findViewById(R.id.follower_payments_list);

        if (followerPaymentListAdapter == null)
        {
            followerPaymentListAdapter = new FollowerPaymentListItemAdapter(
                    getActivity(), getActivity().getLayoutInflater(), R.layout.follower_payment_list_item, R.layout.follower_payment_list_header);
        }
        if (followerPaymentListView != null)
        {
            followerPaymentListView.setAdapter(followerPaymentListAdapter);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        followerId = new FollowerId(getArguments().getBundle(BUNDLE_KEY_FOLLOWER_ID_BUNDLE));
        fetchFollowerSummary();
    }

    @Override public void onPause()
    {
        userFollowerListener = null;
        if (userFollowerFetchTask != null)
        {
            userFollowerFetchTask.setListener(null);
        }
        userFollowerFetchTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        followerPaymentListAdapter = null;
        followerPicture.setOnClickListener(null);
        followerName.setOnClickListener(null);
        super.onDestroyView();
    }

    protected void fetchFollowerSummary()
    {
        UserFollowerDTO followerDTO = userFollowerCache.get().get(followerId);
        if (followerDTO != null)
        {
            display(followerDTO);
        }
        else
        {
            if (userFollowerListener == null)
            {
                userFollowerListener = new DTOCache.Listener<FollowerId, UserFollowerDTO>()
                {
                    @Override public void onDTOReceived(FollowerId key, UserFollowerDTO value, boolean fromCache)
                    {
                        display(value);
                    }

                    @Override public void onErrorThrown(FollowerId key, Throwable error)
                    {
                        THToast.show("There was an error fetching your follower information");
                    }
                };
            }
            if (userFollowerFetchTask != null)
            {
                userFollowerFetchTask.setListener(null);
            }
            userFollowerFetchTask = userFollowerCache.get().getOrFetch(followerId, userFollowerListener);
            userFollowerFetchTask.execute();
        }
    }

    protected String getDisplayName()
    {
        return userBaseDTOUtil.getLongDisplayName(getActivity(), userFollowerDTO);
    }

    public void display(UserFollowerDTO summaryDTO)
    {
        linkWith(summaryDTO, true);
    }

    public void linkWith(UserFollowerDTO summaryDTO, boolean andDisplay)
    {
        this.userFollowerDTO = summaryDTO;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayUserIcon();
        displayActionBarTitle();
        displayFollowerName();
        displayTotalRevenue();
        displayPaymentList();
    }

    public void displayUserIcon()
    {
        if (followerPicture != null)
        {
            if (userFollowerDTO != null)
            {
                picasso.get().load(userFollowerDTO.picture)
                        .transform(peopleIconTransformation)
                        .into(followerPicture);
            }
        }
    }

    public void displayFollowerName()
    {
        if (followerName != null)
        {
            followerName.setText(getDisplayName());
        }
    }

    public void displayTotalRevenue()
    {
        if (totalRevenue != null)
        {
            if (userFollowerDTO != null)
            {
                totalRevenue.setText(String.format("%s %,.2f", SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY, userFollowerDTO.totalRevenue));
            }
            else
            {
                totalRevenue.setText(R.string.na);
            }
        }
    }

    public void displayActionBarTitle()
    {
        if (actionBar != null)
        {
            actionBar.setTitle(getDisplayName());
        }
    }

    public void displayPaymentList()
    {
        if (followerPaymentListAdapter != null)
        {
            if (userFollowerDTO != null)
            {
                followerPaymentListAdapter.setItems(userFollowerDTO.followerTransactions);
            }
        }
    }

    private View.OnClickListener userClickHandler = new View.OnClickListener()
    {
        @Override public void onClick(View v)
        {
            if (userFollowerDTO != null)
            {
                Bundle bundle = new Bundle();
                bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userFollowerDTO.id);
                getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    };
}
