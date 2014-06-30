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
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.social.UserFollowerCache;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.THRouter;
import dagger.Lazy;
import javax.inject.Inject;

public class FollowerPayoutManagerFragment extends BasePurchaseManagerFragment
{
    public static final String BUNDLE_KEY_FOLLOWER_ID_BUNDLE =
            FollowerPayoutManagerFragment.class.getName() + ".followerId";

    private ImageView followerPicture;
    private TextView followerName;
    private TextView totalRevenue;
    private ActionBar actionBar;
    private FollowerPaymentListView followerPaymentListView;
    private View errorView;

    private FollowerPaymentListItemAdapter followerPaymentListAdapter;
    private FollowerHeroRelationId followerHeroRelationId;
    private UserFollowerDTO userFollowerDTO;

    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<UserFollowerCache> userFollowerCache;
    private DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO> userFollowerListener;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject THRouter thRouter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userFollowerListener = createFollowerListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view =
                inflater.inflate(R.layout.fragment_store_manage_follower_revenue, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        followerPicture = (ImageView) view.findViewById(R.id.follower_profile_picture);
        if (followerPicture != null)
        {
            followerPicture.setOnClickListener(createUserClickHandler());
        }
        followerName = (TextView) view.findViewById(R.id.follower_title);
        if (followerName != null)
        {
            followerName.setOnClickListener(createUserClickHandler());
        }

        totalRevenue = (TextView) view.findViewById(R.id.follower_revenue);
        followerPaymentListView =
                (FollowerPaymentListView) view.findViewById(R.id.follower_payments_list);

        errorView = view.findViewById(R.id.error_view);

        if (followerPaymentListAdapter == null)
        {
            followerPaymentListAdapter = new FollowerPaymentListItemAdapter(
                    getActivity(), getActivity().getLayoutInflater(),
                    R.layout.follower_payment_list_item, R.layout.follower_payment_list_header);
        }
        if (followerPaymentListView != null)
        {
            followerPaymentListView.setAdapter(followerPaymentListAdapter);
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_HOME_AS_UP);
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        followerHeroRelationId =
                new FollowerHeroRelationId(getArguments().getBundle(BUNDLE_KEY_FOLLOWER_ID_BUNDLE));
        fetchFollowerSummary();
    }

    @Override public void onStop()
    {
        detachUserFollowerCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        followerPaymentListAdapter = null;
        followerPicture.setOnClickListener(null);
        followerName.setOnClickListener(null);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userFollowerListener = null;
        super.onDestroy();
    }

    protected void detachUserFollowerCache()
    {
        userFollowerCache.get().unregister(userFollowerListener);
    }

    protected void fetchFollowerSummary()
    {
        detachUserFollowerCache();
        userFollowerCache.get().register(followerHeroRelationId, userFollowerListener);
        userFollowerCache.get().getOrFetchAsync(followerHeroRelationId);
    }

    protected String getDisplayName()
    {
        if (userFollowerDTO == null)
        {
            if (followerHeroRelationId == null)
            {
                return "";
            }
            else
            {
                return followerHeroRelationId.followerName;
            }
        }
        return userBaseDTOUtil.getLongDisplayName(getActivity(), userFollowerDTO);
    }

    public void display(UserFollowerDTO summaryDTO)
    {
        linkWith(summaryDTO, true);
    }

    private void showErrorView()
    {
        if (errorView != null)
        {
            errorView.setVisibility(View.VISIBLE);
        }
        if (followerPaymentListView != null)
        {
            followerPaymentListView.setVisibility(View.GONE);
        }
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
                totalRevenue.setText(String.format("%s %,.2f",
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                        userFollowerDTO.totalRevenue));
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

    private View.OnClickListener createUserClickHandler()
    {
        return new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (userFollowerDTO != null)
                {
                    Bundle bundle = new Bundle();
                    thRouter.save(bundle, new UserBaseKey(userFollowerDTO.id));
                    getDashboardNavigator().pushFragment(PushableTimelineFragment.class, bundle);
                }
            }
        };
    }

    protected DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO> createFollowerListener()
    {
        return new FollowerPayoutManagerFollowerListener();
    }

    protected class FollowerPayoutManagerFollowerListener implements DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO>
    {
        @Override public void onDTOReceived(FollowerHeroRelationId key, UserFollowerDTO value)
        {
            display(value);
        }

        @Override
        public void onErrorThrown(FollowerHeroRelationId key, Throwable error)
        {
            THToast.show(
                    "There was an error fetching your follower information");
            showErrorView();
        }
    }
}
