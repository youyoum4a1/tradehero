package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.social.UserFollowerCache;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class FollowerPayoutManagerFragment extends BasePurchaseManagerFragment
{
    private static final String BUNDLE_KEY_FOLLOWER_ID_BUNDLE =
            FollowerPayoutManagerFragment.class.getName() + ".followerId";

    @InjectView(R.id.follower_profile_picture) ImageView followerPicture;
    @InjectView(R.id.follower_title) TextView followerName;
    @InjectView(R.id.follower_revenue) TextView totalRevenue;
    @InjectView(R.id.follower_payments_list) ListView followerPaymentListView;
    @InjectView(R.id.error_view) View errorView;

    private FollowerPaymentListItemAdapter followerPaymentListAdapter;
    private FollowerHeroRelationId followerHeroRelationId;
    private UserFollowerDTO userFollowerDTO;

    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<UserFollowerCache> userFollowerCache;
    private DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO> userFollowerListener;
    @Inject UserBaseDTOUtil userBaseDTOUtil;
    @Inject THRouter thRouter;

    public static void put(@NotNull Bundle args, @NotNull FollowerHeroRelationId followerHeroRelationId)
    {
        args.putBundle(
                BUNDLE_KEY_FOLLOWER_ID_BUNDLE,
                followerHeroRelationId.getArgs());
    }

    public static FollowerHeroRelationId getFollowerHeroRelationId(@NotNull Bundle args)
    {
        return new FollowerHeroRelationId(args.getBundle(BUNDLE_KEY_FOLLOWER_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userFollowerListener = createFollowerListener();
        followerPaymentListAdapter = new FollowerPaymentListItemAdapter(
                getActivity(), getActivity().getLayoutInflater(),
                R.layout.follower_payment_list_item, R.layout.follower_payment_list_header);
        followerHeroRelationId = getFollowerHeroRelationId(getArguments());
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
        ButterKnife.inject(this, view);
        followerPaymentListView.setAdapter(followerPaymentListAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchFollowerSummary();
    }

    @Override public void onStop()
    {
        detachUserFollowerCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        followerPaymentListAdapter = null;
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
        return userBaseDTOUtil.getShortDisplayName(getActivity(), userFollowerDTO);
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
        setActionBarTitle(getDisplayName());
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

    @OnClick({R.id.follower_profile_picture, R.id.follower_title})
    public void onClick(View v)
    {
        if (userFollowerDTO != null)
        {
            Bundle bundle = new Bundle();
            thRouter.save(bundle, new UserBaseKey(userFollowerDTO.id));
            navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
        }
    }

    protected DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO> createFollowerListener()
    {
        return new FollowerPayoutManagerFollowerListener();
    }

    protected class FollowerPayoutManagerFollowerListener implements DTOCacheNew.Listener<FollowerHeroRelationId, UserFollowerDTO>
    {
        @Override public void onDTOReceived(@NotNull FollowerHeroRelationId key, @NotNull UserFollowerDTO value)
        {
            display(value);
        }

        @Override
        public void onErrorThrown(@NotNull FollowerHeroRelationId key, @NotNull Throwable error)
        {
            THToast.show(
                    "There was an error fetching your follower information");
            showErrorView();
        }
    }
}
