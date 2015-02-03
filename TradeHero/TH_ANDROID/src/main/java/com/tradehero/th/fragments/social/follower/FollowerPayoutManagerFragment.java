package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.social.UserFollowerCacheRx;
import com.tradehero.th.utils.SecurityUtils;
import com.tradehero.th.utils.route.THRouter;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

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
    @Inject protected Lazy<UserFollowerCacheRx> userFollowerCache;
    @Inject THRouter thRouter;

    public static void put(@NonNull Bundle args, @NonNull FollowerHeroRelationId followerHeroRelationId)
    {
        args.putBundle(
                BUNDLE_KEY_FOLLOWER_ID_BUNDLE,
                followerHeroRelationId.getArgs());
    }

    public static FollowerHeroRelationId getFollowerHeroRelationId(@NonNull Bundle args)
    {
        return new FollowerHeroRelationId(args.getBundle(BUNDLE_KEY_FOLLOWER_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        followerPaymentListAdapter = new FollowerPaymentListItemAdapter(
                getActivity(), R.layout.follower_payment_list_item, R.layout.follower_payment_list_header);
        followerHeroRelationId = getFollowerHeroRelationId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store_manage_follower_revenue, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        followerPaymentListView.setAdapter(followerPaymentListAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchFollowerSummary();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        followerPaymentListAdapter = null;
        super.onDestroy();
    }

    protected void fetchFollowerSummary()
    {
        AppObservable.bindFragment(this, userFollowerCache.get().get(followerHeroRelationId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createFollowerObserver());
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
        return UserBaseDTOUtil.getShortDisplayName(getActivity(), userFollowerDTO);
    }

    public void display(UserFollowerDTO summaryDTO)
    {
        this.userFollowerDTO = summaryDTO;
        display();
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

    protected Observer<Pair<FollowerHeroRelationId, UserFollowerDTO>> createFollowerObserver()
    {
        return new FollowerPayoutManagerFollowerObserver();
    }

    protected class FollowerPayoutManagerFollowerObserver implements Observer<Pair<FollowerHeroRelationId, UserFollowerDTO>>
    {
        @Override public void onNext(Pair<FollowerHeroRelationId, UserFollowerDTO> pair)
        {
            display(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(
                    "There was an error fetching your follower information");
            showErrorView();
        }
    }
}
