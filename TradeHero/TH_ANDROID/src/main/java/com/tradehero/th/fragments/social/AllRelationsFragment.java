package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.internal.util.Predicate;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.social.OnPremiumFollowRequestedListener;
import com.tradehero.th.models.user.follow.FollowUserAssistant;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener, HasSelectedItem
{
    List<AllowableRecipientDTO> mRelationsList;
    @Inject Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @Inject UserMessagingRelationshipCacheRx userMessagingRelationshipCache;
    @Inject Lazy<AdapterViewUtils> adapterViewUtils;

    @InjectView(R.id.sending_to_header) View sendingToHeader;

    private AllowableRecipientDTO selectedItem;

    private RelationsListItemAdapter mRelationsListItemAdapter;
    @InjectView(R.id.relations_list) ListView mRelationsListView;

    @Override
    protected FollowUserAssistant.OnUserFollowedListener createPremiumUserFollowedListener()
    {
        return new AllRelationsPremiumUserFollowedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_all_relations, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        mRelationsListItemAdapter = new RelationsListItemAdapter(
                getActivity(),
                R.layout.relations_list_item);
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(
                createFollowRequestedListener());
        mRelationsListView.setAdapter(mRelationsListItemAdapter);
        mRelationsListView.setOnItemClickListener(this);
        mRelationsListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        sendingToHeader.setVisibility(isForReturn() ? View.GONE : View.VISIBLE);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isForReturn() ? R.string.message_pick_relation : R.string.message_center_new_message_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        downloadRelations();
    }

    @Override public void onPause()
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        mRelationsListView.setAdapter(null);
        mRelationsListView.setOnItemClickListener(null);
        mRelationsListView.setOnScrollListener(null);
        mRelationsListView = null;
        mRelationsListItemAdapter.clear();
        mRelationsListItemAdapter.setPremiumFollowRequestedListener(null);
        mRelationsListItemAdapter = null;
        mRelationsList = null;
        super.onDestroyView();
    }

    @Nullable @Override public AllowableRecipientDTO getSelectedItem()
    {
        return selectedItem;
    }

    private boolean isForReturn()
    {
        Bundle args = getArguments();
        return args != null && args.containsKey(DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT);
    }

    public void downloadRelations()
    {
        alertDialogUtilLazy.get()
                .showProgressDialog(getActivity(), getString(R.string.downloading_relations));
        AndroidObservable.bindFragment(
                this,
                allowableRecipientPaginatedCache.get(new SearchAllowableRecipientListType(null, null, null)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createAllowableRecipientObserver());
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedItem = mRelationsList.get(position);
        if (isForReturn())
        {
            navigator.get().popFragment();
            return;
        }
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        Bundle args = new Bundle();
        NewPrivateMessageFragment.putCorrespondentUserBaseKey(args,
                mRelationsList.get(position).user.getBaseKey());
        navigator.get().pushFragment(NewPrivateMessageFragment.class, args);
    }

    protected void handleFollowRequested(UserBaseKey userBaseKey)
    {
        premiumFollowUser(userBaseKey);
    }

    protected Observer<Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO>>
    createAllowableRecipientObserver()
    {
        return new AllRelationAllowableRecipientCacheObserver();
    }

    protected class AllRelationAllowableRecipientCacheObserver
            implements Observer<Pair<
            SearchAllowableRecipientListType,
            PaginatedAllowableRecipientDTO>>
    {
        @Override public void onNext(Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> pair)
        {
            //mRelationsList = userProfileCompactCache.get(value.getData());
            mRelationsList = pair.second.getData();
            alertDialogUtilLazy.get().dismissProgressDialog();
            mRelationsListItemAdapter.addAll(mRelationsList);
            mRelationsListItemAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
            alertDialogUtilLazy.get().dismissProgressDialog();
        }
    }

    protected OnPremiumFollowRequestedListener createFollowRequestedListener()
    {
        return new AllRelationsFollowRequestedListener();
    }

    protected class AllRelationsFollowRequestedListener implements OnPremiumFollowRequestedListener
    {
        @Override public void premiumFollowRequested(@NonNull UserBaseKey userBaseKey)
        {
            handleFollowRequested(userBaseKey);
        }
    }

    protected class AllRelationsPremiumUserFollowedListener
            implements FollowUserAssistant.OnUserFollowedListener
    {
        @Override public void onUserFollowSuccess(
                @NonNull UserBaseKey userFollowed,
                @NonNull UserProfileDTO currentUserProfileDTO)
        {
            forceUpdateLook(userFollowed);
        }

        @Override public void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error)
        {
            // nothing for now
        }
    }

    protected void forceUpdateLook(@NonNull final UserBaseKey userFollowed)
    {
        AndroidObservable.bindFragment(
                this,
                userMessagingRelationshipCache.get(userFollowed))
                .subscribe(new Observer<Pair<UserBaseKey, UserMessagingRelationshipDTO>>()
                {
                    private boolean isEmpty = true;

                    @Override public void onCompleted()
                    {
                        if (isEmpty)
                        {
                            allowableRecipientPaginatedCache.invalidateAll();
                            downloadRelations();
                            Timber.e("Strangely, there was no longer the relation in cache");
                        }
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<UserBaseKey, UserMessagingRelationshipDTO> pair)
                    {
                        isEmpty = false;
                        mRelationsListItemAdapter.updateItem(userFollowed, pair.second);
                        adapterViewUtils.get()
                                .updateSingleRowWhere(mRelationsListView, AllowableRecipientDTO.class, new Predicate<AllowableRecipientDTO>()
                                {
                                    @Override public boolean apply(AllowableRecipientDTO allowableRecipientDTO)
                                    {
                                        return allowableRecipientDTO != null
                                                && allowableRecipientDTO.user.getBaseKey().equals(userFollowed);
                                    }
                                });
                    }
                });
    }
}